package org.sciodb.topology;

import org.apache.log4j.Logger;
import org.sciodb.messages.impl.Node;
import org.sciodb.utils.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Jesús Navarrete (24/09/14)
 */
public class TopologyRunnable implements Runnable {

    private Logger logger = Logger.getLogger(TopologyRunnable.class);

//    private static int waitingTime;
    private static int persistTime;
//    private static int masterCheckingTime;

    private Node me;

    private final List<Node> seeds;

    public TopologyRunnable(final Node me, final List<Node> seeds) throws ServerException {
//        waitingTime = Configuration.getInstance().getNodesCheckTimeTopology();
        persistTime = Configuration.getInstance().getNodesPersistTimeTopology();

//        masterCheckingTime = Configuration.getInstance().getMasterCheckTimeTopology();

        this.me = me;
        this.seeds = seeds;
    }

    @Override
    public void run() {

        logger.info("Starting node [" + me.url() + "]");
        connectWithSeed(seeds);

//        parseHistoricalNodes();
        long lastUpdate = System.currentTimeMillis();

        while (true) {
            TopologyContainer.getInstance().checkNodes(me);

            if ((System.currentTimeMillis() - lastUpdate) > persistTime) {
                FileUtils.persistNodes(me.getPort());
                lastUpdate = System.currentTimeMillis();
            }
            ThreadUtils.sleep(persistTime);
        }
    }

    private void parseHistoricalNodes() {
        final String fileName = Configuration.getInstance().getTempFolder() + FileUtils.OUTPUT_FILE;

        try {
            final String previousInfo = FileUtils.read(fileName, FileUtils.ENCODING);
            if (previousInfo != null && !"".equals(previousInfo)) {
                final List<Node> previousNodes = NodeMapper.fromString(previousInfo);

                for (final Node node : previousNodes) {
                    TopologyContainer.getInstance().addNode(node);
                }
            }

        } catch (IOException e) {
            logger.error("Error reading the file", e);
        }
    }

    private void connectWithSeed(final List<Node> seeds) {
        for (final Node node : seeds) {
            if (!me.url().equals(node.url()) && NodeOperations.isAlive(me, node) && NodeOperations.addNode(me, node)) {
                final List<Node> peers = NodeOperations.discoverPeer(me, node);

                logger.info(peers.size() + " peers nodes found.");
                for (final Node n : peers) {
                    TopologyContainer.getInstance().addNode(n);
                }

                break;
            }
        }
    }

}
