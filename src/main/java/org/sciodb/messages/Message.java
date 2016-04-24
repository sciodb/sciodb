package org.sciodb.messages;

/**
 * @author jenaiz on 23/04/16.
 */
public interface Message {

    byte[] encode();

    void decode(byte[] input);

}
