package org.sciodb.messages.impl;

import org.sciodb.messages.Decoder;
import org.sciodb.messages.Encoder;
import org.sciodb.messages.Message;
import org.sciodb.server.nessy.Roles;

/**
 * @author jesus.navarrete  (22/09/14)
 */
public class Node implements Message {

    private String role;
    private String host;
    private int port;

    public Node() {
    }

    public Node(final String host, final int port) {
        super();
        this.host = host;
        this.port = port;
        this.role = Roles.chunker.name();
    }

    public Node(final String host, final int port, final String role) {
        this(host, port);
        this.role = role;

    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String url() {
        final StringBuilder sb = new StringBuilder();

        if (!host.startsWith("http")) {
            sb.append("http://");
        }
        sb.append(host).append(":").append(port);

        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        if (port != node.port) return false;
        if (host != null ? !host.equals(node.host) : node.host != null) return false;
        if (role != null ? !role.equals(node.role) : node.role != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = role != null ? role.hashCode() : 0;
        result = 31 * result + (host != null ? host.hashCode() : 0);
        result = 31 * result + port;
        return result;
    }

    @Override
    public byte[] encode() {
        final Encoder encoder = new Encoder();

        encoder.in(role);
        encoder.in(host);
        encoder.in(port);

        return encoder.container();
    }

    @Override
    public void decode(byte[] input) {
//        final byte[] rest = ByteUtils.split(input, h.getLength(), input.length);
        final Decoder decoder = new Decoder(input);
        this.role = decoder.getString();
        this.host = decoder.getString();
        this.port = decoder.getInt();
    }

}

