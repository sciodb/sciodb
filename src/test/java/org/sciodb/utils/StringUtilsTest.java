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
        assertEquals(StringUtils.isInteger("he12"), false);
        assertEquals(StringUtils.isInteger("   1"), true);
        assertEquals(StringUtils.isInteger("12"), true);
        assertEquals(StringUtils.isInteger("12mmm"), false);
        assertEquals(StringUtils.isInteger("1.2"), false);
        assertEquals(StringUtils.isInteger("1 2"), false);
        assertEquals(StringUtils.isInteger(""), false);
        assertEquals(StringUtils.isInteger(null), false);
    }

}