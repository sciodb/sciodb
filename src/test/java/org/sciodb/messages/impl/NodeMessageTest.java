package org.sciodb.messages.impl;

import org.junit.Test;
import org.sciodb.utils.GUID;
import org.sciodb.utils.ScioDBConstants;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Jesús Navarrete (28/06/16)
 */
public class NodeMessageTest {

    @Test
    public void encode() throws Exception {

        final Node node = new Node("host", 200);
        final String guid = GUID.get();
        node.setGuid(guid);

        final NodeMessage nm = new NodeMessage();
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
    public void encode_listOfNodesSize() {
        int max = 128;

        final List<Node> nodes = new ArrayList<>();
        for (int i = 0; i < max; i++) {
            final Node node = new Node("123.123.123.123", 9000 + i);
            node.setGuid(GUID.get());

            nodes.add(node);
        }

        final NodesMessage nodesMessage = new NodesMessage();
        nodesMessage.getNodes().addAll(nodes);

        byte[] bytes = nodesMessage.encode();
//        System.out.println(bytes.length);
        assertTrue( bytes.length < ScioDBConstants.MAX_ANSWER_BYTES);

    }

    @Test
    public void decode() throws Exception {

    }

}