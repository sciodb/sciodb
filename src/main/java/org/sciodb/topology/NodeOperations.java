package org.sciodb.topology;

import org.apache.log4j.Logger;
import org.sciodb.exceptions.CommunicationException;
import org.sciodb.messages.Operations;
import org.sciodb.messages.impl.ContainerMessage;
import org.sciodb.messages.impl.Node;
import org.sciodb.messages.impl.NodeMessage;
import org.sciodb.messages.impl.NodesMessage;
import org.sciodb.utils.SocketClient;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author jenaiz on 03/10/15.
 */
public class NodeOperations {

    private final static Logger logger = Logger.getLogger(NodeOperations.class);

    public static void main(String[] args) {

        Node me = new Node("localhost", 9091);
        Node seed = new Node("localhost", 9090);

//        for(int i = 0; i < 100; i++) {
//            discoverPeer(me, seed);
//        }

        for(int i = 0; i < 10; i++) {
            isAlife(me, seed);
        }

    }

    public static List<Node> discoverPeer(final Node me, final Node seed) {
        final List<Node> result = new ArrayList<>();
        try {
            final NodeMessage message = new NodeMessage();
            message.setNode(me);

            final ContainerMessage container = new ContainerMessage();
            container.getHeader().setLength(0);
            container.getHeader().setOperationId(Operations.DISCOVERY_PEERS.getValue());
            container.getHeader().setId(UUID.randomUUID().toString());

            container.setContent(message.encode());

            byte[] response = SocketClient.sendToSocket(seed.getHost(),
                                                        seed.getPort(),
                                                        container,
                                                        true);
            if (response.length > 4) {
                final ContainerMessage parseRsp = new ContainerMessage();
                parseRsp.decode(response);

                final NodesMessage msg = new NodesMessage();
                msg.decode(parseRsp.getContent());
                for (final Node n : msg.getNodes()) {
                    if (!me.equals(n))
                    result.add(n);
                }
            }
        } catch (CommunicationException e) {
           logger.error("Add to Root failing because: " + e.getLocalizedMessage());
        }
        return result;
    }

    public static boolean isAlife(final Node me, final Node node) {
        final NodeMessage message = new NodeMessage();
        message.setNode(me);

        final ContainerMessage container = new ContainerMessage();
        container.getHeader().setOperationId(Operations.STATUS.getValue());
        container.getHeader().setId(UUID.randomUUID().toString());

        container.setContent(message.encode());

        try {
            SocketClient.sendToSocket(node.getHost(), node.getPort(), container, false);
            return true;
        } catch (CommunicationException e) {
            return false;
        }

    }

}
