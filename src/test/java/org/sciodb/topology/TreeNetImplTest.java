package org.sciodb.topology;

import org.junit.Before;
import org.junit.Test;
import org.sciodb.messages.impl.Node;
import sun.security.provider.MD5;

import java.security.MessageDigest;

import static org.junit.Assert.*;

/**
 * @author jenaiz on 02/10/16.
 */
public class TreeNetImplTest {

    Net tree;

    @Before
    public void setUp() throws Exception {
        tree = new TreeNetImpl();
    }

    @Test
    public void add() throws Exception {
        final Node node = new Node("localhost", 9090);
        tree.add(node);

        final Node node1 = new Node("localhost", 9091);
        tree.add(node1);

        final Node node2 = new Node("localhost", 9092);
        tree.add(node2);

    }

    @Test
    public void remove() throws Exception {

        final MessageDigest md5 = MessageDigest.getInstance("MD5");

        md5.update("localhost:9090".getBytes());
        byte[] r = md5.digest();

        System.out.println(new sun.misc.BASE64Encoder().encode(r));
        final MessageDigest md5a = MessageDigest.getInstance("MD5");

        md5a.update("localhost:9091".getBytes());
        byte[] ra = md5a.digest();

        System.out.println(new sun.misc.BASE64Encoder().encode(ra));
        final MessageDigest md5b = MessageDigest.getInstance("MD5");

        md5b.update("localhost:9092".getBytes());
        byte[] rb = md5b.digest();
        System.out.println(new sun.misc.BASE64Encoder().encode(rb));

        System.out.println((new sun.misc.BASE64Encoder().encode(r)).compareTo(new sun.misc.BASE64Encoder().encode(ra)));
        System.out.println((new sun.misc.BASE64Encoder().encode(ra)).compareTo(new sun.misc.BASE64Encoder().encode(r)));

        System.out.println((new sun.misc.BASE64Encoder().encode(r)).compareTo(new sun.misc.BASE64Encoder().encode(rb)));
        System.out.println((new sun.misc.BASE64Encoder().encode(rb)).compareTo(new sun.misc.BASE64Encoder().encode(r)));

        System.out.println((new sun.misc.BASE64Encoder().encode(ra)).compareTo(new sun.misc.BASE64Encoder().encode(rb)));
        System.out.println((new sun.misc.BASE64Encoder().encode(rb)).compareTo(new sun.misc.BASE64Encoder().encode(ra)));
    }

    @Test
    public void search() throws Exception {
        TreeNetImpl tree1 = new TreeNetImpl();

        final Node node1 = new Node("localhost", 9090);
        final Node node2 = new Node("192.137.168.1", 9091);
        final Node node3 = new Node("0.0.0.0", 9092);

        tree1.add(node1);
        tree1.add(node2);
        tree1.add(node3);

        final Node n1 = tree1.search(node1);
        final Node n2 = tree1.search(node2);
        final Node n3 = tree1.search(node3);

        assertEquals(node1.getPort(), n1.getPort());
        assertEquals(node1.getHost(), n1.getHost());
        assertEquals(node1.hash(),    n1.hash());

        assertEquals(node2.getPort(), n2.getPort());
        assertEquals(node2.getHost(), n2.getHost());
        assertEquals(node2.hash(),    n2.hash());

        assertEquals(node3.getPort(), n3.getPort());
        assertEquals(node3.getHost(), n3.getHost());
        assertEquals(node3.hash(),    n3.hash());

    }

    @Test
    public void getPosition() throws Exception {
        fail("Not implemented yet");
    }

    @Test
    public void getPeers() throws Exception {
        fail("Not implemented yet");
    }

    @Test
    public void snapshot() throws Exception {
        fail("Not implemented yet");
    }

}