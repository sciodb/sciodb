package org.sciodb.messages.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Jesús Navarrete (30/04/16)
 */
public class EchoMessageTest {

    @Test
    public void encode() {
        final EchoMessage echo = new EchoMessage();

        echo.setMsg("Hello world!");

        byte[] encode = echo.encode();

        final EchoMessage result = new EchoMessage();
        result.decode(encode);

        assertEquals(echo.getMsg(), result.getMsg());
    }

}