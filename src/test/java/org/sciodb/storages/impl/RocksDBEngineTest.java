package org.sciodb.storages.impl;

import org.junit.Before;
import org.junit.Test;
import org.sciodb.storages.impl.RocksDBEngine;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * @author jenaiz on 03/04/16.
 */
public class RocksDBEngineTest {

    private RocksDBEngine storage;

    @Before
    public void setUp() throws Exception {
        storage = new RocksDBEngine();
    }

    @Test
    public void createDatabase() throws Exception {
        storage.createDatabase("/tmp/rocksdb/test");

        assert (true);
    }

    @Test
    public void createTable() throws Exception {
//        storage.createDatabase();
    }

    @Test
    public void persist() throws Exception {
        String key1 = "j";
        String value1 = "The rocksdb library provides a persistent key value store. Keys and values are arbitrary " +
                "byte arrays. The keys are ordered within the key value store according to a user-specified comparator " +
                "function.";

        String key2 = "k";
        String value2 = "The library is maintained by the Facebook Database Engineering Team, and is based on leveldb, " +
                "by Sanjay Ghemawat and Jeff Dean at Google.\n";

        storage.persist(key1.getBytes(), value1.getBytes());
        storage.persist(key2.getBytes(), value2.getBytes());

        assert(true);
    }

    @Test
    public void find() throws Exception {
        String key1 = "j";
        String value1 = "The rocksdb library provides a persistent key value store. Keys and values are arbitrary " +
                "byte arrays. The keys are ordered within the key value store according to a user-specified comparator " +
                "function.";

        String key2 = "k";
        String value2 = "The library is maintained by the Facebook Database Engineering Team, and is based on leveldb, " +
                "by Sanjay Ghemawat and Jeff Dean at Google.\n";

        byte[] v1 = storage.find(key1.getBytes());
        byte[] v2 = storage.find(key2.getBytes());

        assertTrue(Arrays.equals(v1, value1.getBytes()));
        assertTrue(Arrays.equals(v2, value2.getBytes()));

    }

    @Test
    public void close() throws Exception {

    }

}