ScioDB Documentation (draft)
=====

At this moment there is no package that you can download, if you want to play a bit with the current version you have to
compile by yourself using maven. It is pretty simple, you just need to clone or download a copy of the current repository
and execute:
```
mvn install
```
That will download all the needed libraries and it will copy the jar files to the lib folder. After that you can start
Scio DB or multiple Scio DB nodes to play a bit with the system.

How to start a single node
---

The normal way to start Scio DB is using the principal script:

```
./sciodb.sh
```

For testing purposive, if you want to start up multiples nodes, you can use the *startup.sh* script. For example:
```
./startup.sh 9090
```
Or if you want to start a node and connect it with the other:
```
./startup.sh 9091 0.0.0.0:9090
```
The format is:
```
./startup.sh {PORT} {SEED}
```
to stop the nodes, you just need to use the port number:
```
./stop.sh 9090
```

Copyright (c) 2016 Jesús Navarrete <jesus.navarrete@gmail.org>, released under the GPLv2 license