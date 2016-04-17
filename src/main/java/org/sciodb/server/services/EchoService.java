package org.sciodb.server.services;

import org.sciodb.utils.models.Command;
import org.sciodb.utils.models.EchoCommand;

/**
 * @author jesus.navarrete  (24/03/16)
 */
public class EchoService implements Services {

    @Override
    public byte[] operation(Command command) {
        final String result = "time: " + System.currentTimeMillis();

        final EchoCommand echo = new EchoCommand();

//        echo.setMessage(command.getMessageID() + "-result");
        echo.setOperationID(command.getOperationID());
//        echo.setMessage(result);

        return null;

    }

}
