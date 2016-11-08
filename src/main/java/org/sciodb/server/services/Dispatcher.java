package org.sciodb.server.services;

import org.sciodb.messages.Operations;
import org.sciodb.messages.impl.ContainerMessage;

/**
 * @author Jesús Navarrete (29/02/16)
 */
public class Dispatcher {

    private StatusCommand statusCommand;

    private EchoCommand echoCommand;

    public Dispatcher() {
        this.echoCommand = new EchoCommand();
        this.statusCommand = new StatusCommand();
    }

    public byte[] getService(final ContainerMessage message) {
        final Command s;

        final Operations op = Operations.values()[message.getHeader().getOperationId()];
        switch (op) {
            case ECHO:
                s = echoCommand;
                break;
            case STATUS:
                s = statusCommand;
                break;
            default:
                throw new RuntimeException("Operation not allowed !!");
        }

        return s.operation(message.getContent());

    }

}
