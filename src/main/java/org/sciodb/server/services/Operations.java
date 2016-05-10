package org.sciodb.server.services;

import org.sciodb.utils.models.Command;

/**
 * @author jesus.navarrete  (29/02/16)
 */
public interface Operations {

    byte[] operation(final Command command);

}
