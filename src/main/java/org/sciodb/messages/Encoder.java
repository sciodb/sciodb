package org.sciodb.messages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jesús Navarrete (23/04/16)
 *
 */
public class Encoder {

    final static int INT_BYTES = 4;
    final static int LONG_BYTES = 8;

    private final List<ByteBuffer> container;

    public Encoder() {
        this.container = new ArrayList<>();
    }

    public void in(final long l) {
        final ByteBuffer bb = ByteBuffer.allocate(LONG_BYTES)
                                        .order(ByteOrder.BIG_ENDIAN)
                                        .putLong(l);
//        container.add(ByteBuffer.allocate(1).putInt(1)); // idea to mark the type
        container.add(bb);
    }

    public void in(final int i) {
        final ByteBuffer bb = ByteBuffer.allocate(INT_BYTES)
                                        .order(ByteOrder.BIG_ENDIAN)
                                        .putInt(i);
        container.add(bb);
    }

    public void in(final String s) {
        final ByteBuffer bb = ByteBuffer.allocate(s.length())
                                        .order(ByteOrder.BIG_ENDIAN)
                                        .put(s.getBytes());
        in(s.length());
        container.add(bb);
    }

    public void in(final byte[] b) {
        final ByteBuffer bb = ByteBuffer.allocate(b.length)
                .order(ByteOrder.BIG_ENDIAN)
                .put(b);
        in(b.length);
        container.add(bb);
    }

    public void add(final byte[] input) {
        in(input.length);
        container.add(ByteBuffer.wrap(input));
    }

    public byte[] container() {
        try {
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            for (ByteBuffer bb : container) {
                outputStream.write(bb.array());
            }

            return outputStream.toByteArray();
        } catch (IOException e) {
            return new byte[0];
        }
    }

}
