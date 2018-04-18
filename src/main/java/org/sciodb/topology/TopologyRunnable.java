package org.sciodb.topology;

import org.apache.log4j.Logger;
import org.sciodb.exceptions.CommunicationException;
import org.sciodb.messages.impl.Node;
import org.sciodb.utils.Configuration;
import org.sciodb.utils.FileUtils;
import org.sciodb.utils.GUID;
import org.sciodb.utils.StringUtils;
import org.sciodb.utils.ThreadUtils;

import java.util.List;

/**
 * @author Jesús Navarrete (24/09/14)
 */
public class TopologyRunnable implements Runnable {

    private Logger logger = Logger.getLogger(TopologyRunnable.class);

    private static int persistTime;
    private static int waitingTime;

    private static TopologyContainer container;

    private Node me;

    private final List<Node> seeds;

    public TopologyRunnable(final Node me, final List<Node> seeds) {
        persistTime = Configuration.getInstance().getNodesPersistTimeTopology();
        waitingTime = Configuration.getInstance().getNodesCheckTimeTopology();

        this.me = me;
        this.seeds = seeds;
        container = TopologyContainer.getInstance();
        if (seeds.size() == 0) me.setGuid(GUID.get());
    }

    @Override
    public void run() {

        container.setMe(me);

        // bootstrap
        final NodeOperations op = new NodeOperations(me);
        boolean isJoined = false;
        for (final Node seed: seeds) {
            final String guid = op.store(seed);

            if (StringUtils.isNotEmpty(guid)) {
                me.setGuid(guid);
                try {
                    final List<Node> possiblePeers = op.findNode(seed);

                    for(final Node peer: possiblePeers) {
                        container.join(peer);
                    }
                } catch (final CommunicationException e) {
                    logger.error("Problems getting nodes from peer" , e);
                }
                isJoined = true;

                break;
            }
        }

        if (!isJoined) {
            me.setGuid(GUID.get());
        }

        long lastUpdate = System.currentTimeMillis();

        while (true) {
            if (!container.isNetworkUpdated()) { container.setNetworkUpdated(true); }

            final long now = System.currentTimeMillis();

            container.checkNodes();

            final long finished = System.currentTimeMillis();
            final long timeUsed = finished - now;
            if (timeUsed < waitingTime) {
                ThreadUtils.sleep((int)(waitingTime - timeUsed));
            }

            if ((System.currentTimeMillis() - lastUpdate) > persistTime) {
                FileUtils.persistNodes(me.getPort());
                lastUpdate = System.currentTimeMillis();
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

}
