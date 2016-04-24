package org.sciodb.messages;

/**
 * @author jenaiz on 23/04/16.
 */
public class EchoMessage {

    private Header header;

    private String msg;

    public static void main(String[] args) {
        final EchoMessage echo = new EchoMessage();

        echo.getHeader().setId(123);
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
