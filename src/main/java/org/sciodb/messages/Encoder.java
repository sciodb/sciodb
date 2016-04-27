package org.sciodb.messages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jenaiz on 23/04/16.
 */
public class Encoder {

    private List<ByteBuffer> container;

    private final static int LONG_TYPE = 1;

    public Encoder() {
        this.container = new ArrayList<>();
    }

    public void in(final long l) {
        final ByteBuffer bb = ByteBuffer.allocate(8)
                                        .order(ByteOrder.BIG_ENDIAN)
                                        .putLong(l);
//        container.add(ByteBuffer.allocate(1).putInt(1));
        container.add(bb);
    }

    public void in(int i) {
        final ByteBuffer bb = ByteBuffer.allocate(4)
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

    public byte[] container() {
        try {
            return concat(container);
        } catch (IOException e) {
//            e.printStackTrace();
            return new byte[0];
        }
    }


    private byte[] concat(final List<ByteBuffer> input) throws IOException {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        if (input != null) {
            for (ByteBuffer bb : input) {
                outputStream.write(bb.array());
            }
        }

        return outputStream.toByteArray();
    }

    public static void main(String[] args) {
        final Encoder e = new Encoder();
        e.in(100);
        e.in(100L);
        e.in("helloworld");

        byte[] result = e.container();

        System.out.println(result.length == 22);
    }
}
