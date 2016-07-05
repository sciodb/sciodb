package org.sciodb.messages.impl;

import org.sciodb.messages.Decoder;
import org.sciodb.messages.Encoder;
import org.sciodb.messages.Message;

/**
 * @author jesus.navarrete  (09/06/16)
 */
public class NodeMessage implements Message {

    private Header header;

    private Node node;

    public NodeMessage() {
        header = new Header();
        node = new Node();
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
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

        encoder.in(header.encode());
        encoder.in(node.encode());

        return encoder.container();
    }

    @Override
    public void decode(byte[] input) {
        final Decoder d = new Decoder(input);

        header.decode(d.getByteArray());
        node.decode(d.getByteArray());
//        final byte[] rest = ByteUtils.split(input, h.getLength(), input.length);

//        final Node node = new Node();
//        node.decode(rest);
//        this.node = node;
    }
}
