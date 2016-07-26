package org.sciodb.server;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * @author jesus.navarrete  (10/05/16)
 */
public class ServerSocketTest {

    @Before
    public void setUp() throws Exception {
        try {
            new Thread(new ServerSocket(null, 9090)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void run() throws Exception {

    }

}