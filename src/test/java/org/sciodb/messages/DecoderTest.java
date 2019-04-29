package org.sciodb.messages;

import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.junit.Assert.assertEquals;

/**
 * @author Jesús Navarrete (30/04/16)
 */
public class DecoderTest {

    @Test
    public void outInt() throws Exception {
        int i = 12345;
        final ByteBuffer bb = ByteBuffer.allocate(4).putInt(i);

        final Decoder d = new Decoder(bb.array());
        assertEquals(i, d.getInt());
    }

    @Test
    public void outLong() throws Exception {
        long l = 12345L;
        final ByteBuffer bb = ByteBuffer.allocate(8).putLong(l);

        final Decoder d = new Decoder(bb.array());
        assertEquals(l, d.getLong());
    }

    @Test
    public void outString() throws Exception {
        final String str = "helloworld";
        int size = 4 + str.length();
        final ByteBuffer bb = ByteBuffer.allocate(size)
                .order(ByteOrder.BIG_ENDIAN)
                .putInt(str.length())
                .put(str.getBytes());

        final Decoder d = new Decoder(bb.array());
        assertEquals(str, d.getString());
    }

    @Test
    public void outByte() {}

    @Test
    public void getInt() {
        int l = 12345;
        final ByteBuffer bb = ByteBuffer.allocate(8).putInt(l);

        final Decoder d = new Decoder(bb.array());
        assertEquals(l, d.getInt());
    }
}