package org.sciodb.utils.models;

/**
 * @author jesus.navarrete  (06/03/16)
 */
public class Command {

    private String messageID;

    private String operationID;

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
}
