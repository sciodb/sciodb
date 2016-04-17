package org.sciodb.server;

import org.apache.log4j.Logger;
import org.sciodb.server.services.Dispatcher;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
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

    final static private int HEADER_SIZE = 4;
    private Selector selector;
    private Map<SocketChannel, List> dataMapper;
    private InetSocketAddress listenAddress;

    private Dispatcher dispatcher;

    public ServerSocket(String address, int port) throws IOException {
        listenAddress = new InetSocketAddress(address, port);
        dataMapper = new HashMap<>();

        dispatcher = new Dispatcher();
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
                    }
                    else if (key.isReadable()) {
                        readBuffer(key);
                    } else if (key.isWritable()) {

                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer bb1 = ByteBuffer.allocate(10000);

                        String s = "server data";
                        byte[] array1 = s.getBytes();
                        bb1.put(array1);
                        bb1.flip();
                        client.write(bb1);
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

    //read from the socket channel OLD VERSION
    private void read(final SelectionKey key) throws IOException {
        logger.info("size of the pool --> " + dataMapper.size());
        try {

            final SocketChannel channel = (SocketChannel) key.channel();

            final List<String> messages = new ArrayList<>();

            while (true) {
                final ByteBuffer buffer = ByteBuffer.allocate(HEADER_SIZE);
                int bufferSize = channel.read(buffer);

                if (bufferSize == -1) {
                    dataMapper.remove(channel);
                    Socket socket = channel.socket();
                    SocketAddress remoteAddr = socket.getRemoteSocketAddress();
                    logger.info("Connection closed by client: " + remoteAddr);
                    channel.close();
                    key.cancel();
                    return;
                }

                byte[] data = new byte[bufferSize];
                System.arraycopy(buffer.array(), 0, data, 0, bufferSize);

                final String msg = new String(data);

                if (msg != null && msg.length() > 0) {
                    logger.info("Size: " + msg);
                    int size = Integer.valueOf(msg);
                    final ByteBuffer messageBuffer = ByteBuffer.allocate(size);
                    bufferSize = channel.read(messageBuffer);

                    if (bufferSize == -1) {
                        dataMapper.remove(channel);
                        Socket socket = channel.socket();
                        SocketAddress remoteAddr = socket.getRemoteSocketAddress();
                        logger.info("Connection closed by client: " + remoteAddr);
                        channel.close();
                        key.cancel();
                        return;
                    }

                    data = new byte[bufferSize];
                    System.arraycopy(messageBuffer.array(), 0, data, 0, bufferSize);

                    logger.info("Got: " + new String(data));
                    messages.add(new String(data));
                }

            }
        } catch (Exception e) {
            logger.error("There was an error reading the buffer: " + e.getLocalizedMessage());
        }


//
//        final Command command = CommandEncoder.decode(""); //new String(data));
//
//        // do it asynchronously
//        if (command != null && command.getOperationID() != null) {
//            dispatcher.getService(command);
//        }

    }

    public void readBuffer(final SelectionKey key) {
        try {
            final SocketChannel channel = (SocketChannel) key.channel();

                int msgSize = readHeader(channel);

                if (msgSize == 0) {
                    key.cancel();
                } else {
                    final String message = readMessage(channel, msgSize);
//                    if (msgSize != message.length()) {
//                        logger.info("Size: " + msgSize);
//
//                    }
//                        logger.info(" message --> " + message);
                }

        } catch (Exception e) {
            logger.error("There was an error reading the buffer: " + e.getLocalizedMessage());
        }
    }

    private int readHeader(final SocketChannel channel) {
        final ByteBuffer buffer = ByteBuffer.allocate(HEADER_SIZE);
        int bufferSize = 0;
        try {
            bufferSize = channel.read(buffer);

            if (bufferSize == -1) {
                dataMapper.remove(channel);
                final Socket socket = channel.socket();
                final SocketAddress remoteAddr = socket.getRemoteSocketAddress();
                logger.info("Connection closed by client: " + remoteAddr);
                channel.close();
                bufferSize = 0;
            } else {
                final byte[] data = new byte[bufferSize];
                System.arraycopy(buffer.array(), 0, data, 0, bufferSize);

                final String msg = new String(data);

                if (msg != null && msg.length() > 0) {
//                    logger.info("Size: " + msg);
                    bufferSize = Integer.valueOf(msg);
                }
            }
        } catch (IOException e) {
            logger.error("Error reading socket channel", e);
        }
        return bufferSize;

    }

    private String readMessage(final SocketChannel channel, int msgSize) throws IOException {
        final StringBuilder result = new StringBuilder();
        int total = 0;
        if (msgSize > 0) {
            boolean repeat = true;
            int counter = 0;
            while (repeat) {

                final ByteBuffer messageBuffer = ByteBuffer.allocate(msgSize);
                int currentSize = channel.read(messageBuffer);

                if (currentSize == -1) {
                    dataMapper.remove(channel);
                    Socket socket = channel.socket();
                    SocketAddress remoteAddr = socket.getRemoteSocketAddress();
                    logger.info("Connection closed by client: " + remoteAddr);
                    channel.close();
//                    key.cancel();
//                    return;
                }

                byte [] data = new byte[currentSize];
                System.arraycopy(messageBuffer.array(), 0, data, total, currentSize);
                total += currentSize;

                final String str = new String(data);
//                if (str.length() < msgSize) {
//                    logger.info("Got: " + str);
//                }
                result.append(str);
                if (total == msgSize) {
                    repeat = false;
                }

            }
        }
        return result.toString();
    }

}
