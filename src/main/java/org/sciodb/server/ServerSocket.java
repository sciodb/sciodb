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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.sciodb.utils.ScioDBConstants.HEADER_SIZE;

/**
 * @author Jesús Navarrete (24/02/16)
 */
public class ServerSocket implements Runnable {

    final static private Logger logger = Logger.getLogger(ServerSocket.class);

    private final InetAddress hostAddress;
    private final int port;

    // The selector we'll be monitoring
    private Selector selector;

    // A list of PendingChange instances
    final private List<ChangeRequest> pendingChanges = new LinkedList<>();

    // Maps a SocketChannel to a list of ByteBuffer instances
    final private Map<SocketChannel, List<ByteBuffer>> pendingData = new HashMap<>();

    private SocketsThreadPool pool;

    public ServerSocket(final InetAddress hostAddress, final int port) throws IOException {
        this.hostAddress = hostAddress;
        this.port = port;
        this.selector = this.initSelector();

        pool = SocketsThreadPool.getInstance();
    }

    public void send(final SocketChannel socket, final byte[] data) {
        synchronized (this.pendingChanges) {
            // Indicate we want the interest ops set changed
            this.pendingChanges.add(new ChangeRequest(socket, ChangeRequest.CHANGE_OPS, SelectionKey.OP_WRITE));

            // And queue the data we want written
            synchronized (this.pendingData) {
                final List<ByteBuffer> queue = this.pendingData.computeIfAbsent(socket, k -> new ArrayList<>());

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
                    for (ChangeRequest change : this.pendingChanges) {
                        if (change.type == ChangeRequest.CHANGE_OPS) {
                            final SelectionKey key = change.socket.keyFor(this.selector);
                            key.interestOps(change.ops);
                        }
                    }
                    this.pendingChanges.clear();
                }

                // Wait for an event one of the registered channels
                this.selector.select();

                // Iterate over the set of keys for which events are available
                final Iterator<SelectionKey> selectedKeys = this.selector.selectedKeys().iterator();
                while (selectedKeys.hasNext()) {
                    final SelectionKey key = selectedKeys.next();
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
                logger.error("Problems with the socket server", e);
            }
        }
    }

    private void accept(final SelectionKey key) throws IOException {
        final ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();

        final SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);

        socketChannel.register(this.selector, SelectionKey.OP_READ);
    }


    private void read(final SelectionKey key) {
        try {
            final byte[] size = read(key, HEADER_SIZE, false);
            int msgSize;

            if (size.length == HEADER_SIZE) {
                final String msg = new String(size);
                msgSize = Integer.parseInt(msg);

                if (msgSize == 0) {
                    key.cancel();
                } else {
                    final byte[] result = read(key, msgSize, true);
                    final SocketChannel channel = (SocketChannel) key.channel();

                    pool.run(this, channel, result);
                }
            }

        } catch (Exception e) {
            logger.error("There was an error reading the buffer: " + e.getLocalizedMessage(), e);
        }

    }

    private byte[] read(final SelectionKey key, final int msgSize, final boolean cancellation) throws IOException {
        final SocketChannel channel = (SocketChannel) key.channel();

        final ByteBuffer buffer = ByteBuffer.allocate(msgSize);

        int total = 0;
        if (msgSize > 0) {
            while (total < msgSize) {

                final ByteBuffer messageBuffer = ByteBuffer.allocate(msgSize);
                int currentSize = channel.read(messageBuffer);

                if (currentSize == -1) {
                    if (cancellation) {
                        channel.close();
                        key.cancel();
                    }
                    break;
                }

                byte[] data = ByteUtils.newArray(currentSize);
                System.arraycopy(messageBuffer.array(), 0, data, total, currentSize);
                total += currentSize;

                buffer.put(data);

            }
        }
        if (total == msgSize) {
            return buffer.array();
        } else {
            return new byte[0];
        }
    }

    private void write(final SelectionKey key) throws IOException {
        final SocketChannel socketChannel = (SocketChannel) key.channel();

        synchronized (this.pendingData) {
            final List queue = this.pendingData.get(socketChannel);

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
        final Selector socketSelector = SelectorProvider.provider().openSelector();

        final ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);

        final InetSocketAddress isa = new InetSocketAddress(this.hostAddress, this.port);
        serverChannel.socket().bind(isa);

        serverChannel.register(socketSelector, SelectionKey.OP_ACCEPT);

        return socketSelector;
    }

}
