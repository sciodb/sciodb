package org.sciodb.utils;

import org.junit.Test;
import org.sciodb.topology.TopologyRunnable;

import static org.junit.Assert.*;

/**
 * Created by jesusnavarrete on 20/11/2016.
 */
public class StringUtilsTest {

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