package org.sciodb.messages.impl;

import org.sciodb.messages.Decoder;
import org.sciodb.messages.Encoder;
import org.sciodb.messages.Message;

/**
 * @author Jesús Navarrete (09/06/16)
 */
// TODO extends from node
public class NodeMessage implements Message {

    private Node node;

    public NodeMessage() {
        node = new Node();
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    @Override
    public byte[] encode() {
        final Encoder encoder = new Encoder();

        encoder.in(node.encode());

        return encoder.container();
    }

    @Override
    public void decode(byte[] input) {
        final Decoder d = new Decoder(input);

        node.decode(d.getByteArray());
    }

}
