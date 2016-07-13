package org.sciodb.server.nessy;

import org.apache.log4j.Logger;
import org.sciodb.messages.impl.Node;
import org.sciodb.utils.Configuration;
import org.sciodb.utils.ThreadUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author jesus.navarrete  (22/09/14)
 */
public class TopologyContainer {

    private final List<Node> nodes;
    private final List<Node> availableNodes;

    private static final TopologyContainer instance = new TopologyContainer();

    private static int waitingTime;
    private static int persistTime;
    private static int masterCheckingTime;

    private Logger logger = Logger.getLogger(TopologyContainer.class);

    private TopologyContainer() {
        nodes = Collections.synchronizedList(new ArrayList<>());
        availableNodes = Collections.synchronizedList(new ArrayList<>());

        waitingTime = Configuration.getInstance().getNodesCheckTimeNessyTopology();
        persistTime = Configuration.getInstance().getNodesPersistTimeNessyTopology();

        masterCheckingTime = Configuration.getInstance().getMasterCheckTimeNessyTopology();
    }

    public static TopologyContainer getInstance() {
        return instance;
    }

    public void addNode(final Node node) {
        synchronized (this) {
            if (!nodes.contains(node)) {
                logger.info("Discovered node - " + node.url());
                nodes.add(node);
            }
        }
    }

    public void addAvailableNode(final Node node) {
        synchronized (this) {
            if (!availableNodes.contains(node)) {
                logger.info("New node available - " + node.url());
                availableNodes.add(node);
            }
        }
    }

    public void checkNodes(final Node me) {
        final long now = System.currentTimeMillis();

        Iterator<Node> iterator = nodes.iterator();
        while (iterator.hasNext()) {
            final Node node = iterator.next();
            if (node.getLastCheck() == 0) {
                node.setLastCheck(now);
            } else {
                if ((now - node.getLastCheck()) < 5000) {
                    continue;
                }
            }

            boolean alife = checkNode(me, node, masterCheckingTime, 3);

            if (alife) {
                addAvailableNode(node);
                iterator.remove();
                logger.info(node.url() + " - available");
            } else {
                logger.error(node.url() + " - not available ");
            }

        }

        iterator = availableNodes.iterator();
        while (iterator.hasNext()) {
            final Node node = iterator.next();
            boolean execute = checkNode(me, node, masterCheckingTime, 3);
            if (!execute) {
                nodes.add(node);
                iterator.remove();
                logger.error(node.url() + " - not available ");
            } else {
                logger.info(node.url() + " - available");
            }
        }
        final long finished = System.currentTimeMillis();
        final long timeUsed = finished - now;
        if (timeUsed < waitingTime) {
            ThreadUtils.sleep((int)(waitingTime - timeUsed));
        }
    }

    private boolean checkNode(final Node me, final Node node, final int waitingTime, final int retries) {
        boolean execute = false;
        for (int i = 0; i < retries; i++) {
            if (NodeOperations.isAlife(me, node)) {
                execute = true;
                break;
            }
            logger.info(String.format("Retry connecting with node, try %d", i));
            ThreadUtils.sleep(waitingTime);
        }
        return execute;
    }
    public List<Node> getNodes() {
        return nodes;
    }

    public List<Node> getAvailableNodes() {
        return availableNodes;
    }
}
