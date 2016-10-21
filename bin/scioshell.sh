#!/usr/bin/env bash

export JAVA_OPTS_SCIODB="-Xms128m -Xmx256m -XX:MaxMetaspaceSize=128m"

export CLASSPATH=".";
for i in ../lib/*.jar ; do
  CLASSPATH=$CLASSPATH:$i
done

CLASSPATH=$CLASSPATH:../target/classes/

java $JAVA_OPTS_SCIODB -cp $CLASSPATH org.sciodb.shell.ScioSchell "$@"
#java $JAVA_OPTS_SCIODB -cp $CLASSPATH Console "$@"