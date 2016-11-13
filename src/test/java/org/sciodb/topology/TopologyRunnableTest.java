package org.sciodb.topology;

import org.junit.Test;
import org.sciodb.messages.impl.Node;

import static org.junit.Assert.*;

/**
 * @author Jesús Navarrete (13/11/2016)
 */
public class TopologyRunnableTest {

    @Test
    public void run() throws Exception {

    }

    @Test
    public void isInteger() throws Exception {
        assertEquals(TopologyRunnable.isInteger("he12"), false);
        assertEquals(TopologyRunnable.isInteger("   1"), true);
        assertEquals(TopologyRunnable.isInteger("12"), true);
        assertEquals(TopologyRunnable.isInteger("12mmm"), false);
        assertEquals(TopologyRunnable.isInteger("1.2"), false);
        assertEquals(TopologyRunnable.isInteger("1 2"), false);
        assertEquals(TopologyRunnable.isInteger(""), false);
        assertEquals(TopologyRunnable.isInteger(null), false);
    }

}