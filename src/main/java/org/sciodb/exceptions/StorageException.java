package org.sciodb.exceptions;

/**
 * @author Jesús Navarrete (10/04/16)
 */
public class StorageException extends Exception {

    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }

}
