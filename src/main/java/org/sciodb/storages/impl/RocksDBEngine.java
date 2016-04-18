package org.sciodb.storages.impl;

import org.apache.log4j.Logger;
import org.rocksdb.DBOptions;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.sciodb.storages.StorageEngine;
import org.sciodb.utils.StorageException;

import java.util.List;

/**
 * Implementation of RocksDB storage engine
 *
 * @author jenaiz on 03/04/16.
 */
public class RocksDBEngine implements StorageEngine {

    private static final Logger logger = Logger.getLogger(RocksDBEngine.class);

    private Options options;

    private final static String DATABASE_PATH = "/Users/jenaiz/sciodb/sciodb/data/rocksdb";
    private RocksDB db;

    public RocksDBEngine() {
        // loads the RocksDB C++ library
        RocksDB.loadLibrary();
        options = new Options()
                .setCreateIfMissing(true);
    }


    public static void main(String[] args) {

        final StorageEngine se = new RocksDBEngine();

        String key1 = "j";
        String value1 = "The rocksdb library provides a persistent key value store. Keys and values are arbitrary " +
                "byte arrays. The keys are ordered within the key value store according to a user-specified comparator " +
                "function.";

        String key2 = "k";
        String value2 = "The library is maintained by the Facebook Database Engineering Team, and is based on leveldb, " +
                "by Sanjay Ghemawat and Jeff Dean at Google.\n";


//        se.persist(key1, value1);
//        se.persist(key2, value2);

        if (value1.equals(se.find(key1.getBytes()))) {
            System.out.println("Read value 1 correctly");
        }
        if (value2.equals(se.find(key2.getBytes()))) {
            System.out.println("Read value 2 correctly");
        }

        se.close();

        // the Options class contains a set of configurable DB options
        // that determines the behavior of a database.
    }

    @Override
    public void init() throws StorageException {
        try {
            db = RocksDB.open(options, DATABASE_PATH);
            DBOptions dbOptions = new DBOptions();
        } catch (RocksDBException e) {
            throw new StorageException("Error initialising Storage", e);
        }
    }

    @Override
    public void createDatabase(String name) throws StorageException {
        throw new StorageException("METHOD NOT IMPLEMENTED");
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

    public void stats() throws StorageException {
        try {
            final String str = db.getProperty("rocksdb.stats");
            logger.debug(str);
        } catch (RocksDBException ex) {
            throw new StorageException("Error while trying to print RocksDB statistics");
        }
    }

    @Override
    public void close() {
        if (db != null) db.close();
        // TODO look for the right method to kill the C++ stuff
        options.dispose();
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
