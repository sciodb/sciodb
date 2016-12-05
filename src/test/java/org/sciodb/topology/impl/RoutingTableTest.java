package org.sciodb.topology.impl;

import org.junit.Test;
import org.sciodb.exceptions.EmptyDataException;
import org.sciodb.messages.impl.Node;
import org.sciodb.utils.GUID;

import static org.junit.Assert.*;

/**
 * Created by jesusnavarrete on 02/12/2016.
 */
public class RoutingTableTest {

    @Test
    public void getBits() throws Exception {

    }

    @Test
    public void distance() throws Exception {

    }

    @Test
    public void getNodes() throws Exception {

    }

    @Test
    public void add() throws Exception {
        final Node source = new Node("0.0.0.0", 9090);
        source.setGuid(GUID.get());

        final RoutingTable table = new RoutingTable(5);

        final Node target = new Node("0.0.0.0", 9091);
        target.setGuid(GUID.get());

        long distance = GUID.distance(source.getGuid(), target.getGuid());

        table.add(target, distance);

        assertTrue(table.size() == 1);

    }

    @Test
    public void add_tenNodes() throws Exception {
        final Node source = new Node("0.0.0.0", 9090);
        source.setGuid(GUID.get());


        int amount = 10;
        final RoutingTable table = new RoutingTable(amount);

        addNodes(table, 10, source);

        assertTrue(table.size() == amount);
    }

    @Test
    public void add_moreThanExpected() throws Exception {
        final Node source = new Node("0.0.0.0", 9090);
        source.setGuid(GUID.get());

        int amount = 5;
        final RoutingTable table = new RoutingTable(amount);

        addNodes(table, 10, source);

        assertTrue(table.size() == amount);
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

        assertTrue(table.size() == amount);
        assertTrue(table.closest().url().equals(target.url()));
    }

    @Test(expected = EmptyDataException.class)
    public void closest_empty() throws Exception {
        final Node source = new Node("0.0.0.0", 9090);
        source.setGuid(GUID.get());

        int amount = 1;
        final RoutingTable table = new RoutingTable(amount);

        assertTrue(table.size() == 0);
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