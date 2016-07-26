package org.sciodb.server;

import org.apache.log4j.Logger;
import org.sciodb.utils.ByteUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author jesus.navarrete  (24/02/16)
 */
public class ServerSocket implements Runnable {

    final static private Logger logger = Logger.getLogger(ServerSocket.class);

    // The host:port combination to listen on
    private InetAddress hostAddress;
    private int port;

    // The channel on which we'll accept connections
    private ServerSocketChannel serverChannel;

    // The selector we'll be monitoring
    private Selector selector;

    private ExecutorService service;

    // A list of PendingChange instances
    private List pendingChanges = new LinkedList();

    // Maps a SocketChannel to a list of ByteBuffer instances
    private Map pendingData = new HashMap();

    private SocketsThreadPool pool;

    public ServerSocket(final InetAddress hostAddress, final int port) throws IOException {
        this.hostAddress = hostAddress;
        this.port = port;
        this.selector = this.initSelector();
        this.service = Executors.newFixedThreadPool(100);

        pool = SocketsThreadPool.getInstance();
    }

    public void send(final SocketChannel socket, final byte[] data) {
        synchronized (this.pendingChanges) {
            // Indicate we want the interest ops set changed
            this.pendingChanges.add(new ChangeRequest(socket, ChangeRequest.CHANGEOPS, SelectionKey.OP_WRITE));

            // And queue the data we want written
            synchronized (this.pendingData) {
                List queue = (List) this.pendingData.get(socket);
                if (queue == null) {
                    queue = new ArrayList();
                    this.pendingData.put(socket, queue);
                }
                queue.add(ByteBuffer.wrap(data));
            }
        }

        // Finally, wake up our selecting thread so it can make the required changes
        this.selector.wakeup();
    }

    @Override
    public void run() {
        while (true) {
            try {
                // Process any pending changes
                synchronized (this.pendingChanges) {
                    Iterator changes = this.pendingChanges.iterator();
                    while (changes.hasNext()) {
                        ChangeRequest change = (ChangeRequest) changes.next();
                        switch (change.type) {
                            case ChangeRequest.CHANGEOPS:
                                SelectionKey key = change.socket.keyFor(this.selector);
                                key.interestOps(change.ops);
                        }
                    }
                    this.pendingChanges.clear();
                }

                // Wait for an event one of the registered channels
                this.selector.select();

                // Iterate over the set of keys for which events are available
                Iterator selectedKeys = this.selector.selectedKeys().iterator();
                while (selectedKeys.hasNext()) {
                    SelectionKey key = (SelectionKey) selectedKeys.next();
                    selectedKeys.remove();

                    if (!key.isValid()) {
                        continue;
                    }

                    // Check what event is available and deal with it
                    if (key.isAcceptable()) {
                        this.accept(key);
                    } else if (key.isReadable()) {
                        this.read(key);
                    } else if (key.isWritable()) {
                        this.write(key);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void accept(SelectionKey key) throws IOException {
        // For an accept to be pending the channel must be a server socket channel.
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();

        // Accept the connection and make it non-blocking
        SocketChannel socketChannel = serverSocketChannel.accept();
//		Socket socket = socketChannel.socket();
        socketChannel.configureBlocking(false);

        // Register the new SocketChannel with our Selector, indicating
        // we'd like to be notified when there's data waiting to be read
        socketChannel.register(this.selector, SelectionKey.OP_READ);
    }

    final static private int HEADER_SIZE = 4;

    private void read(final SelectionKey key) throws IOException {
        try {
            final byte[] size = read(key, HEADER_SIZE, false);
            int msgSize;

            if (size != null && size.length == HEADER_SIZE) {
                final String msg = new String(size);
                msgSize = Integer.valueOf(msg);

                if (msgSize == 0) {
                    key.cancel();
                } else {
                    final byte[] result = read(key, msgSize, true);
                    final SocketChannel channel = (SocketChannel) key.channel();

//                    final Worker worker = new Worker(this, channel, result);
//                    service.execute(worker);
                    pool.run(this, channel, result);
                }
            }

        } catch (Exception e) {
            logger.error("There was an error reading the buffer: " + e.getLocalizedMessage(), e);
        }


    }

    private byte[] read(final SelectionKey key, final int msgSize, final boolean cancelation) throws IOException {
        final SocketChannel channel = (SocketChannel) key.channel();

        final ByteBuffer buffer = ByteBuffer.allocate(msgSize);

        int total = 0;
        boolean empty = false;
        if (msgSize > 0) {
            while (total < msgSize) {

                final ByteBuffer messageBuffer = ByteBuffer.allocate(msgSize);
                int currentSize = channel.read(messageBuffer);

                if (currentSize == -1) {
                    if (cancelation) {
                        channel.close();
                        key.cancel();
                    }
                    empty = true;
                    break;
                }

                byte[] data = ByteUtils.newArray(currentSize);
                System.arraycopy(messageBuffer.array(), 0, data, total, currentSize);
                total += currentSize;

                buffer.put(data);

            }
            if (!empty) {
                logger.debug("total : " + total + " - msgSize : " + msgSize);
            }
        }
        if (total == msgSize) {
            return buffer.array();
        } else {
            return new byte[0];
        }
    }

    private void write(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        synchronized (this.pendingData) {
            List queue = (List) this.pendingData.get(socketChannel);

            // Write until there's not more data ...
            while (!queue.isEmpty()) {
                ByteBuffer buf = (ByteBuffer) queue.get(0);
                socketChannel.write(buf);
                if (buf.remaining() > 0) {
                    // ... or the socket's buffer fills up
                    break;
                }
                queue.remove(0);
            }

            if (queue.isEmpty()) {
                // We wrote away all data, so we're no longer interested
                // in writing on this socket. Switch back to waiting for
                // data.
                key.interestOps(SelectionKey.OP_READ);
            }
        }
    }

    private Selector initSelector() throws IOException {
        // Create a new selector
        Selector socketSelector = SelectorProvider.provider().openSelector();

        // Create a new non-blocking server socket channel
        this.serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);

        // Bind the server socket to the specified address and port
        InetSocketAddress isa = new InetSocketAddress(this.hostAddress, this.port);
        serverChannel.socket().bind(isa);

        // Register the server socket channel, indicating an interest in
        // accepting new connections
        serverChannel.register(socketSelector, SelectionKey.OP_ACCEPT);

        return socketSelector;
    }

    public static void main(String[] args) {
        try {
//			final Worker worker = new Worker();
//			new Thread(worker).start();
            new Thread(new ServerSocket(null, 9090)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
