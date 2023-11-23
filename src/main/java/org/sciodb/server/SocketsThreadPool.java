package org.sciodb.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sciodb.exceptions.EmptyDataException;
import org.sciodb.messages.Operations;
import org.sciodb.messages.impl.ContainerMessage;
import org.sciodb.messages.impl.Node;
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

    private final static Logger logger = LogManager.getLogger(SocketsThreadPool.class);

    private final ExecutorService service;
    private final Dispatcher dispatcher;
    private static SocketsThreadPool instance;

    private SocketsThreadPool() {
        this.service = Executors.newFixedThreadPool(100);
        this.dispatcher = new Dispatcher();
    }

    static SocketsThreadPool getInstance() {
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

            if (operationId == Operations.PING.getValue()) {
                server.send(channel, new byte[0]);
                final Node source = readNodeFromRequest(request);
                logger.info("PING operation executed from - " + source.url());
            } else if(operationId == Operations.LEAVE.getValue()) {
                final Node source = readNodeFromRequest(request);

                TopologyContainer.getInstance().leave(source);
            } else if (operationId == Operations.STORE.getValue()) {
                // store <key, value> ...
                final Node source = readNodeFromRequest(request);

                if (StringUtils.isEmpty(source.getGuid())) {
                    source.setGuid(UUID.randomUUID().toString());
                }
                TopologyContainer.getInstance().join(source);

                final ContainerMessage response = getMessageForJoiners(operationId, source);

                server.send(channel, response.encode());
            } else if (operationId == Operations.FIND_NODE.getValue()) {
                final Node source = readNodeFromRequest(request);

                final List<Node> peers = TopologyContainer.getInstance().getPeers(source);
                server.send(channel, getContainerMessageForPeers(operationId, peers).encode());
            } else if (operationId == Operations.FIND_VALUE.getValue()) {
                final Node source = readNodeFromRequest(request);

                try {
                    final Node node = TopologyContainer.getInstance().check(source);

                    server.send(channel, getMessageForJoiners(operationId, node).encode());
                } catch (EmptyDataException e) {

                    server.send(channel, getMessageForJoiners(operationId, new Node("", 0)).encode()); // TODO MOVE new Node... TO NOTHING
                }
            } else {
                final byte[] response = dispatcher.getService(request);

                server.send(channel, response);
            }
        }
    }

    private Node readNodeFromRequest(final ContainerMessage message) {
        final Node source = new Node();
        source.decode(message.getContent());

        if (StringUtils.isNotEmpty(source.getGuid())) {
            TopologyContainer.getInstance().join(source); // always add the node to the k-buckets
        }
        return source;
    }

    private ContainerMessage getContainerMessageForPeers(final int id, final List<Node> peers) {
        final NodesMessage n = new NodesMessage();
        n.getNodes().addAll(peers);

        return setupContainerMessageFor(id, n.encode());
    }

    private static ContainerMessage getMessageForJoiners(final int id, final Node peer) {
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
