package org.sciodb.storages;

import org.sciodb.utils.StorageException;

import java.util.List;

/**
 * @author jenaiz on 03/04/16.
 */
public interface StorageEngine {

    void init() throws StorageException;
    void close();


    void createDatabase(final String name) throws StorageException;
    void useDatabase(final String name) throws StorageException;
    // Database functions
    void createDatabase(final byte[] database);
    byte[] databaseInfo();


    void createCollection(final String name) throws StorageException;
    void createCollection(final byte[] collection);
    void dropCollection();


    void persist(final byte[] key, final byte[] value);
    byte[] find(final byte[] query);
    void delete(final byte[] query) throws StorageException;


//    void create();



    // data operations
    /*void store();
    void get();
    void update();
    void delete();*/
    // is there any sense for particular operations?


    List<byte[]> query(final byte[] query);
    List<byte[]> bulkOperation(final byte[] query);
    List<byte[]> getIndexes();

    void getStatistics() throws StorageException;

}
