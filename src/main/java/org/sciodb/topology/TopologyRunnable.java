package org.sciodb.topology;

import org.apache.log4j.Logger;
import org.sciodb.messages.impl.Node;
import org.sciodb.topology.impl.NodeMapper;
import org.sciodb.utils.Configuration;
import org.sciodb.utils.FileUtils;
import org.sciodb.utils.ServerException;
import org.sciodb.utils.ThreadUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Jesús Navarrete (24/09/14)
 */
public class TopologyRunnable implements Runnable {

    private Logger logger = Logger.getLogger(TopologyRunnable.class);

    private static int waitingTime;
    private static int persistTime;
    private static int masterCheckingTime;

    private Node me;

    private final String[] seeds;

    public TopologyRunnable(final Node me, final String[] seeds) throws ServerException {
        waitingTime = Configuration.getInstance().getNodesCheckTimeTopology();
        persistTime = Configuration.getInstance().getNodesPersistTimeTopology();

        masterCheckingTime = Configuration.getInstance().getMasterCheckTimeTopology();

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

    private void connectWithSeed(final String[] seeds) {
        final Set<Node> foundNodes = new HashSet<>();
        for (final String seed : seeds) {
            final String[] parts = seed.split(":");
            if (parts.length == 2 && isInteger(parts[1])) {
                final Node node = new Node(parts[0], new Integer(parts[1].trim()));
                if (!me.url().equals(node.url())) {
                    if (NodeOperations.isAlive(me, node)) {
                        if (NodeOperations.addNode(me, node)) {
                            final List<Node> peers = NodeOperations.discoverPeer(me, node);
                            foundNodes.addAll(peers);
                            break;
                        }
                    }
                }
            }
        }
        logger.info(foundNodes.size() + " peers nodes found.");
        for (final Node n : foundNodes) {
            TopologyContainer.getInstance().addNode(n);
        }
    }

    public static boolean isInteger(final String str) {
        return str != null && str.trim().matches("-?\\d+");  //match a number with optional '-' and decimal.
    }

}
