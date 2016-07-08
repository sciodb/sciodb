package org.sciodb.messages.impl;

/**
 * @author jesus.navarrete  (27/06/16)
 */
public class GenericMessage {

    private Header header;

    private byte[] content;

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
}
