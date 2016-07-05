package org.sciodb.server.nessy;

import org.apache.log4j.Logger;
import org.sciodb.messages.impl.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author jesus.navarrete  (22/09/14)
 */
public class TopologyContainer {

    private final List<Node> nodes;
    private final List<Node> nodesAvailable;

    private static final TopologyContainer instance = new TopologyContainer();

    private Logger logger = Logger.getLogger(TopologyContainer.class);

    private TopologyContainer() {
        nodes = Collections.synchronizedList(new ArrayList<>());
        nodesAvailable = Collections.synchronizedList(new ArrayList<>());;
    }

    public static TopologyContainer getInstance() {
        return instance;
    }

    public void checkNodes() {

        final Iterator<Node> iterator = nodes.iterator();
        while (iterator.hasNext()) {
            final Node node = iterator.next();
            checkStatus(node);
        }
    }

    public void addNode(final Node node) {
        synchronized (this) {
            if (!nodes.contains(node)) {
                logger.info("addNode ... " + node.url());
                nodes.add(node);
            }
        }
    }

    private void checkStatus(final Node node) {

        if (NodeOperations.isAlife(node)) {
            logger.info(node.url() + " - available");
            if (!nodesAvailable.contains(node)) nodesAvailable.add(node);
        } else {
            logger.error(node.url() + " - not available ");
            if (nodesAvailable.contains(node)) {
                nodesAvailable.remove(node);
                logger.error("node not available, removing: " + node.url());
            }
        }
    }

    public List<Node> getNodes() {
        return nodes;
    }

}
