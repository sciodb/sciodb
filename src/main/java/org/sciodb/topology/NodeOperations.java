package org.sciodb.topology;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sciodb.exceptions.CommunicationException;
import org.sciodb.messages.Operations;
import org.sciodb.messages.impl.ContainerMessage;
import org.sciodb.messages.impl.Node;
import org.sciodb.messages.impl.NodesMessage;
import org.sciodb.utils.TcpClient;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Jesús Navarrete (03/10/15)
 */
public class NodeOperations {

    private final static Logger logger = LogManager.getLogger(NodeOperations.class);

    private final Node me;

    public NodeOperations(final Node me) {
        this.me = me;
    }

    /**
     * The ping operation probes if a node is online, no response is required.
     */
    public boolean ping(final Node target) {
        final ContainerMessage container = getMessage(Operations.PING.getValue(), me.encode());

        try {
            TcpClient.sendToSocket(target.getHost(), target.getPort(), container, false);

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

        final ContainerMessage container = getMessage(Operations.STORE.getValue(), me.encode());

        try {
            byte[] response = TcpClient.sendToSocket(target.getHost(), target.getPort(), container, true);

            final ContainerMessage parseRsp = new ContainerMessage();
            parseRsp.decode(response);

            final Node msg = new Node();
            msg.decode(parseRsp.getContent());

            return msg.getGuid();
        } catch (final CommunicationException e) {
            logger.error("Node not added to Seed, reason " + e.getLocalizedMessage());
            return "";
        }
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

        final ContainerMessage container = new ContainerMessage();
        container.getHeader().setOperationId(Operations.FIND_NODE.getValue());
        container.getHeader().setId(UUID.randomUUID().toString());

        container.setContent(me.encode());

        try {
            byte[] response = TcpClient.sendToSocket(target.getHost(), target.getPort(), container, true);

            return nodesFromResponse(response);
        } catch (final CommunicationException e) {
            throw new CommunicationException("Node not added to Seed, reason " + e.getLocalizedMessage());
        }
    }

    /**
     * The ping operation probes if a node is online, no response is required.
     */
    public void leave(final Node peer) {
        final ContainerMessage container = getMessage(Operations.LEAVE.getValue(), me.encode());

        try {
            TcpClient.sendToSocket(peer.getHost(), peer.getPort(), container, false);

        } catch (final CommunicationException e) {
            /* not important */
        }
    }

    private List<Node> nodesFromResponse(final byte[] response) {

        final List<Node> nodes = new ArrayList<>();

        if (response.length > 4) {
            final ContainerMessage parseRsp = new ContainerMessage();
            parseRsp.decode(response);

            final NodesMessage msg = new NodesMessage();
            msg.decode(parseRsp.getContent());
            msg.getNodes()
                    .stream()
                    .filter(n -> !me.equals(n) && !nodes.contains(n))
                    .forEach(nodes::add);
        }

        return nodes;
    }

}
