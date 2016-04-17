package org.sciodb.utils.models;

/**
 * @author jesus.navarrete  (06/03/16)
 */
public class Command {

    private String messageID;

    private String operationID;

    private byte[] message;

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getOperationID() {
        return operationID;
    }

    public void setOperationID(String operationID) {
        this.operationID = operationID;
    }

    public byte[] getMessage() {
        return message;
    }

    public void setMessage(byte[] message) {
        this.message = message;
    }
}
