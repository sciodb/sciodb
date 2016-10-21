[X] - Connection Manager
[X] - Request Dispatcher


- Storage engines:
    * in-memory (for testing)
    * wiredtiger?
    (in progress) * RocksDB storage engine (in progress)


- distributed system manager:
    * different datacenters with asynchronous replication
    * high availability
    * more machines = more power/performance
    * master/slave or no roles like in cassandra: spanner?!
    * replication fault-tolerance and sharding (controlled for the system)


- high performance
    * more memory = more performance
    * more processors = more operations/second
    * more machines = more scalability


- background jobs (expensive jobs):
    * not realtime calculations
    * map reduce jobs or similar technology
    * posibility to use mapreduce
    * machine learning libraries
    * solr queries ...


- query language
    * sql-based or javascript/json query language
    * script language


- build output
    * darwin, linux: build/darwin and build/linux
    * make deps: can be a task to update dependencies: java 8 or bash or ...

RocksDB:
- the path of the database comes from a property in the start up
- the name of the database/schemas comes from the user
- the name of the collection comes from the user

- the database is closed only one time (close() method)
- backup using BackupEngine... research
- bulk operation

...?

- support for big amounts of data, terabytes
- isolation, atomicity...?