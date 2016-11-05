ScioDB
=====

[![Build Status](https://travis-ci.org/sciodb/sciodb.svg?branch=master)](https://travis-ci.org/sciodb/sciodb)

How to compile:

```
mvn clean package
```

it will compile, package and copy all the libraries in the *lib* folder.

How to run the system, you should go to the *bin* folder and execute
```
./sciodb.sh
```

For the moment it is only for testing, the system is not functional.

If you want to see multiple nodes working, you can execute:

```
./startup.sh {PORT} {SEED}
```

For example:
```
./start.sh 9090
```
will start a node without seeds. Or if you want to start nodes with a seed to connect:

```
./start.sh 9091 0.0.0.0:9090
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