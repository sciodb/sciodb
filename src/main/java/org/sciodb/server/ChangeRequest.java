package org.sciodb.server;

import java.nio.channels.SocketChannel;

/**
 * @author Jesús Navarrete (26/07/16)
 */
public class ChangeRequest {

    public static final int REGISTER = 1;
    public static final int CHANGE_OPS = 2;

    public final SocketChannel socket;
    public final int type;
    public final int ops;

    public ChangeRequest(final SocketChannel socket, final int type, final int ops) {
        this.socket = socket;
        this.type = type;
        this.ops = ops;
    }
}
