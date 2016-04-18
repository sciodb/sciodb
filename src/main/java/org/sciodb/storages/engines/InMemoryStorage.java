package org.sciodb.storages.engines;

import org.sciodb.storages.StorageEngine;
import org.sciodb.utils.StorageException;

import java.util.List;

/**
 * @author jesus.navarrete  (24/03/16)
 */
public class InMemoryStorage implements StorageEngine {

    @Override
    public void init() throws StorageException {

    }

    @Override
    public void createDatabase(String name) throws StorageException {

    }

    @Override
    public void createCollection(String name) throws StorageException {

    }

    @Override
    public void persist(byte[] key, byte[] value) {

    }

    @Override
    public byte[] find(byte[] key) {
        return new byte[0];
    }

    @Override
    public void close() {

    }

    @Override
    public void create() {

    }

    @Override
    public void createDatabase(byte[] database) {

    }

    @Override
    public byte[] databaseInfo() {
        return new byte[0];
    }

    @Override
    public void createCollection(byte[] collection) {

    }

    @Override
    public void dropCollection() {

    }

    @Override
    public List<byte[]> query(byte[] query) {
        return null;
    }

    @Override
    public List<byte[]> bulkOperation(byte[] query) {
        return null;
    }

    @Override
    public List<byte[]> getIndexes() {
        return null;
    }

    @Override
    public void getStatistics() {

    }

}
