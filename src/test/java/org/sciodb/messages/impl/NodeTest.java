package org.sciodb.messages.impl;

import org.junit.Test;
import org.sciodb.utils.GUID;

import static org.junit.Assert.assertEquals;

/**
 * @author jenaiz on 03/06/2017.
 */
public class NodeTest {

    @Test
    public void encode() throws Exception {
        final Node node = new Node("host", 200);
        final String guid = GUID.get();
        node.setGuid(guid);

        byte[] b = node.encode();
        assertEquals(20 + guid.length(), b.length);

        final Node result = new Node();
        result.decode(b);

        assertEquals(node.getHost(), result.getHost());
        assertEquals(node.getPort(), result.getPort());
        assertEquals(guid, result.getGuid());
    }

    @Test
    public void decode() throws Exception {
        // TODO replace this code with at by-hand byte array.
        final Node node = new Node("host", 200);
        final String guid = GUID.get();
        node.setGuid(guid);
        byte[] bytes = node.encode();
        // ---


        final Node result = new Node();
        result.decode(bytes);

        assertEquals(node.getHost(), result.getHost());
        assertEquals(node.getPort(), result.getPort());
        assertEquals(guid, result.getGuid());
    }

}