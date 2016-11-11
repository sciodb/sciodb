package org.sciodb.topology;

import org.sciodb.messages.impl.Node;

import java.util.List;

/**
 * @author Jesús Navarrete (06/08/16)
 */
public interface Net {

    void add(final Node node);

    void remove(final Node node);

    List<Node> getPeers(final Node node);
    List<Node> snapshot();

}
