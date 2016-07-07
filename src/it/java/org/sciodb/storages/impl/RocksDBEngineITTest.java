package org.sciodb.storages.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rocksdb.RocksDB;
import org.sciodb.storages.StorageEngine;
import org.sciodb.storages.models.DatabaseInfo;
import org.sciodb.utils.StorageException;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * @author jenaiz on 26/05/16.
 */
public class RocksDBEngineITTest {

    private StorageEngine rocksDB;
    final String dataFolder = "/tmp/data";
    final String database = "test_database";

    @Before
    public void setUp() throws Exception {
        rocksDB = new RocksDBEngine(dataFolder + File.separator + database);
    }

    @After
    public void tearDown() throws Exception {
        rocksDB.close();
        new File(dataFolder).delete();
    }

    @Test
    public void testInit() throws Exception {
        final String databaseName = dataFolder + "/test_1";
        StorageEngine rocksDB = new RocksDBEngine(databaseName);
        rocksDB.init();
        assert (true);
        final File result = new File(databaseName);
        assertTrue(result.exists());
        assertTrue(result.isDirectory());
        assertTrue(result.list().length > 0);
    }

    @Test
    public void testCreateCollection() throws Exception {
        final String databaseName = dataFolder + "/db_1";
        rocksDB = new RocksDBEngine(databaseName);
        rocksDB.init();
        rocksDB.createCollection(database, "collection_" + System.currentTimeMillis());
        assert (true);

        rocksDB.close();
        rocksDB.init();
        assert (true);
    }

    @Test(expected = StorageException.class)
    public void testCreateCollection_alreadyExist() throws Exception {
        rocksDB.init();
        rocksDB.createCollection(database, "collection_2");
        rocksDB.createCollection(database, "collection_2");
    }

    @Test
    public void testDropCollection() throws Exception {
        rocksDB.init();
        rocksDB.dropCollection(null, "collection_1");
        assert (true);
    }

}