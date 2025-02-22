ScioDB
=====


ScioDB is a NoSQL distributed database prototype, it is designed to scale horizontally to thousands of nodes.


What is ScioDB
---
ScioDB is an open-source distributed database designed to horizontally scale to thousands of nodes. It has a distributed
architecture using P2P.


Quickstart
---

At this moment there is no package you can download, if you want to play a bit with the current version you have to
compile by yourself using maven. It is pretty simple, you just need to clone or download a copy of the current repository
and execute:

```
mvn install
```

That will download all the needed libraries, and it will copy the jar files to the lib folder. After that you can start
Scio DB or multiple Scio DB nodes to play a bit with the system.

And then start a single node that will start up a single node from the bin folder:

```
./sciodb.sh
```
Read [our documentation](./doc/Readme.md) for more details, about how to start up multiple nodes.

Enjoy it!


LICENSE
-------

See the file LICENSE.

Copyright (c) 2016-2025 Jesús Navarrete <jesus.navarrete@gmail.com>, released under the GPLv3 license