package org.sciodb.storages;

import java.util.List;

/**
 * @author jesus.navarrete  (24/03/16)
 */
public interface StorageEngine {

    void create();

    // Database functions
    void createDatabase(final byte[] database);
    byte[] databaseInfo();

    void createCollection(final byte[] collection);
    void dropCollection();

    // data operations
    /*void store();
    void get();
    void update();
    void delete();*/
    // is there any sense for particular operations?


    List<byte[]> query(final byte[] query);
    List<byte[]> bulkOperation(final byte[] query);
    List<byte[]> getIndexes();

    void getStatistics();

}
