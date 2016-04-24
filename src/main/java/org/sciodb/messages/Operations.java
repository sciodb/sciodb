package org.sciodb.messages;

/**
 * @author jenaiz on 23/04/16.
 */
public enum Operations {
    ECHO(1);

    private final int value;

    Operations(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
