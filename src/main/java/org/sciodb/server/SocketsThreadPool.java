package org.sciodb.server;

import org.apache.log4j.Logger;
import org.sciodb.messages.Operations;
import org.sciodb.messages.impl.ContainerMessage;
import org.sciodb.messages.impl.Node;
import org.sciodb.messages.impl.NodeMessage;
import org.sciodb.messages.impl.NodesMessage;
import org.sciodb.server.services.Dispatcher;
import org.sciodb.topology.TopologyContainer;

import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author jesus.navarrete  (07/04/16)
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
            if (operationId == Operations.STATUS.getValue()) {
                final NodeMessage nodeMessage = new NodeMessage();
                nodeMessage.decode(request.getContent());

                TopologyContainer.getInstance().addNode(nodeMessage.getNode());

                server.send(channel, new byte[0]);
            } else if (operationId == Operations.DISCOVERY_PEERS.getValue()) {
                final NodeMessage nodeMessage = new NodeMessage();
                nodeMessage.decode(request.getContent());

                TopologyContainer.getInstance().addNode(nodeMessage.getNode());

                final ContainerMessage response = getContainerMessageForPeers(operationId);

                server.send(channel, response.encode());
            } else if (operationId == Operations.ECHO.getValue()) {

                final byte [] response = dispatcher.getService(request);

                server.send(channel, response);
            }
        }

    }

    private ContainerMessage getContainerMessageForPeers(final int id) {
        final Queue<Node> nodes = TopologyContainer.getInstance().getAvailableNodes();
        final NodesMessage n = new NodesMessage();
        for (Node nn : nodes) {
            n.getNodes().add(nn);
        }
        final ContainerMessage response = new ContainerMessage();
        response.getHeader().setId(UUID.randomUUID().toString());
        response.getHeader().setOperationId(id);

        response.setContent(n.encode());
        return response;
    }

}
