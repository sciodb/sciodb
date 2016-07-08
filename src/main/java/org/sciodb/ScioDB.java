package org.sciodb;

import org.apache.commons.cli.*;
import org.apache.log4j.Logger;
import org.sciodb.messages.impl.Node;
import org.sciodb.server.TopologySocket;
import org.sciodb.server.nessy.NodeMapper;
import org.sciodb.server.nessy.TopologyRunnable;
import org.sciodb.utils.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;


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

    private String host;
    private int port;
    private int topologyPort;

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
//                topologyPort = port + 1; TODO remove??
                // properties without option in the command line yet
                host = Configuration.getInstance().getHost();


                // TODO temporal reading for testing: decide where, when and how to do it !
                final String confFile = cmd.getOptionValue("c");

                final InputStream stream = ScioDB.class.getClassLoader().getResourceAsStream(confFile);

                final Scanner s = new Scanner(stream).useDelimiter("\\A");
                final String text = s.hasNext() ? s.next() : "";
                final Node node = NodeMapper.toNode(text);
                topologyPort = node.getPort();

                starting(node);
            }

        } catch (ParseException e) {
            logger.error("Error parsing command line options", e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void starting(final Node node) {
        logger.info(host + " - " + port);
        try {

//            new Thread(new ServerSocket(host, port)).start();
            new Thread(new TopologySocket(host, topologyPort, "type")).start();
//            new Thread(new TopologyRunnable(new Node(host, topologyPort))).start();
            new Thread(new TopologyRunnable(node)).start();

        } catch (Exception e) {
            logger.error("Impossible to start the database", e);
        }
    }
}
