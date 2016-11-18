package org.sciodb.topology;

import org.sciodb.messages.impl.Node;

import java.util.List;

/**
 * @author Jesús Navarrete (06/08/16)
 */
public interface Net extends Iterable<Node>{

    boolean isEmpty();
    int size();

    void add(final Node node);
    void remove(final Node node);
    boolean contains(final Node node);

    List<Node> getPeers(final Node node);
    List<Node> snapshot();

}
