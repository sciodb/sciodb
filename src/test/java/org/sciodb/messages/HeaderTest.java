package org.sciodb.messages;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.*;

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
        final Header h = new Header("123", 100, 2);

        byte[] headerBytes = h.encode();

        final Header h2 = new Header();
        h2.decode(headerBytes);

        assertEquals("123", new String(ByteBuffer.wrap(headerBytes, 0, 4).array()));
        assertEquals(100, ByteBuffer.wrap(headerBytes, 4, 4).getInt());
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