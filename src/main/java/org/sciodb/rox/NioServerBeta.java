package org.sciodb.rox;

import org.apache.log4j.Logger;
import org.sciodb.messages.Operations;
import org.sciodb.messages.impl.ContainerMessage;
import org.sciodb.messages.impl.Node;
import org.sciodb.messages.impl.NodeMessage;
import org.sciodb.messages.impl.NodesMessage;
import org.sciodb.server.services.Dispatcher;
import org.sciodb.topology.TopologyContainer;
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

public class NioServerBeta implements Runnable {

    final static private Logger logger = Logger.getLogger(NioServerBeta.class);

    // The host:port combination to listen on
	private InetAddress hostAddress;
	private int port;

	// The channel on which we'll accept connections
	private ServerSocketChannel serverChannel;

	// The selector we'll be monitoring
	private Selector selector;

	// The buffer into which we'll read data when it's available
//	private ByteBuffer readBuffer = ByteBuffer.allocate(8192);

//	private Worker worker;
    private ExecutorService service;
	private Dispatcher dispatcher;


	// A list of PendingChange instances
	private List pendingChanges = new LinkedList();

	// Maps a SocketChannel to a list of ByteBuffer instances
	private Map pendingData = new HashMap();

	public NioServerBeta(InetAddress hostAddress, int port) throws IOException {
		this.hostAddress = hostAddress;
		this.port = port;
		this.selector = this.initSelector();
//		this.worker = worker;
		this.dispatcher = new Dispatcher();
        this.service = Executors.newFixedThreadPool(100);
    }

	public void send(SocketChannel socket, byte[] data) {
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
//        final byte[] result;
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
//                    channel.register(key.selector(), SelectionKey.OP_WRITE);
					final SocketChannel channel = (SocketChannel) key.channel();
					//        this.worker.processData(this, channel, result);
					service.execute(() -> execute(this, channel, result));
                }
            }

        } catch (Exception e) {
            logger.error("There was an error reading the buffer: " + e.getLocalizedMessage(), e);
        }


    }
	private void execute(final NioServerBeta server, final SocketChannel channel, final byte[] input) {
		if (input != null && input.length > 0) {
			final ContainerMessage request = new ContainerMessage();
			request.decode(input);
			int operationId = request.getHeader().getOperationId();
			if (operationId == Operations.DISCOVERY_PEERS.getValue()) {
				final NodeMessage nodeMessage = new NodeMessage();
				nodeMessage.decode(request.getContent());

				TopologyContainer.getInstance().addNode(nodeMessage.getNode());

				final Queue<Node> nodes = TopologyContainer.getInstance().getAvailableNodes();
				final NodesMessage n = new NodesMessage();
				for (Node nn : nodes) {
					n.getNodes().add(nn);
				}
				final ContainerMessage response = new ContainerMessage();
				response.getHeader().setId(UUID.randomUUID().toString());
				response.getHeader().setOperationId(Operations.DISCOVERY_PEERS.getValue());

				response.setContent(n.encode());

				server.send(channel, response.encode());
			} else if (operationId == Operations.STATUS.getValue()) {
//                nioServer.send(socketChannel, new byte[0]);
			} else if (request.getHeader().getOperationId() == Operations.ECHO.getValue()) {

				final byte [] response = dispatcher.getService(request);

				server.send(channel, response);
			}
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

                byte [] data = ByteUtils.newArray(currentSize);
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
			new Thread(new NioServerBeta(null, 9090)).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
