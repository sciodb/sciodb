package org.sciodb.topology;

import org.apache.log4j.Logger;
import org.sciodb.exceptions.CommunicationException;
import org.sciodb.exceptions.SeedException;
import org.sciodb.messages.Operations;
import org.sciodb.messages.impl.ContainerMessage;
import org.sciodb.messages.impl.Node;
import org.sciodb.messages.impl.NodeMessage;
import org.sciodb.messages.impl.NodesMessage;
import org.sciodb.utils.SocketClient;

import java.util.*;

/**
 * @author Jesús Navarrete (03/10/15)
 */
public class NodeOperations {

    private final static Logger logger = Logger.getLogger(NodeOperations.class);

    public static String joinNetwork(final Node me, final Node seed) {
        final NodeMessage message = new NodeMessage();
        message.setNode(me);

        final ContainerMessage container = new ContainerMessage();
        container.getHeader().setOperationId(Operations.JOIN_NETWORK.getValue());
        container.getHeader().setId(UUID.randomUUID().toString());

        container.setContent(message.encode());

        try {
            byte[] response = SocketClient.sendToSocket(seed.getHost(), seed.getPort(), container, true);

            final ContainerMessage parseRsp = new ContainerMessage();
            parseRsp.decode(response);

            final NodeMessage msg = new NodeMessage();
            msg.decode(parseRsp.getContent());

            return msg.getNode().getGuid();
        } catch (final CommunicationException e) {
            logger.error("Node not added to Seed, reason " + e.getLocalizedMessage());
            return "";
        }
    }

    public static void leaveNetwork(final Node me, final List<Node> peers) {
        for (final Node p: peers) {
            leaveNetwork(me, p);
        }
    }

    public static boolean leaveNetwork(final Node me, final Node target) {
        final NodeMessage message = new NodeMessage();
        message.setNode(me);

        final ContainerMessage container = new ContainerMessage();
        container.getHeader().setOperationId(Operations.LEAVE_NETWORK.getValue());
        container.getHeader().setId(UUID.randomUUID().toString());

        container.setContent(message.encode());

        try {
            SocketClient.sendToSocket(target.getHost(), target.getPort(), container, false);

            return true;
        } catch (final CommunicationException e) {
            return false;
        }
    }

    public static List<Node> copyRoutingTable(final Node me, final Node peer) throws CommunicationException {
        final NodeMessage message = new NodeMessage();
        message.setNode(me);

        final ContainerMessage container = new ContainerMessage();
        container.getHeader().setOperationId(Operations.COPY_ROUTING_TABLE.getValue());
        container.getHeader().setId(UUID.randomUUID().toString());

        container.setContent(message.encode());

        try {
            byte[] response = SocketClient.sendToSocket(peer.getHost(), peer.getPort(), container, true);

//            final ContainerMessage parseRsp = new ContainerMessage();
//            parseRsp.decode(response);
//
//            final NodeMessage msg = new NodeMessage();
//            msg.decode(parseRsp.getContent());

            return nodesFromResponse(me, response);
        } catch (final CommunicationException e) {
            throw new CommunicationException("Node not added to Seed, reason " + e.getLocalizedMessage());
        }
    }

    public static Node findClosest(final Node me, final Node peer) {
        final NodeMessage message = new NodeMessage();
        message.setNode(me);

        final ContainerMessage container = new ContainerMessage();
        container.getHeader().setOperationId(Operations.JOIN_NETWORK.getValue());
        container.getHeader().setId(UUID.randomUUID().toString());

        container.setContent(message.encode());

        try {
            byte[] response = SocketClient.sendToSocket(peer.getHost(), peer.getPort(), container, true);

            final ContainerMessage parseRsp = new ContainerMessage();
            parseRsp.decode(response);

            final NodeMessage msg = new NodeMessage();
            msg.decode(parseRsp.getContent());

            return msg.getNode();
        } catch (final CommunicationException e) {
            logger.error("Node not added to Seed, reason " + e.getLocalizedMessage());
            return null;
        }
    }

//    public static List<Node> discoverPeer(final Node me, final Node seed) {
//        final List<Node> result = new ArrayList<>();
//        try {
//            final NodeMessage message = new NodeMessage();
//            message.setNode(me);
//
//            final ContainerMessage container = new ContainerMessage();
//            container.getHeader().setLength(0);
//            container.getHeader().setOperationId(Operations.DISCOVER_PEERS.getValue());
//            container.getHeader().setId(UUID.randomUUID().toString());
//
//            container.setContent(message.encode());
//
//            byte[] response = SocketClient.sendToSocket(seed.getHost(),
//                                                        seed.getPort(),
//                                                        container,
//                                                        true);
//
//            result.addAll(getNodesFromResponse(me, response));
//        } catch (CommunicationException e) {
//           logger.error("Discovering peers failing because: " + e.getLocalizedMessage());
//        }
//        return result;
//    }

