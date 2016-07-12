package org.sciodb.server.services;

/**
 * @author jesus.navarrete  (24/03/16)
 */
public class EchoCommand implements Command {

    @Override
    public byte[] operation(byte[] command) {
        final String result = "time: " + System.currentTimeMillis();

        return new byte[0];
    }

}
