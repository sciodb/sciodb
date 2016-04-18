package org.sciodb.storages;

import java.util.List;
import org.sciodb.utils.StorageException;

/**
 * @author jenaiz on 03/04/16.
 */
public interface StorageEngine {

    void init() throws StorageException;

    void createDatabase(final String name) throws StorageException;

    void createCollection(final String name) throws StorageException;

    void persist(final byte[] key, final byte[] value);

    byte[] find(final byte[] key);

    void close();

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
