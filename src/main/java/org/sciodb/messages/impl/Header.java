package org.sciodb.messages.impl;

import org.sciodb.messages.Decoder;
import org.sciodb.messages.Encoder;
import org.sciodb.messages.Message;

/**
 * @author Jesús Navarrete (23/04/16)
 */
public class Header implements Message {

    private String id;
    private int length;

    private int operationId;

    public Header() {
    }

    public Header(String id, int length, int operationId) {
        this.id = id;
        this.length = length;
        this.operationId = operationId;
    }

    @Override
    public byte[] encode() {
        final Encoder encoder = new Encoder();

        // Order it's really important
        encoder.in(id);
        encoder.in(length);
        encoder.in(operationId);

        return encoder.container();
    }

    @Override
    public void decode(byte[] input) {
        final Decoder decoder = new Decoder(input);
        // Order it's really important
        this.id = decoder.getString();
        this.length = decoder.getInt();
        this.operationId = decoder.getInt();
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getOperationId() {
        return operationId;
    }

    public void setOperationId(int operationId) {
        this.operationId = operationId;
    }
    
}
