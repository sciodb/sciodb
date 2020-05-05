package org.sciodb.messages.impl;

import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;

/**
 * @author Jesús Navarrete (24/04/16)
 */
public class HeaderTest {

    @Test
    public void encode() {
        final String id = "123";
        final int length = 100;
        final int operationId = 2;

        final Header h = new Header(id, length, operationId);

        byte[] headerBytes = h.encode();

        final ByteBuffer bb = ByteBuffer.wrap(headerBytes);
        int size = bb.getInt();

        assertEquals(id.length(), size);

        byte[] textEncoded = new byte[size];
        bb.get(textEncoded);
        bb.position(size + 4); // + 4 for the first integer

        final String str = new String(textEncoded);
        assertEquals(id, str);

        assertEquals(length, bb.getInt());
        assertEquals(operationId, bb.getInt());
    }

    @Test
    public void decode() {
        byte[] headerBytes = {0, 0, 0, 3, 49, 50, 51, 0, 0, 0, 100, 0, 0, 0, 2};

        final Header h2 = new Header();
        h2.decode(headerBytes);

        assertEquals("123", h2.getId());
        assertEquals(100, h2.getLength());
        assertEquals(2, h2.getOperationId());
    }

}