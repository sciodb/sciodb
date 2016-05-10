package org.sciodb.server.services;

import org.sciodb.utils.models.Command;

/**
 * @author jesus.navarrete  (29/02/16)
 */
public class Dispatcher {

    private StatusOperation statusOperation;

    private EchoOperation echoOperation;

    public Dispatcher() {
        this.statusOperation = new StatusOperation();
    }

    public byte[] getService(final Command input) {
        final Operations s;

        switch (input.getOperationID()) {
            case "status":
                s = statusOperation;
                break;
            case "echo":
                s = echoOperation;
                break;
            default:
                throw new RuntimeException("Operation not allowed !!");
        }

        return s.operation(input);

    }

}
