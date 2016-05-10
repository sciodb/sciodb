package org.sciodb.messages.impl;

import org.sciodb.messages.Decoder;
import org.sciodb.messages.Encoder;
import org.sciodb.messages.Message;
import org.sciodb.messages.Operations;

import java.util.UUID;

/**
 * @author jenaiz on 23/04/16.
 */
public class EchoMessage implements Message {

    private Header header;

    private String msg;

    public static void main(String[] args) {
        final EchoMessage echo = new EchoMessage();

        final UUID uuid = UUID.randomUUID();

        echo.getHeader().setId(uuid.toString());
        echo.setMsg("Hello world!");

        echo.getHeader().setLength(1234);
    }

    public EchoMessage() {
        header = new Header();
        header.setOperationId(Operations.ECHO.getValue());
    }

    public Header getHeader() {
        return header;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public byte[] encode() {
        final Encoder encoder = new Encoder();

        header.encode();
        encoder.in(msg);

        return encoder.container();
    }

    @Override
    public void decode(byte[] input) {
        final Header h = new Header();
        h.decode(input);

        // TODO split the input and send the interesting part!!!!
        final Decoder decoder = new Decoder(input);


    }
}
