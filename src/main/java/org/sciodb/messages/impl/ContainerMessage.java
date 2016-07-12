package org.sciodb.messages.impl;

import org.sciodb.messages.Decoder;
import org.sciodb.messages.Encoder;
import org.sciodb.messages.Message;

/**
 * @author jesus.navarrete  (27/06/16)
 */
public class ContainerMessage implements Message {

    private Header header;

    private byte[] content;

    public ContainerMessage() {
        header = new Header();
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    @Override
    public byte[] encode() {
        final Encoder encoder = new Encoder();
        encoder.in(header.encode());
        encoder.in(content);

        return encoder.container();
    }

    @Override
    public void decode(byte[] input) {
        final Decoder d = new Decoder(input);

        header.decode(d.getByteArray());
        content = d.getByteArray();
    }
}
