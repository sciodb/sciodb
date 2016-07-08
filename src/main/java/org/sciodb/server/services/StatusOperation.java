package org.sciodb.server.services;

        import org.apache.log4j.Logger;
        import org.sciodb.utils.models.Command;

/**
 * @author jesus.navarrete  (09/03/16)
 */
public class StatusOperation implements Operations {

    private final static Logger logger = Logger.getLogger(StatusOperation.class.getName());

    @Override
    public byte[] operation(final Command command) {
        logger.debug("Status Sevice - executed ! - " + command.getOperationID());

        return "status operation executed!".getBytes();
    }

}
