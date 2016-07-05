package org.sciodb.server;

import org.apache.log4j.Logger;
import org.sciodb.messages.Operations;
import org.sciodb.messages.impl.NodeMessage;
import org.sciodb.server.nessy.NodeOperations;
import org.sciodb.server.nessy.TopologyContainer;

import java.io.IOException;
import java.nio.channels.SelectionKey;

/**
 * @author jesus.navarrete  (30/05/16)
 */
public class TopologySocket extends AbstractSocket {

    private final static Logger logger = Logger.getLogger(NodeOperations.class);

    private OneMasterTopology oneMaster;

    public TopologySocket(final String address, final int port, final String type) throws IOException {
        super(address, port);
        logger.info("Starting Topology :: " + type);

        oneMaster = new OneMasterTopology();
    }

    @Override
    public void read(final SelectionKey key) {
        final NodeCommunicationReader reader = new NodeCommunicationReader(key);
        byte[] result = reader.getMessage();

        if (result != null && result.length > 0) {
            final NodeMessage node = new NodeMessage();
            node.decode(result);

            if (node.getHeader().getOperationId() == Operations.ADD_SLAVE_NODE.getValue()) {
                TopologyContainer.getInstance().addNode(node.getNode());
            } else if (node.getHeader().getOperationId() == Operations.CHECK_NODE_STATUS.getValue()) {
                logger.info("Root OK - checked by" + node.getNode().url());
            }
        }
        reader.close();
    }
}
