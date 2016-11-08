package org.sciodb.storages.models;

/**
 * @author Jesús Navarrete (26/05/16)
 */
public class DatabaseInfo {

    private String name;
    private long spaceUsed;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSpaceUsed() {
        return spaceUsed;
    }

    public void setSpaceUsed(long spaceUsed) {
        this.spaceUsed = spaceUsed;
    }
}
