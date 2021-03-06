package org.sciodb.utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * @author jenaiz on 02/01/2017.
 */
public class GUIDTest {

    @Test
    public void get() {
        int max = 128;
        final List<String> cache = new ArrayList<>(max);

        for (int i = 0; i < max; i++) {
            final String guid = GUID.get();

            assertFalse(cache.contains(guid));

            cache.add(guid);
        }
    }

    @Test
    public void distance() {
        final String A = GUID.get();
        final String B = GUID.get();

        assertEquals(0, GUID.distance(A, A));

        assertEquals(GUID.distance(A, A), GUID.distance(A, A));

        assertEquals(GUID.distance(A, B), GUID.distance(B, A));
    }

}