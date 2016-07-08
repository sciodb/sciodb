package org.sciodb.server.nessy;

import org.apache.log4j.Logger;
import org.sciodb.exceptions.CommunicationException;
import org.sciodb.messages.Operations;
import org.sciodb.messages.impl.Header;
import org.sciodb.messages.impl.Node;
import org.sciodb.messages.impl.NodeMessage;
import org.sciodb.utils.Configuration;
import org.sciodb.utils.SocketClient;

import java.util.UUID;

/**
 * @author jenaiz on 03/10/15.
 */
public class NodeOperations {

    private final static Logger logger = Logger.getLogger(NodeOperations.class);

    public static boolean addToRoot(final Node me, final Node root) {
        boolean result = false;
        try {
            final NodeMessage message = new NodeMessage();

            final Header header = new Header();
            header.setLength(0);
            header.setOperationId(Operations.ADD_SLAVE_NODE.getValue());
            header.setId(UUID.randomUUID().toString());

            message.setHeader(header);
            message.setNode(me);

            SocketClient.sendToSocket(root.getHost(),
                    root.getPort(),
                    message,
                    false);
            logger.info("Node added to Root!");
            result = true;
        } catch (CommunicationException e) {
           logger.error("Add to Root failing because: " + e.getLocalizedMessage());
        }
        return result;
    }

    public static boolean checkRoot(final Node root) {
        boolean result = false;
        try {
            final NodeMessage message = new NodeMessage();

            final Header header = new Header();
            header.setOperationId(Operations.CHECK_NODE_STATUS.getValue());
            header.setId(UUID.randomUUID().toString());

            message.setHeader(header);
            message.setNode(root);

            SocketClient.sendToSocket(Configuration.getInstance().getRootHostNessyTopology(),
                    Configuration.getInstance().getRootPortNessyTopology(),
                    message,
                    false);
            logger.info("Root status ok!");
            result = true;
        } catch (CommunicationException e) {
            logger.error(" Error communicating with root - " + e.getMessage());
        }

        return result;
    }

    public static boolean isAlife(final Node node) {
        final NodeMessage message = new NodeMessage();

        final Header header = new Header();
        header.setOperationId(Operations.CHECK_NODE_STATUS.getValue());
        header.setId(UUID.randomUUID().toString());

        message.setHeader(header);
        message.setNode(node);

        try {
            SocketClient.sendToSocket(node.getHost(), node.getPort(), message, false);
            return true;
        } catch (CommunicationException e) {
            return false;
        }

    }

}
