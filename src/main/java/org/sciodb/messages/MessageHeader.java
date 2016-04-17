package org.sciodb.messages;

/**
 * @author jesus.navarrete  (10/03/16)
 */
public class MessageHeader {

    private long messageLength; // total message size, including this
    private long requestID;     // identifier for this message
    private long responseTo;    // requestID from the original request
                                //   (used in responses from db)
    private long opCode;        // request type - see table below

    public long getMessageLength() {
        return messageLength;
    }

    public void setMessageLength(long messageLength) {
        this.messageLength = messageLength;
    }

    public long getRequestID() {
        return requestID;
    }

    public void setRequestID(long requestID) {
        this.requestID = requestID;
    }

    public long getResponseTo() {
        return responseTo;
    }

    public void setResponseTo(long responseTo) {
        this.responseTo = responseTo;
    }

    public long getOpCode() {
        return opCode;
    }

    public void setOpCode(long opCode) {
        this.opCode = opCode;
    }
}
