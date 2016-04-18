package org.sciodb;

import org.apache.commons.cli.*;
import org.apache.log4j.Logger;
import org.sciodb.server.ServerSocket;
import org.sciodb.utils.Configuration;

import java.io.IOException;


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
        options.addOption("p", "port", false, "port for the database");

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

                // properties without option in the command line yet
                host = Configuration.getInstance().getHost();

                starting();
            }

        } catch (ParseException e) {
            logger.error("Error parsing command line options", e);
        }
    }

    public void starting() {
        logger.info(host + " - " + port);
        try {
            new Thread(new ServerSocket(host, port)).start();
        } catch (IOException e) {
            logger.error("Impossible to start the database", e);
        }
    }
}
