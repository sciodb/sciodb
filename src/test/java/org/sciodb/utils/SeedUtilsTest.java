package org.sciodb.utils;

import org.junit.Test;
import org.sciodb.messages.impl.Node;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by jesusnavarrete on 20/11/2016.
 */
public class SeedUtilsTest {

    @Test
    public void fromString() throws Exception {
        final String host = "0.0.0.0";
        final int port = 9090;
        final List<Node> seeds = SeedUtils.fromString(host + ":" + port);

        assertEquals(1, seeds.size());
        assertEquals(port, seeds.get(0).getPort());
        assertEquals(host, seeds.get(0).getHost());
    }

    @Test
    public void fromString_withoutPort() throws Exception {
        final String host = "0.0.0.0";
        final List<Node> seeds = SeedUtils.fromString(host);

        assertEquals(0, seeds.size());
    }

    @Test
    public void fromString_withoutHost() throws Exception {
        final String port = ":9090";
        final List<Node> seeds = SeedUtils.fromString(port);

        assertEquals(0, seeds.size());
    }

    @Test
    public void fromString_multiple() throws Exception {
        final String host = "0.0.0.0";
        final int port = 9090;
        final List<Node> seeds = SeedUtils.fromString(host + ":" + port + "," + host + ":" + port + ","
                                                        + host + ":" + port + ",");

        assertEquals(3, seeds.size());
        assertEquals(port, seeds.get(0).getPort());
        assertEquals(port, seeds.get(1).getPort());
        assertEquals(port, seeds.get(2).getPort());

        assertEquals(host, seeds.get(0).getHost());
        assertEquals(host, seeds.get(1).getHost());
        assertEquals(host, seeds.get(2).getHost());
    }

}