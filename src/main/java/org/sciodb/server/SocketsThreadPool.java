package org.sciodb.server;

import com.sun.corba.se.spi.orb.Operation;
import org.apache.log4j.Logger;
import org.sciodb.exceptions.EmptyDataException;
import org.sciodb.messages.Operations;
import org.sciodb.messages.impl.ContainerMessage;
import org.sciodb.messages.impl.Node;
import org.sciodb.messages.impl.NodeMessage;
import org.sciodb.messages.impl.NodesMessage;
import org.sciodb.server.services.Dispatcher;
import org.sciodb.topology.TopologyContainer;
import org.sciodb.utils.StringUtils;

import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Jesús Navarrete (07/04/16)
 */
public class SocketsThreadPool {

    final static private Logger logger = Logger.getLogger(SocketsThreadPool.class);

    private ExecutorService service;
    private Dispatcher dispatcher;
    private static SocketsThreadPool instance;

    private SocketsThreadPool() {
        this.service = Executors.newFixedThreadPool(100);
        this.dispatcher = new Dispatcher();
    }

    public static SocketsThreadPool getInstance() {
        if (instance == null) {
            instance = new SocketsThreadPool();
        }
        return instance;
    }
    public void run(final ServerSocket serverSocket, final SocketChannel channel, final byte[] result) {

        service.execute(() -> execute(serverSocket, channel, result));
    }

    private void execute(final ServerSocket server, final SocketChannel channel, final byte[] input) {
        if (input != null && input.length > 0) {
            final ContainerMessage request = new ContainerMessage();
            request.decode(input);

            int operationId = request.getHeader().getOperationId();
            final Node source = readRequest(request);

            if (StringUtils.isNotEmpty(source.getGuid())) {
                TopologyContainer.getInstance().join(source); // always add the node to the k-buckets
            }

            if (operationId == Operations.PING.getValue()) {
                server.send(channel, new byte[0]);
            } else if (operationId == Operations.STORE.getValue()) {
                    // store <key, value> ...
                if (StringUtils.isEmpty(source.getGuid())) {
                    source.setGuid(UUID.randomUUID().toString());
                }
                TopologyContainer.getInstance().join(source);
                final NodeMessage nodeMessage = new NodeMessage();
                nodeMessage.setNode(source);

                final ContainerMessage response = getMessageForJoiners(operationId, nodeMessage);

                server.send(channel, response.encode());
            } else if (operationId == Operations.FIND_NODE.getValue()) {
                final List<Node> peers = TopologyContainer.getInstance().getPeers(source);
                server.send(channel, getContainerMessageForPeers(operationId, peers).encode());
            } else if (operationId == Operations.FIND_VALUE.getValue()) {

                try {
                    final Node node = TopologyContainer.getInstance().check(source);

                    final NodeMessage nodeMessage = new NodeMessage();
                    nodeMessage.setNode(node);

                    server.send(channel, getMessageForJoiners(operationId, nodeMessage).encode());
                } catch (EmptyDataException e) {
                    final NodeMessage nodeMessage = new NodeMessage();
                    nodeMessage.setNode(new Node("", 0)); // TODO MOVE TO NOTHING

                    server.send(channel, getMessageForJoiners(operationId, nodeMessage).encode());
                }
            } else {
                final byte[] response = dispatcher.getService(request);

                server.send(channel, response);
            }
        }
    }

    private Node readRequest(final ContainerMessage message) {
        final NodeMessage nodeMessage = new NodeMessage();
        nodeMessage.decode(message.getContent());

        return nodeMessage.getNode();
    }

    private ContainerMessage getContainerMessageForPeers(final int id, final List<Node> peers) {
        final NodesMessage n = new NodesMessage();
        n.getNodes().addAll(peers);

        return setupContainerMessageFor(id, n.encode());
    }

    private static ContainerMessage getMessageForJoiners(final int id, final NodeMessage peer) {
        return setupContainerMessageFor(id, peer.encode());
    }

    private static ContainerMessage setupContainerMessageFor(final int id, final byte[] message) {
        final ContainerMessage response = new ContainerMessage();
        response.getHeader().setId(UUID.randomUUID().toString());
        response.getHeader().setOperationId(id);

        response.setContent(message);

        return response;
    }

}
