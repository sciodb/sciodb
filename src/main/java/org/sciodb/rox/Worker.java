package org.sciodb.rox;

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

/**
 * @author jesus.navarrete  (25/07/16)
 */
public class Worker implements Runnable {

    private Dispatcher dispatcher;

    public Worker() {
        this.dispatcher = new Dispatcher();
    }

    @Override
    public void run() {

    }

    public void processData(final NioServerBeta nioServer, final SocketChannel socketChannel, final byte[] input) {

        if (input != null && input.length > 0) {
            final ContainerMessage request = new ContainerMessage();
            request.decode(input);
            int operationId = request.getHeader().getOperationId();
            if (operationId == Operations.DISCOVERY_PEERS.getValue()) {
                final NodeMessage nodeMessage = new NodeMessage();
                nodeMessage.decode(request.getContent());

                TopologyContainer.getInstance().addNode(nodeMessage.getNode());

                final Queue<Node> nodes = TopologyContainer.getInstance().getAvailableNodes();
                final NodesMessage n = new NodesMessage();
                for (Node nn : nodes) {
                    n.getNodes().add(nn);
                }
                final ContainerMessage response = new ContainerMessage();
                response.getHeader().setId(UUID.randomUUID().toString());
                response.getHeader().setOperationId(Operations.DISCOVERY_PEERS.getValue());

                response.setContent(n.encode());

                nioServer.send(socketChannel, response.encode());
            } else if (operationId == Operations.STATUS.getValue()) {
//                nioServer.send(socketChannel, new byte[0]);
            } else if (request.getHeader().getOperationId() == Operations.ECHO.getValue()) {

                final byte [] response = dispatcher.getService(request);

                nioServer.send(socketChannel, response);
            }
        }
    }
}
