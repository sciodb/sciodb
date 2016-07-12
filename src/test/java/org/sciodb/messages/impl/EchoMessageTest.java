package org.sciodb.messages.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sciodb.messages.Operations;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

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

        echo.getHeader().setId(UUID.randomUUID().toString());
        echo.getHeader().setOperationId(Operations.CHECK_NODE_STATUS.getValue());
        echo.getHeader().setLength(1234);

        echo.setMsg("Hello world!");

        byte[] encode = echo.encode();

        final EchoMessage result = new EchoMessage();
        result.decode(encode);

        assertEquals(echo.getMsg(), result.getMsg());
        assertEquals(echo.getHeader().getId(), result.getHeader().getId());
        assertEquals(echo.getHeader().getOperationId(), result.getHeader().getOperationId());
        assertEquals(echo.getHeader().getLength(), result.getHeader().getLength());
    }

    @Test
    public void decode() throws Exception {

    }

}