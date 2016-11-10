package org.sciodb.topology;

import org.apache.log4j.Logger;
import org.sciodb.messages.impl.Node;
import org.sciodb.utils.Configuration;
import org.sciodb.utils.ThreadUtils;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author scJesús Navarrete (22/09/14)
 */
public class TopologyContainer {

    private final Queue<Node> nodes;
    private final Queue<Node> availableNodes;

    private static final TopologyContainer instance = new TopologyContainer();

    private static int waitingTime;
    private static int persistTime;
    private static int masterCheckingTime;

    private Logger logger = Logger.getLogger(TopologyContainer.class);

    private TopologyContainer() {
        nodes = new ConcurrentLinkedQueue<>();
        availableNodes = new ConcurrentLinkedQueue<>();

        waitingTime = Configuration.getInstance().getNodesCheckTimeTopology();
        persistTime = Configuration.getInstance().getNodesPersistTimeTopology();

        masterCheckingTime = Configuration.getInstance().getMasterCheckTimeTopology();
    }

    public static TopologyContainer getInstance() {
        return instance;
    }

    public void addNode(final Node node) {
        if (!availableNodes.contains(node) && !nodes.contains(node)) {
            logger.info("Discovered node - " + node.url());
            nodes.add(node);
        }
    }

    private void addAvailableNode(final Node node) {
        if (!availableNodes.contains(node)) {
            logger.info("New node available - " + node.url());
            availableNodes.add(node);
        }
    }

    void checkNodes(final Node me) {
        final long now = System.currentTimeMillis();

        final Iterator<Node> iterator = availableNodes.iterator();

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

        final Iterator<Node> iterator2 = nodes.iterator();
        while (iterator2.hasNext()) {
            final Node node = iterator2.next();
            if (node.getLastCheck() == 0) {
                node.setLastCheck(now);
            } else {
                if ((now - node.getLastCheck()) < 5000) {
                    continue;
                }
            }

            boolean alive = checkNode(me, node, masterCheckingTime, 3);

            if (alive) {
                addAvailableNode(node);
                iterator2.remove();
                logger.info(node.url() + " - available");
            } else {
                logger.error(node.url() + " - not available ");
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
            if (NodeOperations.isAlife(me, node, nodes)) {
                execute = true;
                break;
            }
            logger.info(String.format("Retry connecting with node, try %d", i));
            ThreadUtils.sleep(waitingTime);
        }
        return execute;
    }

    public Queue<Node> getAvailableNodes() {
        return availableNodes;
    }
    
}
