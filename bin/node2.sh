#!/usr/bin/env bash

export JAVA_OPTS_SCIODB="-Xms128m -Xmx256m -XX:MaxMetaspaceSize=128m"

export CLASSPATH=".";
for i in ../lib/*.jar ; do
  CLASSPATH=$CLASSPATH:$i
done

CLASSPATH=$CLASSPATH:../target/classes/

java $JAVA_OPTS_SCIODB -cp $CLASSPATH org.sciodb.ScioDB -c node2.json
#nohup java $JAVA_OPTS_SCIODB -cp $CLASSPATH org.sciodb.Kernel node.json > logs/node.log 2>&1 &
#pid=$!
#echo $pid >> ../pids/node.pid

#echo "ScioDB is running with pid $pid"