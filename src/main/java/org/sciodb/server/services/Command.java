package org.sciodb.server.services;

/**
 * @author jesus.navarrete  (29/02/16)
 */
public interface Command {

    byte[] operation(final byte[] command);

}
