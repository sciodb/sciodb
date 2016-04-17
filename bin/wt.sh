#!/usr/bin/env bash

export JAVA_OPTS_SCIODB="-Xms128m -Xmx256m -XX:PermSize=64m -XX:MaxPermSize=128m"

export CLASSPATH=".";
for i in ../lib/*.jar ; do
  CLASSPATH=$CLASSPATH:$i
done

CLASSPATH=$CLASSPATH:../target/classes/

export JAVA_LIBRARY_PATH=".";
for i in ../wt-libs/* ; do
  JAVA_LIBRARY_PATH=$JAVA_LIBRARY_PATH:$i
done

echo "$JAVA_LIBRARY_PATH"


#java $JAVA_OPTS_SCIODB -Djava.library.path=$JAVA_LIBRARY_PATH -cp $CLASSPATH org.sciodb.storages.engines.WiredTiger
java $JAVA_OPTS_SCIODB -Djava.library.path=../wt-libs/libwiredtiger_java.la -cp $CLASSPATH org.sciodb.storages.engines.WiredTiger
