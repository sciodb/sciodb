package org.sciodb.messages;

import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.*;

/**
 * @author jenaiz on 24/04/16.
 */
public class EncoderTest {

    @Test
    public void in_int() throws Exception {
        final Encoder e = new Encoder();
        final int number = 100;
        e.in(number);

        byte[] result = e.container();

        assertEquals(4, result.length);
        assertEquals(number, ByteBuffer.wrap(result).getInt());
    }

    @Test
    public void in_long() throws Exception {
        final Encoder e = new Encoder();
        final long number = 100L;
        e.in(number);

        byte[] result = e.container();

        assertEquals(8, result.length);
        assertEquals(number, ByteBuffer.wrap(result).getLong());
    }

    @Test
    public void in_string() throws Exception {
        final Encoder e = new Encoder();
        final String text = "hello world!";
        e.in(text);

        byte[] result = e.container();

        assertEquals(text.length() + 4, result.length);
        assertEquals(text, new String(ByteBuffer.wrap(result, 4, text.length()-4).array()));
    }

    @Test
    public void container() throws Exception {
        fail("not tested yet");
    }

}