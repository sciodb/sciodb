package org.sciodb.server.services;

import org.sciodb.utils.models.Command;

/**
 * @author jesus.navarrete  (29/02/16)
 */
public class Dispatcher {

    private StatusService statusService;

    public Dispatcher() {
        this.statusService = new StatusService();
    }

    public byte[] getService(final Command input) {
        final Services s;

        switch (input.getOperationID()) {
            case "status":
                s = statusService;
                break;
            default:
                throw new RuntimeException("Operation not allowed !!");
        }

        return s.operation(input);

    }

}
