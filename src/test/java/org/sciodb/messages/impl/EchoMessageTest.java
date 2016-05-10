package org.sciodb.messages.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

/**
 * @author jesus.navarrete  (30/04/16)
 */
public class EchoMessageTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void encode() throws Exception {
        final EchoMessage echo = new EchoMessage();

        final UUID uuid = UUID.randomUUID();

        echo.getHeader().setId(uuid.toString());
        echo.setMsg("Hello world!");

        echo.getHeader().setLength(1234);

        byte[] encode = echo.encode();

//        final EchoMessage echoDecode = new EchoMessage(encode);
    }

    @Test
    public void decode() throws Exception {

    }

}