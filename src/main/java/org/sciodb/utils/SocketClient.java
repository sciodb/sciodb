package org.sciodb.utils;

import org.apache.log4j.Logger;
import org.sciodb.exceptions.CommunicationException;
import org.sciodb.messages.Message;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author jesus.navarrete  (08/06/16)
 */
public class SocketClient {

    final public static int MAX_ANSWER_BYTES = 1024;

    final static private Logger logger = Logger.getLogger(SocketClient.class);

    public static byte[] sendToSocket(final String host, final int port, final Message message, final boolean responseRequired) throws CommunicationException {

        final byte[] input = message.encode();

        final InetSocketAddress hostAddress = new InetSocketAddress(host, port);

        long init = System.currentTimeMillis();

        SocketChannel client = null;
        try {
            client = SocketChannel.open(hostAddress);
            final ByteBuffer buffer = ByteBuffer.wrap(input);

            client.write(messageLength(input.length));
            client.write(buffer);

            buffer.clear();
            byte[] data;

            if (responseRequired) {
                final ByteBuffer response = ByteBuffer.allocate(MAX_ANSWER_BYTES);

                int currentSize = client.read(response);
                data = ByteUtils.newArray(currentSize);
                System.arraycopy(response.array(), 0, data, 0, currentSize);
            } else {
                data = new byte[0];
            }
            client.close();
            return data;

        } catch (IOException e) {
            try { if (client != null) client.close(); } catch (IOException e1) {}

            throw new CommunicationException("Error connecting with node " + host + ":" + port, e);
        } finally {
            long end = System.currentTimeMillis() - init;

            logger.debug(" Connection [" + host + ":" + port + "] took " + end + "ms");
        }
    }

    public static ByteBuffer messageLength(final int length) {
        final String headerSize = String.format("%04d", length);
        final ByteBuffer header = ByteBuffer.wrap(headerSize.getBytes());

        return header;
    }

}
