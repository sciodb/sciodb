package org.sciodb.topology.impl;

import org.sciodb.messages.impl.Node;

/**
 * @author Jesús Navarrete (02/12/2016)
 */
public class RoutingNode {

    private Node node;
    private long distance;

    public RoutingNode(final Node node, final long distance) {
        this.node = node;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public long getDistance() {
        return distance;
    }

    public void setDistance(long distance) {
        this.distance = distance;
    }
}
