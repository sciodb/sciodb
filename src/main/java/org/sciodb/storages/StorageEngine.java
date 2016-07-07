package org.sciodb.storages;

import org.sciodb.exceptions.StorageException;
import org.sciodb.storages.models.CollectionInfo;
import org.sciodb.storages.models.DatabaseInfo;

import java.util.List;

/**
 * @author jenaiz on 03/04/16.
 */
public interface StorageEngine {

    // Storage methods
    void init() throws StorageException;

    void close();

    // Database functions
//     void createDatabase(final String name) throws StorageException;

    DatabaseInfo getDatabaseStats(final String name) throws StorageException;


    // Collection functions
    void createCollection(final String databaseName, final String collectionName) throws StorageException;

    CollectionInfo getCollectionInfo(final String name);

    List<byte[]> getIndexes(final String collectionName);

    void dropCollection(final String databaseName, final String name) throws StorageException;


    // data operations
    void store(final byte[] key, final byte[] value);

    byte[] find(final byte[] query);

    List<byte[]> query(final byte[] query);

    void update(final byte[] key, final byte[] value);

    void update(final byte[] query);

    List<byte[]> bulkOperation(final byte[] query);

    void delete(final byte[] query) throws StorageException;

}
