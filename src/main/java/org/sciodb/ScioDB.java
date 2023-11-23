package org.sciodb;

import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sciodb.messages.impl.Node;
import org.sciodb.server.ServerSocket;
import org.sciodb.topology.TopologyContainer;
import org.sciodb.topology.TopologyRunnable;
import org.sciodb.utils.Configuration;
import org.sciodb.utils.SeedUtils;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Starter of the database based on the parameters of the command line.
 *
 * @author Jesús Navarrete (21/02/16)
 *
 * Copyright (C) 2016-2023 Jesús Navarrete <jesus.navarrete@gmail.com>
 *
 * This source code is licensed under the GNU General Public License,
 * Version 3.  See the file LICENSE for more details.
 *
 */
public class ScioDB {

    private final static Logger logger = LogManager.getLogger(ScioDB.class);

    public static void main(String[] args) {

        logger.info("Starting ScioDB...");

        final ScioDB scio = new ScioDB();
        scio.commandLine(args); // 20 - 880, 30 - 1320, 50 - 2200
    }

    private void commandLine(final String[] args) {
        final Options options = new Options();

        options.addOption("h", "help", false, "help, show this message");
        options.addOption("v", "version", false, "current version of the software");
        options.addOption("p", "port", true, "port for the database");
        options.addOption("s", "seeds", true, "seeds nodes to connect");

        try {
            final CommandLineParser parser = new DefaultParser();
            final CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("h")) {
                final HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp( "h", options );
            } else if (cmd.hasOption("v")) {
                logger.info("ScioDB");
                logger.info("v. 0.1");
            } else {
                final String p = cmd.getOptionValue("p");
                int port = p != null ? Integer.parseInt(p) : Configuration.getInstance().getPort();

                final Node node = new Node();
                try {
                    node.setHost(Inet4Address.getLocalHost().getHostAddress());
                } catch (final UnknownHostException e) {
                    node.setHost("0.0.0.0");
                }
                node.setPort(port);

                final String s = cmd.getOptionValue("s");

                final List<Node> seeds = SeedUtils.fromString(s);
                starting(node, seeds);
            }

        } catch (ParseException e) {
            logger.error("Error parsing command line options", e);
        }
    }

    private void starting(final Node node, final List<Node> seeds) {
        logger.info(node.getHost() + " - " + node.getPort());
        try {
            new Thread(new ServerSocket(null, node.getPort())).start();
            new Thread(new TopologyRunnable(node, seeds)).start();

            final Runtime runtime = Runtime.getRuntime();
            runtime.addShutdownHook(new Thread(new Shutdown()));

        } catch (Exception e) {
            logger.error("Impossible to start the database", e);
        }
    }

    private static class Shutdown implements Runnable {
        @Override
        public void run() {
            TopologyContainer.getInstance().leaveNetwork();
            logger.info("Stopped!");
        }
    }

}
