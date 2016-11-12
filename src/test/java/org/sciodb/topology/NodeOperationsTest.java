package org.sciodb.topology;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sciodb.messages.impl.Node;

import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.Assert.*;

/**
 * @author jenaiz on 12/11/2016.
 */
public class NodeOperationsTest {

    private NodeOperations operations;

    @Before
    public void setUp() throws Exception {
        operations = new NodeOperations();
    }

    @After
    public void tearDown() throws Exception {
        operations = null;
    }

    @Test
    public void discoverPeer() throws Exception {
        final Node me = new Node("localhost", 9091);
        final Node seed = new Node("localhost", 9090);

        for(int i = 0; i < 100; i++) {
            operations.discoverPeer(me, seed);
        }

    }

    @Test
    public void isAlive() throws Exception {
        final Node me = new Node("localhost", 9091);
        final Node seed = new Node("localhost", 9090);

        for(int i = 0; i < 10; i++) {
            assertTrue(operations.isAlive(me, seed));
        }
    }

}