package org.sciodb.messages;

/**
 * This class contains the code and name operations that the nodes can manage.
 *
 * @author Jesús Navarrete (23/04/16)
 */
public enum Operations {
    // Public simple operations
    ECHO(1), STATUS(2),

    // Internal operations between nodes
    ADD_NODE(30), DISCOVER_PEERS(31);

    private final int value;

    Operations(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
