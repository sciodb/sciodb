package org.sciodb.topology;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sciodb.messages.impl.Node;

import static org.junit.Assert.assertTrue;

/**
 * @author jenaiz on 12/11/2016.
 */
public class NodeOperationsTest {

    private NodeOperations operations;

    @Before
    public void setUp() {
        final Node node = new Node("localhost", 9090);

        operations = new NodeOperations(node);
    }

    @After
    public void tearDown() {
        operations = null;
    }

    @Ignore
    @Test
    public void isAlive() {
        final Node target = new Node("localhost", 9091);

        for(int i = 0; i < 10; i++) {
            assertTrue(operations.ping(target));
        }
    }
}