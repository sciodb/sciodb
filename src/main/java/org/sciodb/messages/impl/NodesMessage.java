package org.sciodb.messages.impl;

import org.sciodb.messages.Decoder;
import org.sciodb.messages.Encoder;
import org.sciodb.messages.Message;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Jesús Navarrete (11/07/16)
 */
public class NodesMessage implements Message {

    private Queue<Node> nodes;

    public NodesMessage() {
        nodes = new ConcurrentLinkedQueue<>();
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
    public void decode(final byte[] input) {
        final Decoder d = new Decoder(input);

        byte[] bytes = d.getByteArray();
        while (bytes != null && bytes.length > 0) {
            final Node n = new Node();
            n.decode(bytes);
            nodes.add(n);
            bytes = d.getByteArray();
        }
    }

    public Queue<Node> getNodes() {
        return nodes;
    }

    public void setNodes(final Queue<Node> nodes) {
        this.nodes = nodes;
    }
}
