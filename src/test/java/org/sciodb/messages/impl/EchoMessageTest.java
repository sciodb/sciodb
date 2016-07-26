package org.sciodb.messages.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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

        echo.setMsg("Hello world!");

        byte[] encode = echo.encode();

        final EchoMessage result = new EchoMessage();
        result.decode(encode);

        assertEquals(echo.getMsg(), result.getMsg());
    }

    @Test
    public void decode() throws Exception {

    }

}