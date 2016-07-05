package org.sciodb.storages.impl;

import org.apache.log4j.Logger;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.sciodb.storages.StorageEngine;
import org.sciodb.exceptions.StorageException;

import java.io.File;
import java.util.List;

/**
 * Implementation of RocksDB storage engine
 *
 * @author jenaiz on 03/04/16.
 */
public class RocksDBEngine implements StorageEngine {

    private static final Logger logger = Logger.getLogger(RocksDBEngine.class);

    private Options options;

//    private final static String DATABASE_PATH = "/Users/jesus.navarrete/projects/database/sciodb/data/rocksdb";
    private final static String DATABASE_PATH = "/Users/jesus.navarrete/projects/database/sciodb/data";
    private RocksDB db;

    public RocksDBEngine() {
        // loads the RocksDB C++ library
        RocksDB.loadLibrary();
        options = new Options()
                .setCreateIfMissing(true);
    }

    public static void main(String[] args) {
        RocksDBEngine engine = new RocksDBEngine();
        for (int i = 0; i < 100; i++) {
            try {
                engine.createDatabase("x-" + i);
            } catch (StorageException e) {
                System.out.println("error - i : " + i);
                e.printStackTrace();
            }
        }
    }

    @Override
    public void init() throws StorageException {
        try {
            db = RocksDB.open(options, DATABASE_PATH); // takes between 1-5 ms to read or create it
//            final DBOptions dbOptions = new DBOptions();
        } catch (RocksDBException e) {
            throw new StorageException("Error initialising Storage", e);
        }
    }

    @Override
    public void createDatabase(final String name) throws StorageException {
        try {
            long init = System.currentTimeMillis();
            db = RocksDB.open(options, DATABASE_PATH + File.separator + name); // takes between 1-5 ms to read or create it
            long finished = System.currentTimeMillis() - init;
            logger.info(" creation time of the database :: " + finished);
        } catch (RocksDBException e) {
            throw new StorageException("Error initialising Storage", e);
        }
    }

    @Override
    public void useDatabase(String name) throws StorageException {

    }

    @Override
    public void createDatabase(byte[] database) {

    }

    @Override
    public void createCollection(String name) throws StorageException {
        throw new StorageException("METHOD NOT IMPLEMENTED");
    }

    @Override
    public void persist(byte[] key, byte[] value) {
        try {
            db.put(key, value);
        } catch (RocksDBException e) {
            logger.error("It is not possible to persist the data", e);
        }
    }

    @Override
    public byte[] find(byte[] key) {
        byte[] result = null;
        try {
            result = db.get(key);

        } catch (RocksDBException e) {
            logger.error("It is not possible to find by key", e);
        }
        return result;
    }

    public void delete(byte[] key) throws StorageException {
        try {
            db.remove(key);
        } catch (RocksDBException e) {
            throw new StorageException("Not possible to remove key", e);
        }
    }

    @Override
    public void close() {
        if (db != null) db.close();
        // TODO look for the right method to kill the C++ stuff
        options.dispose();
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
    public void getStatistics() throws StorageException {
        try {
            final String str = db.getProperty("rocksdb.stats");
            logger.debug(str);
        } catch (RocksDBException ex) {
            throw new StorageException("Error while trying to print RocksDB statistics");
        }
    }
}
