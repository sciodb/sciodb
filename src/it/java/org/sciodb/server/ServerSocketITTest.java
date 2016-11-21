package org.sciodb.server;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sciodb.messages.Operations;
import org.sciodb.messages.impl.ContainerMessage;
import org.sciodb.messages.impl.EchoMessage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.UUID;

/**
 * @author Jesús Navarrete (10/05/16)
 */
public class ServerSocketITTest {

    final static private Logger logger = Logger.getLogger(ServerSocketITTest.class);

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test_echoMessages_10() throws Exception {
        threads(10);
    }

    @Test
    public void test_echoMessages_100() throws Exception {
        threads(100);
    }

    @Test
    public void test_echoMessages_1000() throws Exception {
        threads(1000);
    }

    @Test
    public void test_echoMessages_200000() throws Exception {
        threads(20000);
    }

//    @Test
    public void test_echoMessages_1M() throws Exception {
        threads(1000000);
    }

    private void threads(int limit) throws IOException {
        final SocketChannel client;

        final InetSocketAddress hostAddress = new InetSocketAddress("localhost", 9090);
        client = SocketChannel.open(hostAddress);

        long init = System.currentTimeMillis();

        for (int i = 0; i < limit; i++) {
            sendMsg(client, "thread-" + i);
        }

        long end = System.currentTimeMillis() - init;
        logger.info(limit + " message in " + end + "ms");

        client.close();
    }

    public void sendMsg(final SocketChannel client, final String input) throws IOException {

        new Thread(input){
            @Override
            public synchronized void start() {
                super.start();
                try {

                    final byte [] message = createMessage(this.getName());

                    final ByteBuffer buffer = ByteBuffer.wrap(message);
                    final String headerSize = String.format("%04d", message.length);
                    final ByteBuffer header = ByteBuffer.wrap(headerSize.getBytes());
                    client.write(header);
                    client.write(buffer);
                    buffer.clear();

                    final ByteBuffer response = ByteBuffer.allocate(1024);

                    int currentSize = client.read(response);
                    byte [] data = new byte[currentSize];
                    System.arraycopy(response.array(), 0, data, 0, currentSize);

                    final String str = new String(data);
                    logger.debug("Got: " + str);
                } catch (IOException e) {
                    logger.error("Error in the thread", e);
                }
            }
        }.start();

    }

    private static byte[] createMessage(final String name) {
        final EchoMessage echo = new EchoMessage();
        echo.setMsg("test message - " + name);

        final ContainerMessage container = new ContainerMessage();
        container.getHeader().setId(UUID.randomUUID().toString());
        container.getHeader().setOperationId(Operations.OP_ECHO.getValue());

        container.setContent(echo.encode());

        return container.encode();
    }
}