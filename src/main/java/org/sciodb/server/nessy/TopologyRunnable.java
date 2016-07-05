package org.sciodb.server.nessy;

import org.apache.log4j.Logger;
import org.sciodb.messages.impl.Node;
import org.sciodb.utils.Configuration;
import org.sciodb.utils.FileUtils;
import org.sciodb.utils.ServerException;

import java.io.IOException;
import java.util.List;

/**
 * @author jesus.navarrete  (24/09/14)
 */
public class TopologyRunnable implements Runnable {

    private Logger logger = Logger.getLogger(TopologyRunnable.class);

    private static int waitingTime;
    private static int persistTime;
    private static int masterCheckingTime;

    private Node me;

    private static long lastUpdate;

    public TopologyRunnable(final Node node) throws ServerException {
        waitingTime = Configuration.getInstance().getNodesCheckTimeNessyTopology();
        persistTime = Configuration.getInstance().getNodesPersistTimeNessyTopology();

        masterCheckingTime = Configuration.getInstance().getMasterCheckTimeNessyTopology();

        me = node;
    }

    @Override
    public void run() {

        if (Roles.chunker.name().equals(me.getRole())) {

            final Node root = new Node(Configuration.getInstance().getRootHostNessyTopology(),
                    Configuration.getInstance().getRootPortNessyTopology(), Roles.root.name());

            final NodeOperations operation = new NodeOperations();

            logger.info("Starting node as [" + Roles.chunker.name() + "]");
            boolean execute = false;
            for (int i = 0; i < 3; i++) {
                if (NodeOperations.addToRoot(me, root)) {
                    execute = true;
                    break;
                }
                try {
                    logger.info(String.format("Connecting with Root, try %d", i));
                    Thread.sleep(masterCheckingTime);
                } catch (InterruptedException ie) { /*not important*/ }
            }
            if (!execute) throw new RuntimeException("Imposible to communicate with root"); // TODO stop the world !!!!!

            while (execute) {
                NodeOperations.checkRoot(root);
                try {
                    Thread.sleep(masterCheckingTime);
                } catch (InterruptedException ie) { /*not important*/ }
            }
        } else if (Roles.root.name().equals(me.getRole())) {
            final String fileName = Configuration.getInstance().getTempFolder() + FileUtils.OUTPUT_FILE;

            lastUpdate = System.currentTimeMillis();
            final TopologyContainer t = TopologyContainer.getInstance();

            try {
                final String previousInfo = FileUtils.read(fileName, FileUtils.ENCODING);
                if (previousInfo != null && !"".equals(previousInfo)) {
                    final List<Node> previousNodes = NodeMapper.fromString(previousInfo);

                    for (final Node node : previousNodes) {
                        t.addNode(node);
                    }
                }

            } catch (IOException e) {
                logger.error("Error reading the file", e);
            }

            logger.info("Starting node as [" + Roles.root.name() + "]");
            while (true) {
                t.checkNodes();

                if ((System.currentTimeMillis() - lastUpdate) > persistTime) {
                    FileUtils.persistNodes(t);
                }

                try {
                    Thread.sleep(waitingTime);
                } catch (InterruptedException e) { /* not important */ }
            }
        }
    }

}
