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
 * @author Jesús Navarrete (03/10/15)
 */
public class NodeOperations {

    private final static Logger logger = Logger.getLogger(NodeOperations.class);

    private Node me;

    public NodeOperations(final Node me) {
        this.me = me;
    }

    /**
     * The ping operation probes if a node is online, no response is required.
     */
    public boolean ping(final Node target) {
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

    /**
     * The store operation send a <key, value> pair for later retrieval.
     *
     * @param target
     * @return
     */
    public String store(final Node target) {
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

//        behaves like FIND NODE—returning
//        <IP address, UDP port, Node ID> triples—with one
//        exception. If the RPC recipient has received a STORE
//        RPC for the key, it just returns the stored value.
    public static void findValue() {
    }

    private ContainerMessage getMessage(final int op, final byte[] encoded) {
        final ContainerMessage container = new ContainerMessage();

        container.getHeader().setOperationId(op);
        container.getHeader().setId(UUID.randomUUID().toString());
        container.setContent(encoded);

        return container;
    }

    /**
     * The find-node operation send the GUID of the node and retrieves:
     * - in case the node was not executing store operation before: it retrieves a list of
     *   <IP address, UDP port, Node ID> triples for the k nodes closest to the target ID.
     *
     *   TODO investigate return a list of triples and not a list of nodes.
     *
     * @param target
     * @return a list of k closest nodes.
     * @throws CommunicationException
     */
    public List<Node> findNode(final Node target) throws CommunicationException {

        final NodeMessage message = new NodeMessage();
        message.setNode(me);

        final ContainerMessage container = new ContainerMessage();
        container.getHeader().setOperationId(Operations.FIND_NODE.getValue());
        container.getHeader().setId(UUID.randomUUID().toString());

        container.setContent(message.encode());

        try {
            byte[] response = SocketClient.sendToSocket(target.getHost(), target.getPort(), container, true);

            return nodesFromResponse(response);
        } catch (final CommunicationException e) {
            throw new CommunicationException("Node not added to Seed, reason " + e.getLocalizedMessage());
        }
    }

    /**
     * The ping operation probes if a node is online, no response is required.
     */
    public boolean leave(final Node peer) {
        final NodeMessage message = new NodeMessage();
        message.setNode(me);

        final ContainerMessage container = getMessage(Operations.LEAVE.getValue(), message.encode());

        try {
            SocketClient.sendToSocket(peer.getHost(), peer.getPort(), container, false);

            return true;
        } catch (final CommunicationException e) {
            return false;
        }
    }

    private List<Node> nodesFromResponse(final byte[] response) {

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
