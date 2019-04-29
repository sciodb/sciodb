package org.sciodb.utils;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by jesusnavarrete on 20/11/2016.
 */
public class StringUtilsTest {

    @Test
    public void isInteger() throws Exception {
        assertFalse(StringUtils.isInteger("he12"));
        assertTrue(StringUtils.isInteger("   1"));
        assertTrue(StringUtils.isInteger("12"));
        assertFalse(StringUtils.isInteger("12mmm"));
        assertFalse(StringUtils.isInteger("1.2"));
        assertFalse(StringUtils.isInteger("1 2"));
        assertFalse(StringUtils.isInteger(""));
        assertFalse(StringUtils.isInteger(null));
    }

}
