package org.sciodb;

import org.apache.commons.cli.*;
import org.apache.log4j.Logger;
import org.sciodb.messages.impl.Node;
import org.sciodb.server.ServerSocket;
import org.sciodb.server.nessy.TopologyRunnable;
import org.sciodb.utils.Configuration;

import java.net.Inet4Address;
import java.net.UnknownHostException;


/**
 * Starter of the database based on the parameters of the command line.
 *
 * @author jesus.navarrete  (21/02/16)
 *
 * Copyright (C) 2016 Jesús Navarrete <jenaiz@alblang.org>
 *
 * This source code is licensed under the GNU General Public License,
 * Version 2.  See the file COPYING for more details.
 *
 */
public class ScioDB {

    private final static Logger logger = Logger.getLogger(ScioDB.class);

    private int port;

    public static void main(String[] args) {

        logger.info("Starting ScioDB...");

        final ScioDB scio = new ScioDB();
        scio.commandLine(args);

        logger.info("Stopped!");
    }

    public void commandLine(final String[] args) {
        final Options options = new Options();

        options.addOption("h", "help", false, "help, show this message");
        options.addOption("v", "version", false, "current version of the software");
        options.addOption("p", "port", true, "port for the database");
        options.addOption("s", "seeds", true, "seeds nodes to connect"); // idea from cassandra !!
        options.addOption("c", "conf", true, "configuration by file");

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
                port = p != null? Integer.valueOf(p) : Configuration.getInstance().getPort();

                // TODO temporal reading for testing: decide where, when and how to do it !
                final String confFile = cmd.getOptionValue("c");

//                if (confFile != null && !"".equals(confFile)) {
//
//                    final InputStream stream = ScioDB.class.getClassLoader().getResourceAsStream(confFile);
//
//                    final Scanner s = new Scanner(stream).useDelimiter("\\A");
//                    final String text = s.hasNext() ? s.next() : "";
//                    node = NodeMapper.toNode(text);
//                }
                final Node node = new Node();
                try {
                    node.setHost(Inet4Address.getLocalHost().getHostAddress());
//                    System.out.println(Inet4Address.getLocalHost().getHostName());
                } catch (UnknownHostException e) {
                    node.setHost(Configuration.getInstance().getHost()); // TODO by default, should we use this IP?
                }
                node.setPort(port);

                final String s = cmd.getOptionValue("s");

                String[] seeds;
                if (s != null) {
                    seeds = s.split(",");
                } else {
                    seeds = new String[0];
                }
                starting(node, seeds);
            }

        } catch (ParseException e) {
            logger.error("Error parsing command line options", e);
        }
    }

    public void starting(final Node node, final String[] seeds) {
        logger.info(node.getHost() + " - " + node.getPort());
        try {

            new Thread(new ServerSocket(node.getHost(), node.getPort())).start();
            new Thread(new TopologyRunnable(node, seeds)).start();

        } catch (Exception e) {
            logger.error("Impossible to start the database", e);
        }
    }
}
