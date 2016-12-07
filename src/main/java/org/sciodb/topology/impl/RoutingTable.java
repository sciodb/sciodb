package org.sciodb.topology.impl;

import org.sciodb.exceptions.EmptyDataException;
import org.sciodb.messages.impl.Node;

import java.util.*;

/**
 * @author jenaiz on 27/11/2016.
 */
public class RoutingTable {

    private org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(RoutingTable.class);
    private int bits;
    private LinkedList<RoutingNode> nodes;

    public RoutingTable(int bits) {
        this.bits = bits;
        nodes = new LinkedList<>();
    }

    public int getBits() {
        return bits;
    }

    public List<Node> getNodes() {
        List<Node> result = new ArrayList<>();

        for (RoutingNode r: nodes) { // TODO use lambdas !!
            result.add(r.getNode());
        }
        return result;
    }

    public boolean add(final Node node, final long distance) {
        final RoutingNode wrapper = new RoutingNode(node, distance);
        boolean result = true;

        if (!contains(node)) {
            nodes.add(wrapper);

            Collections.sort(nodes, new Comparator<RoutingNode>(){
                @Override
                public int compare(RoutingNode o1, RoutingNode o2){
                    if(o1.getDistance() < o2.getDistance()){
                        return -1;
                    }
                    if(o1.getDistance() > o2.getDistance()){
                        return 1;
                    }
                    return 0;
                }
            });
            if (nodes.size() > bits) {
                final RoutingNode n = nodes.removeLast();
                logger.info("Deleting the last element " + n.getNode().url());
                if (n.getNode().getGuid().equals(node.getGuid())) result = false;
            }
        } else {
            result = false;
        }
        return result;
    }

    public void leave(final Node node) {
        for (RoutingNode rn : nodes) {
            if (rn.getNode().getGuid().equals(node.getGuid())) {
                nodes.remove(rn);
                break;
            }
        }
    }

    public Node closest() throws EmptyDataException {
        if (nodes.size() > 0) {
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
        for (final RoutingNode rn : nodes) {
            if ((rn.getNode().url().equals(node.url())) || (rn.getNode().getGuid().equals(node.getGuid()))) {
                found = true;
                break;
            }
        }

        return found;
    }

}
