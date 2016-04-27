package org.sciodb.messages;

import java.util.UUID;

/**
 * @author jenaiz on 23/04/16.
 */
public class EchoMessage {

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
}
