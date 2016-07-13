package org.sciodb.topology;

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

    private TopologyContainer container;

    public TopologyRunnable(final Node node, final String[] seeds) throws ServerException {
        waitingTime = Configuration.getInstance().getNodesCheckTimeNessyTopology();
        persistTime = Configuration.getInstance().getNodesPersistTimeNessyTopology();

        masterCheckingTime = Configuration.getInstance().getMasterCheckTimeNessyTopology();

        me = node;
        parseSeeds(seeds);
        container = TopologyContainer.getInstance();

    }

    @Override
    public void run() {

        logger.info("Starting node [" + me.url() + "]");

        parseHistoricalNodes();
        long lastUpdate = System.currentTimeMillis();

        while (true) {
            container.checkNodes(me);

            if ((System.currentTimeMillis() - lastUpdate) > persistTime) {
                FileUtils.persistNodes(container, me.getPort());
                lastUpdate = System.currentTimeMillis();
            }
        }
    }

    private void parseHistoricalNodes() {
        final String fileName = Configuration.getInstance().getTempFolder() + FileUtils.OUTPUT_FILE;

        try {
            final String previousInfo = FileUtils.read(fileName, FileUtils.ENCODING);
            if (previousInfo != null && !"".equals(previousInfo)) {
                final List<Node> previousNodes = NodeMapper.fromString(previousInfo);

                for (final Node node : previousNodes) {
                    container.addNode(node);
                }
            }

        } catch (IOException e) {
            logger.error("Error reading the file", e);
        }
    }

    private void parseSeeds(final String[] seeds) {
        for (final String s : seeds) {
            final String[] parts = s.split(":");
            if (parts.length == 2 && isInteger(parts[1])) {
                final Node node = new Node(parts[0], new Integer(parts[1].trim()));
                TopologyContainer.getInstance().addNode(node);
            }
        }
    }


    public static boolean isInteger(final String str) {
        return str != null && str.trim().matches("-?\\d+");  //match a number with optional '-' and decimal.
    }

    public static void main(String[] args) {
        System.out.println(isInteger("he12") == false);
        System.out.println(isInteger("   1") == true);
        System.out.println(isInteger("12") == true);
        System.out.println(isInteger("12mmm") == false);
        System.out.println(isInteger("1.2") == false);
        System.out.println(isInteger("1 2") == false);
        System.out.println(isInteger("") == false);
        System.out.println(isInteger(null) == false);
    }

}
