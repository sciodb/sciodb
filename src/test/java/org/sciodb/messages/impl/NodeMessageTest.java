package org.sciodb.messages.impl;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author jesus.navarrete  (28/06/16)
 */
public class NodeMessageTest {

    @Test
    public void encode() throws Exception {
        final NodeMessage nm = new NodeMessage();

        final Header h = new Header();
        h.setId("id");
        h.setLength(0);
        h.setOperationId(31);
        nm.setHeader(h);

        assertEquals(14, h.encode().length);

        final Node node = new Node();
        node.setHost("host");
        node.setPort(200);
        node.setRole("role");
        nm.setNode(node);

        assertEquals(20, node.encode().length);

        final NodeMessage result = new NodeMessage();

        byte[] b = nm.encode();
        assertEquals(42, b.length);

        result.decode(b);

        assertEquals(node.getHost(), result.getNode().getHost());
        assertEquals(node.getRole(), result.getNode().getRole());
        assertEquals(node.getPort(), result.getNode().getPort());

        assertEquals(h.getId(), result.getHeader().getId());
        assertEquals(h.getOperationId(), result.getHeader().getOperationId());
    }

    @Test
    public void decode() throws Exception {

    }

}