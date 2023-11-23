package org.sciodb.server.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Jesús Navarrete (09/03/16)
 */
public class StatusCommand implements Command {

    private final static Logger logger = LogManager.getLogger(StatusCommand.class.getName());

    @Override
    public byte[] operation(byte[] command) {
        logger.debug("Status Service - executed ! ");

        return "status operation executed!".getBytes();
    }

}
