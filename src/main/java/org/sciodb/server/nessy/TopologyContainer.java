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
//        for (final Node node: nodes) {
            final Node node = iterator.next();
            try {
                checkStatus(node);
                logger.info(node.url() + " - available");
            } catch (Exception e) {
                logger.error(node.url() + " - not available [" + e.getMessage() + "]");
                if (nodesAvailable.contains(node)) {
                    nodesAvailable.remove(node);
                    logger.error("node not available, removing: " + node.url());
                }
            }
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

    private void checkStatus(final Node node) throws Exception {

//        try {
            // TODO prepare the status input !!! (prepare the header ...)
//            byte[] input = node.encode();

//            SocketClient.sendToSocket(node.getHost(), node.getPort(), input);
        if (NodeOperations.isAlife(node)) {
            logger.info("adding available node " + node.url());
            if (!nodesAvailable.contains(node)) nodesAvailable.add(node);
        }
//        } catch (final CommunicationException ce) {
//            logger.error("Connection loose with the node " + node.getHost() + " - because: " + ce.getMessage());
//            if (nodesAvailable.contains(node)) {
//                nodesAvailable.remove(node);
//                logger.warn("node not available, removing: " + node.url());
//            }
//        }

    }

    public List<Node> getNodes() {
        return nodes;
    }

}
