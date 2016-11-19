package org.sciodb.topology.impl;

import org.junit.Before;
import org.junit.Test;
import org.sciodb.messages.impl.Node;
import org.sciodb.topology.Net;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Jesús Navarrete (06/08/16)
 */
public class MatrixNetImplTest {

    private Net matrixNet;

    @Before
    public void setUp() throws Exception {
        matrixNet = new MatrixNetImpl();
    }

    @Test
    public void add() throws Exception {
        fillNet(1);

        assertEquals(1, matrixNet.snapshot().size());
        assertEquals(1, matrixNet.size());
        assertEquals(false, matrixNet.isEmpty());
    }

    @Test
    public void add_null() throws Exception {
        matrixNet.add(null);

        assertEquals(0, matrixNet.snapshot().size());
        assertEquals(0, matrixNet.size());
        assertEquals(true, matrixNet.isEmpty());
    }

    @Test
    public void add_10() throws Exception {
        fillNet(10);

        assertEquals(10, matrixNet.snapshot().size());
        assertEquals(10, matrixNet.size());
        assertEquals(false, matrixNet.isEmpty());
    }

    @Test
    public void remove() throws Exception {
        final Node node = new Node("0.0.0.0", 9090);
        matrixNet.add(node);

        assertEquals(1, matrixNet.snapshot().size());

        matrixNet.remove(node);

        assertEquals(0, matrixNet.snapshot().size());
        assertEquals(0, matrixNet.size());
        assertEquals(true, matrixNet.isEmpty());
    }

    @Test
    public void remove_null() throws Exception {
        fillNet(1);

        assertEquals(1, matrixNet.snapshot().size());

        matrixNet.remove(null);

        assertEquals(1, matrixNet.snapshot().size());
        assertEquals(1, matrixNet.size());
        assertEquals(false, matrixNet.isEmpty());
    }

    @Test
    public void getPeers() throws Exception {
        final Node node1 = new Node("0.0.0.0", 9090);
        matrixNet.add(node1);

        final Node node2 = new Node("0.0.0.0", 9091);
        matrixNet.add(node2);

        final List<Node> peers = matrixNet.getPeers(node1);

        assertEquals(1, peers.size());
        assertEquals(node2.getPort(), peers.get(0).getPort());
        assertEquals(node2.getHost(), peers.get(0).getHost());
        assertEquals(node2.hash(), peers.get(0).hash());
    }

    @Test
    public void getPeers_withNodes() throws Exception {
        final Node node1 = new Node("0.0.0.0", 9090);
        matrixNet.add(node1);

        final Node node2 = new Node("0.0.0.0", 9091);
        matrixNet.add(node2);

        final Node node3 = new Node("0.0.0.0", 9092);
        matrixNet.add(node3);

        final List<Node> peers = matrixNet.getPeers(node2);

        assertEquals(2, peers.size());

        assertEquals(node1.getPort(), peers.get(0).getPort());
        assertEquals(node1.getHost(), peers.get(0).getHost());
        assertEquals(node1.hash(), peers.get(0).hash());

        assertEquals(node3.getPort(), peers.get(1).getPort());
        assertEquals(node3.getHost(), peers.get(1).getHost());
        assertEquals(node3.hash(), peers.get(1).hash());
    }

    @Test
    public void snapshot() throws Exception {
        final int amount = 4;

        fillNet(amount);
        final List<Node> nodes = matrixNet.snapshot();

        assertEquals(amount, nodes.size());
        assertEquals(9000, nodes.get(0).getPort());
        assertEquals("0.0.0.0", nodes.get(0).getHost());
    }

    private void fillNet(final int amount) {
        for (int i = 0; i < amount; i++) {
            final Node node = new Node("0.0.0.0", 9000 + i);
            matrixNet.add(node);
        }
    }

}