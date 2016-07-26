package org.sciodb.messages.impl;

import org.sciodb.messages.Decoder;
import org.sciodb.messages.Encoder;
import org.sciodb.messages.Message;

/**
 * @author jenaiz on 23/04/16.
 */
public class EchoMessage implements Message {

    private String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public byte[] encode() {
        final Encoder encoder = new Encoder();

        encoder.in(msg);

        return encoder.container();
    }

    @Override
    public void decode(byte[] input) {
        final Decoder d = new Decoder(input);

        msg = new String(d.getByteArray());
    }

}
