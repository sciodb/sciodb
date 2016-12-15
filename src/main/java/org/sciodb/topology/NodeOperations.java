package org.sciodb.topology;

import org.apache.log4j.Logger;
import org.sciodb.exceptions.CommunicationException;
import org.sciodb.exceptions.SeedException;
import org.sciodb.messages.Operations;
import org.sciodb.messages.impl.ContainerMessage;
import org.sciodb.messages.impl.Node;
import org.sciodb.messages.impl.NodeMessage;
import org.sciodb.messages.impl.NodesMessage;
import org.sciodb.topology.models.Triple;
import org.sciodb.utils.SocketClient;

import java.util.*;

/**
 * @author Jesús Navarrete (03/10/15)
 */
public class NodeOperations {

    private final static Logger logger = Logger.getLogger(NodeOperations.class);

    private Node me;

    public NodeOperations(final Node me) {
        this.me = me;
    }

    public boolean ping(final Node target) {
//        The PING
//        RPC probes a node to see if it is online.
        final NodeMessage message = new NodeMessage();
        message.setNode(me);

        final ContainerMessage container = getMessage(Operations.PING.getValue(), message.encode());

        try {
            SocketClient.sendToSocket(target.getHost(), target.getPort(), container, false);

            return true;
        } catch (final CommunicationException e) {
            return false;
        }
    }

    public String store(final Node target) {
//        instructs
//        a node to store a <key, value> pair for later
//        retrieval.
        final NodeMessage message = new NodeMessage();
        message.setNode(me);

        final ContainerMessage container = getMessage(Operations.STORE.getValue(), message.encode());

        try {
            byte[] response = SocketClient.sendToSocket(target.getHost(), target.getPort(), container, true);

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

    public static void findValue() {
//        behaves like FIND NODE—returning
//        hIP address, UDP port, Node IDi triples—with one
//        exception. If the RPC recipient has received a STORE
//        RPC for the key, it just returns the stored value.
    }

    private ContainerMessage getMessage(final int op, final byte[] encoded) {
        final ContainerMessage container = new ContainerMessage();

        container.getHeader().setOperationId(op);
        container.getHeader().setId(UUID.randomUUID().toString());
        container.setContent(encoded);

        return container;
    }

//    public static String joinNetwork(final Node me, final Node seed) {
//        final NodeMessage message = new NodeMessage();
//        message.setNode(me);
//
//        final ContainerMessage container = new ContainerMessage();
//        container.getHeader().setOperationId(Operations.JOIN_NETWORK.getValue());
//        container.getHeader().setId(UUID.randomUUID().toString());
//
//        container.setContent(message.encode());
//
//        try {
//            byte[] response = SocketClient.sendToSocket(seed.getHost(), seed.getPort(), container, true);
//
//            final ContainerMessage parseRsp = new ContainerMessage();
//            parseRsp.decode(response);
//
//            final NodeMessage msg = new NodeMessage();
//            msg.decode(parseRsp.getContent());
//
//            return msg.getNode().getGuid();
//        } catch (final CommunicationException e) {
//            logger.error("Node not added to Seed, reason " + e.getLocalizedMessage());
//            return "";
//        }
//    }

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

    public List<Node> findNode(final Node peer) throws CommunicationException {
        //        takes a 160-bit ID as an argument.
//        The recipient of a the RPC returns
//        <IP address, UDP port, Node ID> triples for the k
//        nodes it knows about closest to the target ID. These
//        triples can come from a single k-bucket, or they may
//        come from multiple k-buckets if the closest k-bucket
//        is not full. In any case, the RPC recipient must return
//                k items (unless there are fewer than k nodes in all its
//                k-buckets combined, in which case it returns every
//        node it knows about).
        final NodeMessage message = new NodeMessage();
        message.setNode(me);

        final ContainerMessage container = new ContainerMessage();
        container.getHeader().setOperationId(Operations.FIND_NODE.getValue());
        container.getHeader().setId(UUID.randomUUID().toString());

        container.setContent(message.encode());

        try {
            byte[] response = SocketClient.sendToSocket(peer.getHost(), peer.getPort(), container, true);

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

//    public static boolean ping(final Node me, final Node node) {
//        final NodeMessage message = new NodeMessage();
//        message.setNode(me);
//
//        final ContainerMessage container = new ContainerMessage();
//        container.getHeader().setOperationId(Operations.STATUS.getValue());
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

}
