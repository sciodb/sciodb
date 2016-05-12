package org.sciodb.server;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

/**
 * @author jesus.navarrete  (24/02/16)
 */
public class ServerSocket implements Runnable {

    final static private Logger logger = Logger.getLogger(ServerSocket.class);

    private Selector selector;
    private Map<SocketChannel, List> dataMapper;
    private InetSocketAddress listenAddress;

    private SocketsThreadPool pool;

    public ServerSocket(final String address, final int port) throws IOException {
        listenAddress = new InetSocketAddress(address, port);
        dataMapper = new HashMap<>();

        pool = SocketsThreadPool.getInstance();
    }

    @Override
    public void run() {
        try {
            selector = Selector.open();
            final ServerSocketChannel serverChannel = ServerSocketChannel.open();
            serverChannel.configureBlocking(false);

            // retrieve server socket and bind to port
            serverChannel.socket().bind(listenAddress);
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

            logger.info("Server started...");

            while (true) {
                // wait for events
                selector.select();

                //work on selected keys
                final Iterator keys = selector.selectedKeys().iterator();
                while (keys.hasNext()) {
                    final SelectionKey key = (SelectionKey) keys.next();

                    // this is necessary to prevent the same key from coming up
                    // again the next time around.
                    keys.remove();

                    if (!key.isValid()) {
                        continue;
                    }

                    if (key.isAcceptable()) {
                        accept(key);
                    } else if (key.isReadable()) {
                        pool.run(key);
                    } else if (key.isWritable()) {
                        key.interestOps(SelectionKey.OP_READ);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Problems with the socket server", e);
        }
    }

    //accept a connection made to this channel's socket
    private void accept(final SelectionKey key) throws IOException {
        final ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        final SocketChannel channel = serverChannel.accept();
        channel.configureBlocking(false);

        final Socket socket = channel.socket();
        final SocketAddress remoteAddr = socket.getRemoteSocketAddress();
        logger.info("Connected to: " + remoteAddr);

        // register channel with selector for further IO
        dataMapper.put(channel, new ArrayList());
        channel.register(selector, SelectionKey.OP_READ);
    }

}
