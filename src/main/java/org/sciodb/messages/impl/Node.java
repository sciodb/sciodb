package org.sciodb.messages.impl;

import org.apache.log4j.Logger;
import org.sciodb.messages.Decoder;
import org.sciodb.messages.Encoder;
import org.sciodb.messages.Message;

/**
 * @author Jesús Navarrete (22/09/14)
 */
public class Node implements Message {

    private String host;
    private int port;
    private int status;
    private String guid;

    private long lastCheck;

    public Node() {
        guid = "";
    }

    public Node(final String host, final int port) {
        super();
        this.host = host;
        this.port = port;
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

    public String url() {
        final StringBuilder sb = new StringBuilder();

        if (!host.startsWith("http")) {
            sb.append("http://");
        }
        sb.append(host).append(":").append(port);

        return sb.toString();
    }

    @Override
    public byte[] encode() {
        final Encoder encoder = new Encoder();

        encoder.in(host);
        encoder.in(port);
        encoder.in(status);
        encoder.in(guid);

        return encoder.container();
    }

    @Override
    public void decode(byte[] input) {
        final Decoder decoder = new Decoder(input);
        this.host = decoder.getString();
        this.port = decoder.getInt();
        this.status = decoder.getInt();
        this.guid = decoder.getString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        if (port != node.port) return false;
        return host.equals(node.host);

    }

    @Override
    public int hashCode() {
        int result = host.hashCode();
        result = 31 * result + port;
        return result;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getLastCheck() {
        return lastCheck;
    }

    public void setLastCheck(long lastCheck) {
        this.lastCheck = lastCheck;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }
}

