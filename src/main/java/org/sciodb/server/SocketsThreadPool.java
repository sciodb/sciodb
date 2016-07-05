package org.sciodb.server;

import org.apache.log4j.Logger;
import org.sciodb.server.services.Dispatcher;
import org.sciodb.utils.CommandEncoder;
import org.sciodb.utils.models.Command;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
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

        // TODO parse the message, decide between priority task or normal task and queue

        final MessageReader reader = new MessageReader(key);
        final String msg = reader.getContent();
        service.execute(() -> execute(reader, msg) );

//        final NodeCommunicationReader r = new NodeCommunicationReader(key);
//        final byte[] result = r.getMessage();
//
//        final Node node = new Node();
//        node.decode(result);
//
//        TopologyContainer.getInstance().addNode(node);
    }

    private void execute(final MessageReader reader, final String message) {
        logger.debug(" message --> " + message);
        //
        if (message != null) {
            final Command command = CommandEncoder.decode(message);

            if (command != null && command.getOperationID() != null) {
                final byte [] response = dispatcher.getService(command);

                final SocketChannel channel = (SocketChannel)reader.getKey().channel();
                try {
                    channel.register(reader.getKey().selector(), SelectionKey.OP_WRITE);
                    while(true) {
                        if (reader.getKey().isWritable()) {
                            final ByteBuffer dummyResponse = ByteBuffer.wrap(response);

                            channel.write(dummyResponse);

                            reader.getKey().interestOps(SelectionKey.OP_READ);
                            break;
                        } else {
                            logger.error("THE SOCKET WILL BE IN WRITE MODE FOREVER !!!");
                        }
                    }
                } catch (IOException e) {
                    logger.error("Not possible to write in socket", e);
                }

                reader.close();
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        service.shutdown();
    }

}
