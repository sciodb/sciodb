package org.sciodb.server;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * @author jesus.navarrete  (07/04/16)
 */
public class MessageReader {

    final static private Logger logger = Logger.getLogger(MessageReader.class);

    final static private int HEADER_SIZE = 4;

    final private SelectionKey key;

    private boolean closeChannel;

    public MessageReader(final SelectionKey key) {
        this.key = key;
    }

    public String getContent() {
        String result = null;
        try {
            final SocketChannel channel = (SocketChannel) key.channel();
            final String size = readMessage(channel, HEADER_SIZE);
            int msgSize;
            if (size != null && size.length() == 4) {
                msgSize = Integer.valueOf(size);

                if (msgSize == 0) {
                    key.cancel();
                } else {
                    result = readMessage(channel, msgSize);
                }
            }

        } catch (Exception e) {
            logger.error("There was an error reading the buffer: " + e.getLocalizedMessage());
        }
        return result;
    }

    private String readMessage(final SocketChannel channel, int msgSize) throws IOException {
        final StringBuilder result = new StringBuilder();
        int total = 0;
        boolean empty = false;
        if (msgSize > 0) {
            while (total < msgSize) {

                final ByteBuffer messageBuffer = ByteBuffer.allocate(msgSize);
                int currentSize = channel.read(messageBuffer);

                if (currentSize == -1) {
                    Socket socket = channel.socket();
                    SocketAddress remoteAddr = socket.getRemoteSocketAddress();
                    logger.debug("Connection closed by client: " + remoteAddr);
                    channel.close();
                    key.cancel();
                    empty = true;
                    closeChannel = true;
                    break;
                }

                byte [] data = new byte[currentSize];
                System.arraycopy(messageBuffer.array(), 0, data, total, currentSize);
                total += currentSize;

                final String str = new String(data);
                if (str.length() < msgSize && str.length() != 0) {
                    logger.debug("Got: " + str);
                }
                result.append(str);

            }
            if (!empty) {
                logger.debug("total : " + total + " - msgSize : " + msgSize);
            }
        }
        return result.toString();
    }

    public void close() {
        try {
            if (closeChannel) {
                final SocketChannel channel = (SocketChannel) key.channel();
                channel.close();
                key.cancel();
            }
        } catch (final IOException e) {
            logger.error("Error closing channel socket", e);
        }
    }

}
