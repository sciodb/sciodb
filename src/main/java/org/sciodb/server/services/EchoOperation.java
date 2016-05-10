package org.sciodb.server.services;

import org.sciodb.utils.models.Command;
import org.sciodb.utils.models.EchoCommand;

/**
 * @author jesus.navarrete  (24/03/16)
 */
public class EchoOperation implements Operations {

    @Override
    public byte[] operation(final Command command) {
        final String result = "time: " + System.currentTimeMillis();

        final EchoCommand echo = new EchoCommand();

        echo.setMessageID(command.getMessageID() + "-result");
        echo.setOperationID(command.getOperationID());
        echo.setMessage(result.getBytes());

        return null;

    }

}
