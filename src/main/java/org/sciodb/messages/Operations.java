package org.sciodb.messages;

/**
 * This class contains the code and name operations that the nodes can manage.
 *
 * @author Jesús Navarrete (23/04/16)
 */
public enum Operations {
    // Public simple operations
    STATUS(1),

    // Internal operations between nodes
    ADD_NODE(30), DISCOVER_PEERS(31), SHARE_SNAPSHOT(32),

    // Database operations
    DATABASE_STATUS(40), OP_ECHO(41);

    private final int value;

    Operations(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
