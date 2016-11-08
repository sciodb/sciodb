package org.sciodb.server.services;

/**
 * @author Jesús Navarrete (29/02/16)
 */
public interface Command {

    byte[] operation(final byte[] command);

}
