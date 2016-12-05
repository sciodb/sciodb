package org.sciodb.topology;

import org.apache.log4j.Logger;
import org.sciodb.exceptions.CommunicationException;
import org.sciodb.messages.impl.Node;
import org.sciodb.utils.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jesús Navarrete (24/09/14)
 */
public class TopologyRunnable implements Runnable {

    private Logger logger = Logger.getLogger(TopologyRunnable.class);

//    private static int waitingTime;
    private static int persistTime;
//    private static int masterCheckingTime;
    private static TopologyContainer container;

    private Node me;

    private final List<Node> seeds;

    public TopologyRunnable(final Node me, final List<Node> seeds) throws ServerException {
//        waitingTime = Configuration.getInstance().getNodesCheckTimeTopology();
        persistTime = Configuration.getInstance().getNodesPersistTimeTopology();

//        masterCheckingTime = Configuration.getInstance().getMasterCheckTimeTopology();

        this.me = me;
        this.seeds = seeds;
        container = TopologyContainer.getInstance();
        if (seeds.size() == 0) me.setGuid(GUID.get());
    }

    @Override
    public void run() {

        container.setMe(me);

//        logger.info("Starting node [" + me.url() + "]");
//        connectWithSeed(seeds);
//
////        parseHistoricalNodes();
//        long lastUpdate = System.currentTimeMillis();
//
//        while (true) {
//            container.checkNodes(me);
//
//            if ((System.currentTimeMillis() - lastUpdate) > persistTime) {
//                FileUtils.persistNodes(me.getPort());
//                lastUpdate = System.currentTimeMillis();
//            }
//            ThreadUtils.sleep(persistTime);
//        }

        // join the network
        if (seeds.size() > 0) {
            final List<Node> nodes = connectWithSeed(seeds);
            if (nodes.size() != 0) {
                // find correct peers
                createRoutingTable(nodes);
            }
        } else {
            me.setGuid(GUID.get());
        }
        long lastUpdate = System.currentTimeMillis();

        while (true) {
            container.checkNodes(); //me);

            if ((System.currentTimeMillis() - lastUpdate) > persistTime) {
                FileUtils.persistNodes(me.getPort());
                lastUpdate = System.currentTimeMillis();
            }
            ThreadUtils.sleep(persistTime);
        }

    }

    private void createRoutingTable(final List<Node> nodes) {
        for (final Node node: nodes) {
            if (!node.getGuid().equals(me.getGuid())) {
                final Node closestNode = NodeOperations.findClosest(me, node);
                if (!closestNode.getGuid().equals(me.getGuid()) && !(closestNode.url().equals(me.url()))) {
                    container.join(closestNode);
                }
            }
        }
    }

//    private void parseHistoricalNodes() {
//        final String fileName = Configuration.getInstance().getTempFolder() + FileUtils.OUTPUT_FILE;
//
//        try {
//            final String previousInfo = FileUtils.read(fileName, FileUtils.ENCODING);
//            if (previousInfo != null && !"".equals(previousInfo)) {
//                final List<Node> previousNodes = NodeMapper.fromString(previousInfo);
//
//                for (final Node node : previousNodes) {
//                    container.join(node);
//                }
//            }
//
//        } catch (IOException e) {
//            logger.error("Error reading the file", e);
//        }
//    }

    private List<Node> connectWithSeed(final List<Node> seeds) {
        for (final Node seed : seeds) {
            if (!me.url().equals(seed.url()) && NodeOperations.isAlive(me, seed)) {
                final String guid = NodeOperations.joinNetwork(me, seed);

                if (guid != null && !guid.isEmpty()) {
                    me.setGuid(guid);
                    container.join(seed);
                    try {
                        final List<Node> peers = NodeOperations.copyRoutingTable(me, seed);

//                        peers.add(seed);
                        logger.info(peers.size() + " peers nodes found.");
                        return peers;
                    } catch (CommunicationException ce) { /* continue with the next seed */
                    }
                }
            }
        }
        return new ArrayList<>();
    }

}
