package org.sciodb.exceptions;

/**
 * @author Jesús Navarrete (08/06/16)
 */
public class CommunicationException extends Exception {

    public CommunicationException(String message) {
        super(message);
    }

    public CommunicationException(String message, Throwable cause) {
        super(message, cause);
    }

}
