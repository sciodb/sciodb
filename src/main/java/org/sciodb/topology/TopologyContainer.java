package org.sciodb.topology;

import org.apache.log4j.Logger;
import org.sciodb.exceptions.EmptyDataException;
import org.sciodb.messages.impl.Node;
import org.sciodb.topology.impl.RoutingTable;
import org.sciodb.utils.Configuration;
import org.sciodb.utils.GUID;
import org.sciodb.utils.StringUtils;
import org.sciodb.utils.ThreadUtils;

import java.util.Iterator;
import java.util.List;

/**
 * @author Jesús Navarrete (22/09/14)
 */
public class TopologyContainer {

    private final RoutingTable table;

    private static final TopologyContainer instance = new TopologyContainer();

    private static final int MAX_RETRIES = 3;

    private static int retryTime;

    private Logger logger = Logger.getLogger(TopologyContainer.class);

    private Node me;
    private boolean networkUpdated;

    private TopologyContainer() {
        table = new RoutingTable(128);

        retryTime = Configuration.getInstance().getRetryTimeTopology();
        networkUpdated = true;
    }

    public static TopologyContainer getInstance() {
        return instance;
    }

    public void join(final Node node) {
        // TODO least-recently seen node at the head, most-recently seen at the tail
        if (table.contains(node)) {
            final Node origin = table.find(node);
            origin.setLastCheck(System.currentTimeMillis());
        } else {
            if (StringUtils.isNotEmpty(node.getGuid())) {

                final long distance = GUID.distance(me.getGuid(), node.getGuid());
                table.add(node, distance);
                networkUpdated = false;

            } else {
                logger.warn("Node not added (empty guid)- " + node.url());
            }

        }
    }

    void checkNodes() {
        final Iterator<Node> iterator = table.getNodes().iterator();

        logger.debug("Nodes available...");
        while (iterator.hasNext()) {
            final Node node = iterator.next();

            boolean alive = checkNode(me, node);

            if (alive) {
                logger.info(node.url() + " - available");
            } else {
                iterator.remove();
                table.remove(node);
                logger.error(node.url() + " - not available ");
            }
        }
    }

    private boolean checkNode(final Node me, final Node node) {
        boolean execute = false;
        final NodeOperations op = new NodeOperations(me);
        for (int i = 0; i < MAX_RETRIES; i++) {
            if (op.ping(node)) {
                execute = true;
                break;
            }
            logger.info(String.format("Retry connecting with node, try %d", i));
            ThreadUtils.sleep(retryTime);
        }
        return execute;
    }

    public List<Node> getAvailableNodes() {
        return table.getNodes();
    }

    /**
     * It takes the GUID as an argument.  And it returns <IP address, UDP port, Node ID> triples for the k nodes it
     * knows about closest to the target ID. These triples can come from a single k-bucket, or they may come from
     * multiple k-buckets if the closest k-bucket is not full. In any case, the RPC recipient must return k items
     * (unless there are fewer than k nodes in all its k-buckets combined, in which case it returns every node it
     * knows about).
     * @param node
     * @return
     */
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

    public boolean isNetworkUpdated() {
        return networkUpdated;
    }

    public void setNetworkUpdated(boolean networkUpdated) {
        this.networkUpdated = networkUpdated;
    }

    public void leaveNetwork() {
        final NodeOperations op = new NodeOperations(me);
        try {
            op.leave(table.closest());
        } catch (final EmptyDataException e) {
            logger.warn("No close member found it, when leaving the network.");
        }
    }

    public void leave(final Node node) {
        if (table.contains(node)) {
            logger.info(node.url() + " leaving the network.");
            table.remove(node);
        }
    }
}
