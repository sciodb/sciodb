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

This should start the ScioDB, and you will be able to add or remove nodes to the sciodb.
For the moment it is only for testing, the system is not functional.

If you want to see multiple nodes working, you can execute:

```
./startup.sh {PORT} {SEED}
```

For example:
```
./startup.sh 9090
```
will start a node without seeds. Or if you want to start nodes with a seed to connect:

```
./startup.sh 9091 0.0.0.0:9090
```

if you want to stop a node:
```
./stop.sh 9090
```
If you want to learn more about how to start the system, take a look to the [documentation](doc/Readme.md).

Enjoy it!

LICENSE
-------

See the file LICENSE.

Copyright (c) 2016-2020 Jesús Navarrete <jesus.navarrete@gmail.org>, released under the GPLv3 license