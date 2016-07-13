package org.sciodb.server.nessy;

import org.apache.log4j.Logger;
import org.sciodb.messages.impl.Node;
import org.sciodb.utils.Configuration;
import org.sciodb.utils.ServerException;

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

        while (true) {

            container.checkNodes(me);

        }


//            final String fileName = Configuration.getInstance().getTempFolder() + FileUtils.OUTPUT_FILE;
//
//            lastUpdate = System.currentTimeMillis();
//            final TopologyContainer t = TopologyContainer.getInstance();
//
//            try {
//                final String previousInfo = FileUtils.read(fileName, FileUtils.ENCODING);
//                if (previousInfo != null && !"".equals(previousInfo)) {
//                    final List<Node> previousNodes = NodeMapper.fromString(previousInfo);
//
//                    for (final Node node : previousNodes) {
//                        t.addNode(node);
//                    }
//                }
//
//            } catch (IOException e) {
//                logger.error("Error reading the file", e);
//            }
//
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
