package org.sciodb.messages;

import org.sciodb.utils.ByteUtils;

import java.nio.ByteBuffer;

/**
 * @author Jesús Navarrete (23/04/16)
 */
public class Decoder {

    private ByteBuffer container;
    private int position;

    public Decoder(final byte[] input) {
        container = ByteBuffer.wrap(input);
        position = 0;
    }

    public int getInt() {
        if (position >= container.limit()) return 0;
        final int result = container.getInt(position);
        position += Encoder.INT_BYTES;
        container.position(position);

        return result;
    }

    public long getLong() {
        if (position >= container.limit()) return 0;
        final long result = container.getLong(position);
        position += Encoder.LONG_BYTES;
        container.position(position);

        return result;
    }

    public String getString() {
        final int size = getInt();

        final byte[] data = ByteUtils.newArray(size);
        container.get(data);

        final String result = new String(data);
        position += result.length();
        container.position(position);

        return result;
    }

    public byte getByte() {
        position += 1;
        container.position(position);

        return container.get();
    }

    public byte[] getByteArray() {
        int length = this.getInt();
        byte[] dst = ByteUtils.newArray(length);
        container.get(dst);

        position += length;
        container.position(position);

        return dst;
    }
}
