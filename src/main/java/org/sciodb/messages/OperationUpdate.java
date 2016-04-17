package org.sciodb.messages;

/**
 * @author jesus.navarrete  (10/03/16)
 */
public class OperationUpdate {

    private MessageHeader header; // standard message header
    private long ZERO;               // 0 - reserved for future use
    private String fullCollectionName; // "dbname.collectionname"
    private long flags;              // bit vector. see below
//    private Document selector;           // the query to select the document
//    private Document update;             // specification of the update to perform

    public MessageHeader getHeader() {
        return header;
    }

    public void setHeader(MessageHeader header) {
        this.header = header;
    }

    public long getZERO() {
        return ZERO;
    }

    public void setZERO(long ZERO) {
        this.ZERO = ZERO;
    }

    public String getFullCollectionName() {
        return fullCollectionName;
    }

    public void setFullCollectionName(String fullCollectionName) {
        this.fullCollectionName = fullCollectionName;
    }

    public long getFlags() {
        return flags;
    }

    public void setFlags(long flags) {
        this.flags = flags;
    }

}
