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

    public byte[] decode() {
        return null;
    }

    public int outInt() {
        System.out.println("position - " + container.position());
        final int result = container.getInt(position);
        position += 4;
        container.position(position);
        return result;
    }

    public long outLong() {
        System.out.println("position - " + container.position());
        final long result = container.getLong(position);
        position += 8;
        container.position(position);
        return result;
    }
}
