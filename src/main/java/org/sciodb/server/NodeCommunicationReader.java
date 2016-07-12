package org.sciodb.server;

import org.apache.log4j.Logger;
import org.sciodb.utils.ByteUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * @author jesus.navarrete  (30/05/16)
 */
public class NodeCommunicationReader {

    final static private Logger logger = Logger.getLogger(NodeCommunicationReader.class);

    final static private int HEADER_SIZE = 4;

    final private SelectionKey key;

    private boolean closeChannel;

    public NodeCommunicationReader(final SelectionKey key) {
        this.key = key;
    }

    public byte[] getMessage() {
        byte[] result = null;
        try {
            final SocketChannel channel = (SocketChannel) key.channel();
            final byte[] size = readMessage(channel, HEADER_SIZE);
            int msgSize;

            if (size != null && size.length == HEADER_SIZE) {
                final String msg = new String(size);
                msgSize = Integer.valueOf(msg);

                if (msgSize == 0) {
                    key.cancel();
                } else {
                    result = readMessage(channel, msgSize);
                    channel.register(key.selector(), SelectionKey.OP_WRITE);
                }
            }

        } catch (Exception e) {
            logger.error("There was an error reading the buffer: " + e.getLocalizedMessage());
        }
        return result;
    }

    private byte[] readMessage(final SocketChannel channel, int msgSize) throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocate(msgSize);

        int total = 0;
        boolean empty = false;
        if (msgSize > 0) {
            while (total < msgSize) {

                final ByteBuffer messageBuffer = ByteBuffer.allocate(msgSize);
                int currentSize = channel.read(messageBuffer);

                if (currentSize == -1) {
                    channel.close();
                    key.cancel();
                    empty = true;
                    closeChannel = true;
                    break;
                }

                byte [] data = ByteUtils.newArray(currentSize);
                System.arraycopy(messageBuffer.array(), 0, data, total, currentSize);
                total += currentSize;

                final String str = new String(data);
                if (str.length() < msgSize && str.length() != 0) {
                    logger.debug("Got: " + str);
                }

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

    public SelectionKey getKey() {
        return key;
    }
}
