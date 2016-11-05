ScioDB
=====

[![Build Status](https://travis-ci.org/sciodb/sciodb.svg?branch=master)](https://travis-ci.org/sciodb/sciodb)

How to compile:

```
mvn clean package
```

it will compile, package and copy all the libraries in the *lib* folder.

How to run the system:
```
./bin/sciodb.sh
```

For the moment it is only for testing, the system is not functional.

If you want to see multiple nodes working, you can execute:

```
./start.sh {PORT} {SEED}
```

for example:
```
./start.sh 9090 0.0.0.0:9090
./start.sh 9091 0.0.0.0:9090
./start.sh 9092 0.0.0.0:9090
```

and if you want to stop a node:
```
./stop.sh 9090
```

Enjoy it!

COPYING
-------

See the file COPYING.

Copyright (c) 2016 Jesús Navarrete <jesus.navarrete@gmail.org>, released under the GPLv2 license