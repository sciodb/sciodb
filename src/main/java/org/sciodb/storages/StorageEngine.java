package org.sciodb.storages;

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

}
