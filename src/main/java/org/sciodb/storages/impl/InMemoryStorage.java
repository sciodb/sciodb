package org.sciodb.storages.impl;

import org.sciodb.exceptions.StorageException;
import org.sciodb.storages.StorageEngine;
import org.sciodb.storages.models.CollectionInfo;
import org.sciodb.storages.models.DatabaseInfo;

import java.util.Hashtable;
import java.util.List;

/**
 * @author Jesús Navarrete (24/03/16)
 */
public class InMemoryStorage implements StorageEngine {


    public static void main(String[] args) {
        final int size = 10000000;
        final String doc = createDoc(size);

        final Hashtable<Integer, String> memory = new Hashtable<>();

        for (int i = 0; i < size * 10; i++) {
            memory.put(i, doc);
        }
    }

    private static String createDoc(int size) {
        return "x".repeat(Math.max(0, size));
    }

    @Override
    public void init() throws StorageException {

    }

    @Override
    public void close() {

    }

    @Override
    public DatabaseInfo getDatabaseStats(String name) throws StorageException {
        return null;
    }

    @Override
    public void createCollection(String databaseName, String collectionName) throws StorageException {

    }

    @Override
    public CollectionInfo getCollectionInfo(String name) {
        return null;
    }

    @Override
    public List<byte[]> getIndexes(String collectionName) {
        return null;
    }

    @Override
    public void dropCollection(String databaseName, String name) throws StorageException {

    }

    @Override
    public void store(byte[] key, byte[] value) {

    }

    @Override
    public byte[] find(byte[] query) {
        return new byte[0];
    }

    @Override
    public List<byte[]> query(byte[] query) {
        return null;
    }

    @Override
    public void update(byte[] key, byte[] value) {

    }

    @Override
    public void update(byte[] query) {

    }

    @Override
    public List<byte[]> bulkOperation(byte[] query) {
        return null;
    }

    @Override
    public void delete(byte[] query) throws StorageException {

    }
}
