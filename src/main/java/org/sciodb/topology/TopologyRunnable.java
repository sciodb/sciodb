package org.sciodb.topology;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sciodb.exceptions.CommunicationException;
import org.sciodb.messages.impl.Node;
import org.sciodb.utils.Configuration;
import org.sciodb.utils.FileUtils;
import org.sciodb.utils.GUID;
import org.sciodb.utils.NodeMapper;
import org.sciodb.utils.StringUtils;
import org.sciodb.utils.ThreadUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jesús Navarrete (24/09/14)
 */
public class TopologyRunnable implements Runnable {

    private final Logger logger = LogManager.getLogger(TopologyRunnable.class);

    private static int persistTime;
    private static int waitingTime;

    private static TopologyContainer container;

    private final Node me;

    private final List<Node> seeds;

    public TopologyRunnable(final Node me, final List<Node> seeds) {
        persistTime = Configuration.getInstance().getNodesPersistTimeTopology();
        waitingTime = Configuration.getInstance().getNodesCheckTimeTopology();

        this.me = me;
        this.seeds = seeds;
        container = TopologyContainer.getInstance();
        if (seeds.isEmpty()) me.setGuid(GUID.get());
    }

    @Override
    public void run() {

        container.setMe(me);
        seeds.addAll(parseHistoricalNodes());
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
                        logger.warn("Peer joined - {}", peer.url());
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
        } else {
            FileUtils.persistNodes(me.getPort());
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

    private List<Node> parseHistoricalNodes() {
        final String fileName = Configuration.getInstance().getTempFolder()  + me.getPort() + "_" + FileUtils.OUTPUT_FILE;

        final List<Node> oldNodes = new ArrayList<>();
        try {
            final String previousInfo = FileUtils.read(fileName, FileUtils.ENCODING);
            if (!previousInfo.isEmpty()) {
                oldNodes.addAll(NodeMapper.fromString(previousInfo));

                for (final Node node : oldNodes) {
                    node.setGuid(null);
                    container.join(node);
                }
            }

        } catch (IOException e) {
            logger.error("Error reading the file", e);
        }
        return oldNodes;
    }

}
