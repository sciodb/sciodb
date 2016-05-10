package org.sciodb.messages.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;

/**
 * @author jenaiz on 24/04/16.
 */
public class HeaderTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void encode() throws Exception {
        final String id = "123";
        final Header h = new Header(id, 100, 2);

        byte[] headerBytes = h.encode();

        assertEquals(15, headerBytes.length);

        byte[] lenEncoded = new byte[4];
        System.arraycopy(headerBytes, 0, lenEncoded, 0, 4);
        int len = new Integer(new String(lenEncoded));
        assertEquals(id.length(), len);

        lenEncoded = new byte[len];
        System.arraycopy(headerBytes, 4, lenEncoded, 0, len);
        assertEquals(id, new String(lenEncoded));

        lenEncoded = new byte[4];
        System.arraycopy(headerBytes, 0, lenEncoded, 0, 4);
        len = new Integer(new String(lenEncoded));
        assertEquals(100, len);

        assertEquals(2, ByteBuffer.wrap(headerBytes, 8, 4).getInt());
    }

    @Test
    public void decode() throws Exception {
        byte[] headerBytes = {};

        final Header h2 = new Header();
        h2.decode(headerBytes);

        assertEquals("123", h2.getId());
        assertEquals(100, h2.getLength());
        assertEquals(2, h2.getOperationId());
    }

}