package org.sciodb.topology;

import org.junit.Before;
import org.junit.Test;
import org.sciodb.messages.impl.Node;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Jesús Navarrete (06/08/16)
 */
public class P2PNetImplTest {

    private Net p2pNet;

    @Before
    public void setUp() throws Exception {
        p2pNet = new P2PNetImpl();
    }

    @Test
    public void add() throws Exception {
        final Node node1 = new Node("0.0.0.0", 9090);
        p2pNet.add(node1);

        assertEquals(1, p2pNet.snapshot().size());
    }

    @Test
    public void remove() throws Exception {
        final Node node1 = new Node("0.0.0.0", 9090);
        p2pNet.add(node1);

        assertEquals(1, p2pNet.snapshot().size());

        p2pNet.remove(node1);

        assertEquals(0, p2pNet.snapshot().size());

    }

    @Test
    public void getPeers() throws Exception {
        final Node node1 = new Node("0.0.0.0", 9090);
        p2pNet.add(node1);

        final Node node2 = new Node("0.0.0.0", 9091);
        p2pNet.add(node2);

        final List<Node> peers = p2pNet.getPeers(node1);

        assertEquals(1, peers.size());
        assertEquals(node2.getPort(), peers.get(0).getPort());
    }

    @Test
    public void getPeers_threeNodes() throws Exception {
        final Node node1 = new Node("0.0.0.0", 9090);
        p2pNet.add(node1);

        final Node node2 = new Node("0.0.0.0", 9091);
        p2pNet.add(node2);

        final Node node3 = new Node("0.0.0.0", 9092);
        p2pNet.add(node3);

        final List<Node> peers = p2pNet.getPeers(node2);

        assertEquals(2, peers.size());
        assertEquals(node1.getPort(), peers.get(0).getPort());
        assertEquals(node3.getPort(), peers.get(1).getPort());
    }

    @Test
    public void snapshot() throws Exception {

        final int amount = 4;

        fillNet(amount);
        final List<Node> nodes = p2pNet.snapshot();

        assertEquals(amount, nodes.size());
        assertEquals(9000, nodes.get(0).getPort());
        assertEquals("0.0.0.0", nodes.get(0).getHost());
    }

    private void fillNet(final int amount) {
        for (int i = 0; i < amount; i++) {
            final Node node = new Node("0.0.0.0", 9000 + i);
            p2pNet.add(node);
        }
    }

}