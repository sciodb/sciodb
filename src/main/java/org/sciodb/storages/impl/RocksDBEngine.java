package org.sciodb.storages.impl;

import org.apache.log4j.Logger;
import org.rocksdb.*;
import org.sciodb.storages.StorageEngine;
import org.sciodb.exceptions.StorageException;
import org.sciodb.storages.models.CollectionInfo;
import org.sciodb.storages.models.DatabaseInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of RocksDB storage engine
 *
 * @author Jesús Navarrete (03/04/16)
 */
public class RocksDBEngine implements StorageEngine {

    private static final Logger logger = Logger.getLogger(RocksDBEngine.class);

    private Options options;
    private DBOptions dbOptions;

    private static String dataPath;

    private RocksDB db;
    private List<ColumnFamilyHandle> collections;
    private List<ColumnFamilyDescriptor> descriptors;

    public RocksDBEngine(final String dataFolder) {
        dataPath = dataFolder;

        // loads the RocksDB C++ library
        RocksDB.loadLibrary();
        options = new Options()
                .setCreateIfMissing(true);
    }

    @Override
    public void init() throws StorageException {
        try {
            collections = new ArrayList<>();
            descriptors = new ArrayList<>();

            final File f = new File(dataPath);
            boolean newDB = !f.exists();
            if (newDB) {
                f.mkdirs();
                logger.info("Created database folder : " + dataPath);
                descriptors.add(new ColumnFamilyDescriptor(
                        RocksDB.DEFAULT_COLUMN_FAMILY, new ColumnFamilyOptions()));

            } else {
                final List<byte[]> columns = RocksDB.listColumnFamilies(options, dataPath);
                if (columns != null) {
                    for(byte[] column: columns) {
                        descriptors.add(new ColumnFamilyDescriptor(
                                        column, new ColumnFamilyOptions()));
                    }
                }

            }

            dbOptions = new DBOptions()
                            .setCreateIfMissing(true);

            db = RocksDB.open(dbOptions, dataPath, descriptors, collections);

        } catch (RocksDBException e) {
            throw new StorageException("Error initialising Storage", e);
        }
    }

//    @Override
//    public void createDatabase(final String name) throws StorageException {
//        try {
//            long init = System.currentTimeMillis();
//            collections.clear();
//            // TODO check how much time take to open this takes between 1-5 ms to read or create it
//            db = RocksDB.open(dbOptions, dataPath + File.separator + name, descriptors, collections);
//            long finished = System.currentTimeMillis() - init;
//            logger.info(" creation time of the database :: " + finished);
//        } catch (RocksDBException e) {
//            throw new StorageException("Error initialising Storage", e);
//        }
//    }

    @Override
    public void createCollection(final String databaseName, final String collectionName) throws StorageException {
        try {
            final ColumnFamilyHandle column = db.createColumnFamily(new ColumnFamilyDescriptor(collectionName
                    .getBytes()));

            collections.add(column);

        } catch (RocksDBException e) {
            throw new StorageException("Collection not created", e);
        }
    }

    @Override
    public CollectionInfo getCollectionInfo(final String name) {
        return null;
    }

    @Override
    public List<byte[]> getIndexes(final String collectionName) {
        return null;
    }

    @Override
    public void dropCollection(final String databaseName, final String collectionName) throws StorageException {
        try {
            final List<byte[]> l = RocksDB.listColumnFamilies(options, dataPath);
            for (byte[] b : l) {
                if (collectionName.getBytes() == b) {
                    db.dropColumnFamily(null);
                }
            }
//            final ColumnFamilyHandle column = new ColumnFamilyHandle(db, 0L);
//            db.dropColumnFamily();
        } catch (RocksDBException e) {
            throw new StorageException("Not possible to delete collection", e);
        }
    }

    @Override
    public void store(byte[] key, byte[] value) {
        try {
            db.put(key, value);
        } catch (RocksDBException e) {
            logger.error("It is not possible to store the data", e);
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

    @Override
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
    public DatabaseInfo getDatabaseStats(final String name) throws StorageException {
        try {
            final String str = db.getProperty("rocksdb.stats");
            logger.debug(str);

            return new DatabaseInfo();
        } catch (RocksDBException ex) {
            throw new StorageException("Error while trying to print RocksDB statistics");
        }
    }
}
