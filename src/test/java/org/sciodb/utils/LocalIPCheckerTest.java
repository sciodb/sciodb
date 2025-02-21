package org.sciodb.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class LocalIPCheckerTest {

    @Test
    public void testLocalIPs() {
        assertTrue(LocalIPChecker.isLocalIP("10.0.0.1"));
        assertTrue(LocalIPChecker.isLocalIP("172.16.5.10"));
        assertTrue(LocalIPChecker.isLocalIP("192.168.1.1"));
        assertTrue(LocalIPChecker.isLocalIP("169.254.10.20"));
    }

    @Test
    public void testPublicIPs() {
        assertFalse(LocalIPChecker.isLocalIP("8.8.8.8"));
        assertFalse(LocalIPChecker.isLocalIP("1.1.1.1"));
        assertFalse(LocalIPChecker.isLocalIP("200.200.200.200"));
    }

    @Test
    public void testInvalidIPs() {
        assertFalse(LocalIPChecker.isLocalIP("999.999.999.999"));
        assertFalse(LocalIPChecker.isLocalIP("abc.def.ghi.jkl"));
    }
}