package org.sciodb.topology.impl;

import org.junit.Test;
import org.sciodb.exceptions.EmptyDataException;
import org.sciodb.messages.impl.Node;
import org.sciodb.utils.GUID;

import static org.junit.Assert.assertEquals;

/**
 * @author Jesús Navarrete on 02/12/2016.
 */
public class RoutingTableTest {

    @Test
    public void add() {
        final Node source = new Node("0.0.0.0", 9090);
        source.setGuid(GUID.get());

        final RoutingTable table = new RoutingTable(5);

        final Node target = new Node("0.0.0.0", 9091);
        target.setGuid(GUID.get());

        long distance = GUID.distance(source.getGuid(), target.getGuid());

        table.add(target, distance);

        assertEquals(1, table.size());
    }

    @Test
    public void add_tenNodes() {
        final Node source = new Node("0.0.0.0", 9090);
        source.setGuid(GUID.get());

        int amount = 10;
        final RoutingTable table = new RoutingTable(amount);

        addNodes(table, 10, source);

        assertEquals(table.size(), amount);
    }

    @Test
    public void add_moreThanExpected() {
        final Node source = new Node("0.0.0.0", 9090);
        source.setGuid(GUID.get());

        int amount = 5;
        final RoutingTable table = new RoutingTable(amount);

        addNodes(table, amount + 1, source);

        assertEquals(table.size(), amount);
    }

    @Test
    public void closest() throws Exception {
        final Node source = new Node("0.0.0.0", 9090);
        source.setGuid(GUID.get());

        int amount = 1;
        final RoutingTable table = new RoutingTable(amount);

        final Node target = new Node("0.0.0.0", 9091);
        target.setGuid(GUID.get());

        long distance = GUID.distance(source.getGuid(), target.getGuid());

        table.add(target, distance);

        assertEquals(table.size(), amount);
        assertEquals(table.closest().url(), target.url());
    }

    @Test(expected = EmptyDataException.class)
    public void closest_empty() throws Exception {
        int amount = 1;
        final RoutingTable table = new RoutingTable(amount);

        assertEquals(0, table.size());
        table.closest();
    }

    private void addNodes(final RoutingTable table, final int amount, final Node source) {
        for (int i = 1; i <= amount; i++) {
            final Node target = new Node("0.0.0.0", 9090 + i);
            target.setGuid(GUID.get());

            long distance = GUID.distance(source.getGuid(), target.getGuid());

            table.add(target, distance);
        }
    }

}