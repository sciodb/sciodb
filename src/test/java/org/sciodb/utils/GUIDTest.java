package org.sciodb.utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author jenaiz on 02/01/2017.
 */
public class GUIDTest {

    @Test
    public void get() throws Exception {
        int max = 128;
        final List<String> cache = new ArrayList<>(max);

        for (int i = 0; i < max; i++) {
            final String guid = GUID.get();

            assertFalse(cache.contains(guid));

            cache.add(guid);
        }
    }

    @Test
    public void distance() throws Exception {
        final String A = GUID.get();
        final String B = GUID.get();

        assertEquals(0, GUID.distance(A, A));

        assertEquals(GUID.distance(A, A), GUID.distance(A, A));

        assertEquals(GUID.distance(A, B), GUID.distance(B, A));
    }

}