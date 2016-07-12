package org.sciodb.server;

import org.apache.log4j.Logger;
import org.sciodb.messages.Operations;
import org.sciodb.messages.impl.ContainerMessage;
import org.sciodb.messages.impl.Node;
import org.sciodb.messages.impl.NodeListMessage;
import org.sciodb.server.nessy.TopologyContainer;
import org.sciodb.server.services.Dispatcher;
import org.sciodb.utils.SocketClient;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.List;
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

    public void run(final SelectionKey key) {

        final NodeCommunicationReader reader = new NodeCommunicationReader(key);
        byte[] result = reader.getMessage();

        if (result != null && result.length > 0) {
            final ContainerMessage message = new ContainerMessage();
            message.decode(result);

            if (message.getHeader().getOperationId() == Operations.ADD_SLAVE_NODE.getValue()) {
                final Node node = new Node();
                node.decode(message.getContent());
                TopologyContainer.getInstance().addNode(node);
            } else if (message.getHeader().getOperationId() == Operations.CHECK_NODE_STATUS.getValue()) {
                // TODO response with all the nodes in the local network
                final List<Node> nodes = TopologyContainer.getInstance().getNodes();

                final NodeListMessage msg = new NodeListMessage();
                msg.setNodes(nodes);

                final ContainerMessage container = new ContainerMessage();
                container.getHeader().setId(UUID.randomUUID().toString());
                container.getHeader().setOperationId(Operations.CHECK_NODE_STATUS.getValue());
                container.setContent(msg.encode());

//                sendResponse(reader, container.encode());
            } else if (message.getHeader().getOperationId() == Operations.ECHO.getValue()) {

                service.execute(() -> execute(reader, message));
            }
        }
    }

    private void execute(final NodeCommunicationReader reader, final ContainerMessage message) {
        final byte [] response = dispatcher.getService(message);

        if (response != null && response.length > 0) {
            final SocketChannel channel = (SocketChannel) reader.getKey().channel();
            try {
                channel.register(reader.getKey().selector(), SelectionKey.OP_WRITE);
                while (true) {
                    if (reader.getKey().isWritable()) {
                        final ByteBuffer wrap = ByteBuffer.wrap(response);
                        channel.write(SocketClient.messageLength(wrap.array().length));
                        channel.write(wrap);

                        reader.getKey().interestOps(SelectionKey.OP_READ);
                        break;
                    } else if (!reader.getKey().isValid()) {
                        break;
                    } else {
                        logger.error("THE SOCKET WILL BE IN WRITE MODE FOREVER !!!");
                    }
                }
            } catch (IOException e) {
                logger.error("Not possible to write in socket", e);
            }
        }
        reader.close();

    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        service.shutdown();
    }

}
