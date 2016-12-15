package org.sciodb.topology;

import org.apache.log4j.Logger;
import org.sciodb.exceptions.EmptyDataException;
import org.sciodb.messages.impl.Node;
import org.sciodb.topology.impl.RoutingTable;
import org.sciodb.utils.Configuration;
import org.sciodb.utils.GUID;
import org.sciodb.utils.StringUtils;
import org.sciodb.utils.ThreadUtils;

import java.util.*;

/**
 * @author Jesús Navarrete (22/09/14)
 */
public class TopologyContainer {

    private final RoutingTable table;

    private static final TopologyContainer instance = new TopologyContainer();

    private static int waitingTime;
    private static int persistTime;
    private static int masterCheckingTime;

    private Logger logger = Logger.getLogger(TopologyContainer.class);

//    private final int MINIMUN_PEERS;
    private Node me;

    private TopologyContainer() {
        table = new RoutingTable(64); // TODO set to 128 bits

        waitingTime = Configuration.getInstance().getNodesCheckTimeTopology();
        persistTime = Configuration.getInstance().getNodesPersistTimeTopology();

        masterCheckingTime = Configuration.getInstance().getMasterCheckTimeTopology();
//        MINIMUN_PEERS = Configuration.getInstance().getReplicasNumber();
    }

    public static TopologyContainer getInstance() {
        return instance;
    }

    public void join(final Node node) {
        // TODO least-recently seen node at the head, most-recently seen at the tail
         if (StringUtils.isNotEmpty(node.getGuid())) {

            final long distance = GUID.distance(me.getGuid(), node.getGuid());
            table.add(node, distance);

        } else {
            logger.warn("Node not added - " + node.url());
            logger.warn("Node not added - " + node.getGuid());

        }
    }

    void checkNodes() {

        final long now = System.currentTimeMillis();

        final Iterator<Node> iterator = table.getNodes().iterator();

        logger.debug("Nodes availables...");
        while (iterator.hasNext()) {
            final Node node = iterator.next();

            boolean alive = checkNode(me, node, masterCheckingTime, 3);

            if (alive) {
                logger.info(node.url() + " - available");
            } else {
                iterator.remove();
                table.leave(node);
                logger.error(node.url() + " - not available ");
            }
        }

        checkPeers();

        final long finished = System.currentTimeMillis();
        final long timeUsed = finished - now;
        if (timeUsed < waitingTime) {
            ThreadUtils.sleep((int)(waitingTime - timeUsed));
        }
    }

    private boolean checkNode(final Node me, final Node node, final int waitingTime, final int retries) {
        boolean execute = false;
        final NodeOperations op = new NodeOperations(me);
        for (int i = 0; i < retries; i++) {
            if (op.ping(node)) {
                execute = true;
                break;
            }
            logger.info(String.format("Retry connecting with node, try %d", i));
            ThreadUtils.sleep(waitingTime);
        }
        return execute;
    }

    private void checkPeers() {
//        if (!availableNodes.isEmpty() && availableNodes.size() > peers.size() && peers.size() <= MINIMUN_PEERS) {
//            final List<Node> nodes;
//            try {
//                nodes = NodeOperations.copyRoutingTable(me, availableNodes.first());
//                logger.info("More peers discovered: " + nodes.size());
//                peers.addAll(nodes);
//            } catch (CommunicationException e) {
//                e.printStackTrace();
//            }
//        }
    }

    public List<Node> getAvailableNodes() {
        return table.getNodes();
    }

    public List<Node> getPeers(final Node node) { // Don't delete this *node* it will be used soon!
        final List<Node> peers = table.getNodes();
        peers.add(me);
        return peers;
    }

    public void setMe(final Node me) {
        this.me = me;
    }

    public Node check(final Node source) throws EmptyDataException {
        if (table.contains(source)) {
            return source;
        } else {
            throw new EmptyDataException("No node found");
        }
    }

}