    public static boolean isAlive(final Node me, final Node node) {
        final NodeMessage message = new NodeMessage();
        message.setNode(me);

        final ContainerMessage container = new ContainerMessage();
        container.getHeader().setOperationId(Operations.STATUS.getValue());
        container.getHeader().setId(UUID.randomUUID().toString());

        container.setContent(message.encode());

        try {
            SocketClient.sendToSocket(node.getHost(), node.getPort(), container, false);

            return true;
        } catch (final CommunicationException e) {
            return false;
        }
    }

//    public static List<Node> getNetworkSnapshot(final Node me, final Node node) {
//        final List<Node> result = new ArrayList<>();
//
//        final NodeMessage message = new NodeMessage();
//        message.setNode(me);
//
//        final ContainerMessage container = new ContainerMessage();
//        container.getHeader().setOperationId(Operations.SHARE_SNAPSHOT.getValue());
//        container.getHeader().setId(UUID.randomUUID().toString());
//
//        container.setContent(message.encode());
//
//        try {
//            byte[] response = SocketClient.sendToSocket(node.getHost(), node.getPort(), container, true);
//
//            result.addAll(getNodesFromResponse(me, response));
//        } catch (final CommunicationException e) {
//            logger.error("Node " + node.url() + " is not alive, because... " + e.getLocalizedMessage());
//        }
//        return result;
//    }

//    public static boolean join(final Node me, final Node node) {
//        final NodeMessage message = new NodeMessage();
//        message.setNode(me);
//
//        final ContainerMessage container = new ContainerMessage();
//        container.getHeader().setOperationId(Operations.ADD_NODE.getValue());
//        container.getHeader().setId(UUID.randomUUID().toString());
//
//        container.setContent(message.encode());
//
//        try {
//            SocketClient.sendToSocket(node.getHost(), node.getPort(), container, false);
//
//            return true;
//        } catch (final CommunicationException e) {
//            return false;
//        }
//    }

//    public static boolean distributeSnapshot(final List<Node> nodes, final Node root) {
//        final NodesMessage message = new NodesMessage();
//        message.getNodes().addAll(nodes);
//
//        final ContainerMessage container = new ContainerMessage();
//        container.getHeader().setOperationId(Operations.SHARE_SNAPSHOT.getValue());
//        container.getHeader().setId(UUID.randomUUID().toString());
//
//        container.setContent(message.encode());
//
//        try {
//            SocketClient.sendToSocket(root.getHost(), root.getPort(), container, false);
//
//            return true;
//        } catch (final CommunicationException e) {
//            return false;
//        }
//    }

    private static List<Node> nodesFromResponse(final Node me, final byte[] response) {

        if (response.length > 4) {
            final ContainerMessage parseRsp = new ContainerMessage();
            parseRsp.decode(response);

            final NodesMessage msg = new NodesMessage();
            msg.decode(parseRsp.getContent());
            final List<Node> nodes = new ArrayList<>();
            msg.getNodes().stream().filter(n -> !me.equals(n) && !nodes.contains(n)).forEach(nodes::add);
            return nodes;
        }
        return new ArrayList<>();
    }

//    private static List<Node> getNodesFromResponse(final Node me, final byte[] response) {
//        final List<Node> nodes = new LinkedList<>();
//        nodesFromResponse(me, response, nodes);
//        return nodes;
//    }

}
