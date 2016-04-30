package org.sciodb.messages;

import java.nio.ByteBuffer;

/**
 * @author jenaiz on 23/04/16.
 */
public class Decoder {

    private ByteBuffer container;
    private int position;

    public Decoder(final byte[] input) {
        container = ByteBuffer.wrap(input);
        position = 0;
    }

    public int outInt() {
        final int result = container.getInt(position);
        position += 4;
        container.position(position);

        return result;
    }

    public long outLong() {
        final long result = container.getLong(position);
        position += 8;
        container.position(position);

        return result;
    }

    public String outString() {
        final int size = outInt();

        final byte[] data = new byte[size];
        container.get(data);

        final String result = new String(data);
        position += result.length();
        container.position(position);

        return result;
    }
}
