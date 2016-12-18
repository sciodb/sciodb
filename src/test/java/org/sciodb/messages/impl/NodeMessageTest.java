package org.sciodb.messages.impl;

import org.junit.Test;
import org.sciodb.utils.GUID;

import static org.junit.Assert.assertEquals;

/**
 * @author Jesús Navarrete (28/06/16)
 */
public class NodeMessageTest {

    @Test
    public void encode() throws Exception {
        final NodeMessage nm = new NodeMessage();

        final Node node = new Node();
        node.setHost("host");
        node.setPort(200);
        final String guid = GUID.get();
        node.setGuid(guid);
        nm.setNode(node);

        assertEquals(20 + guid.length(), node.encode().length);

        final NodeMessage result = new NodeMessage();

        byte[] b = nm.encode();
        assertEquals(24 + guid.length(), b.length);

        result.decode(b);

        assertEquals(node.getHost(), result.getNode().getHost());
        assertEquals(node.getPort(), result.getNode().getPort());
        assertEquals(guid, result.getNode().getGuid());
    }

    @Test
    public void decode() throws Exception {

    }

}