package org.sciodb.messages.impl;

import org.sciodb.messages.Decoder;
import org.sciodb.messages.Encoder;
import org.sciodb.messages.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jesus.navarrete  (11/07/16)
 */
public class NodeListMessage implements Message {

    private List<Node> nodes;

    public NodeListMessage() {
        nodes = new ArrayList<>();
    }

    @Override
    public byte[] encode() {
        final Encoder encoder = new Encoder();

        for (final Node n: nodes) {
            encoder.in(n.encode());
        }

        return encoder.container();
    }

    @Override
    public void decode(byte[] input) {
        final Decoder d = new Decoder(input);

        byte[] bytes = d.getByteArray();
        while (bytes != null && bytes.length > 0) {
            final Node n = new Node();
            n.decode(bytes);
            nodes.add(n);
            bytes = d.getByteArray();
        }
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }
}
