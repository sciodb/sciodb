package org.sciodb.server.services;

        import org.apache.log4j.Logger;
        import org.sciodb.utils.models.Command;

/**
 * @author jesus.navarrete  (09/03/16)
 */
public class StatusService implements Services {

    private final static Logger logger = Logger.getLogger(StatusService.class.getName());

    @Override
    public void operation(final Command command) {
        logger.info("Status Sevice - executed !!!");
        logger.info("status - " + command.getOperationID());
    }

}
