package org.sciodb.topology.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sciodb.exceptions.EmptyDataException;
import org.sciodb.messages.impl.Node;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * @author Jesús Navarrete (27/11/2016)
 */
public class RoutingTable {

    private final Logger logger = LogManager.getLogger(RoutingTable.class);

    private final int bits;
    private final LinkedList<RoutingNode> nodes;

    public RoutingTable(int bits) {
        this.bits = bits;
        nodes = new LinkedList<>();
    }

    public List<Node> getNodes() {
        return nodes.stream()
                .map(RoutingNode::getNode)
                .collect(Collectors.toList());
    }

    public void add(final Node node, final long distance) {
        final RoutingNode wrapper = new RoutingNode(node, distance);

        if (!contains(node)) {
            logger.info("New node available - {}", node.url());

            nodes.add(wrapper);

            nodes.sort(Comparator.comparingLong(RoutingNode::getDistance));
            if (nodes.size() > bits) {
                final RoutingNode n = nodes.removeLast();
                logger.info("Deleting the last element {}", n.getNode().url());
            }
        }
    }

    public void remove(final Node node) {
        for (RoutingNode rn : nodes) {
            if (rn.getNode().getGuid().equals(node.getGuid())) {
                nodes.remove(rn);
                break;
            }
        }
    }

    public Node closest() throws EmptyDataException {
        if (!nodes.isEmpty()) {
            return nodes.getFirst().getNode();
        } else {
            throw new EmptyDataException("Not element found");
        }
    }

    public int size() {
        return nodes.size();
    }

    public boolean contains(final Node node) {
        boolean found = false;
        for (final RoutingNode rn : nodes) { // TODO FIX: ConcurrentModificationException
            if ((rn.getNode().url().equals(node.url())) || (rn.getNode().getGuid().equals(node.getGuid()))) {
                found = true;
                break;
            }
        }

        return found;
    }

    public Node find(final Node node) {
        for (final RoutingNode rn : nodes) {
            if ((rn.getNode().url().equals(node.url())) || (rn.getNode().getGuid().equals(node.getGuid()))) {
                return rn.getNode();
            }
        }

        throw new NoSuchElementException("Element not found");
    }
}
