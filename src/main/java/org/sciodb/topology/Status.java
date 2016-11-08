package org.sciodb.topology;

/**
 * @author Jesús Navarrete (13/07/16)
 */
public enum Status {
    UP(1), SYNC(2), DOWN(3);

    private final int value;

    Status(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
