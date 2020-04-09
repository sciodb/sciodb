package org.sciodb.server;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * @author Jesús Navarrete (10/05/16)
 */
public class ServerSocketTest {

    @Before
    public void setUp() {
        try {
            new Thread(new ServerSocket(null, 9090)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}