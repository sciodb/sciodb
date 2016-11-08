package org.sciodb.messages;

/**
 * @author Jesús Navarrete (23/04/16)
 */
public interface Message {

    byte[] encode();

    void decode(byte[] input);

}
