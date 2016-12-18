package org.sciodb.exceptions;

/**
 * Created by jesusnavarrete on 27/11/2016.
 */
public class SeedException extends Exception {
    public SeedException() {
        super();
    }

    public SeedException(String message) {
        super(message);
    }

    public SeedException(String message, Throwable cause) {
        super(message, cause);
    }

    public SeedException(Throwable cause) {
        super(cause);
    }

    protected SeedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
