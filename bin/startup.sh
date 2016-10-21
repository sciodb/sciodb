#!/usr/bin/env bash

# how to use:
# ./startup.sh 9090

export JAVA_OPTS_SCIODB="-Xms128m -Xmx256m -XX:MaxMetaspaceSize=128m"

export CLASSPATH=".";
for i in ../lib/*.jar ; do
  CLASSPATH=$CLASSPATH:$i
done

CLASSPATH=$CLASSPATH:../target/classes/

#java $JAVA_OPTS_SCIODB -cp $CLASSPATH org.sciodb.ScioDB "$@" -p $1
nohup java $JAVA_OPTS_SCIODB -cp $CLASSPATH org.sciodb.ScioDB -p $1 > logs/node_$1.log 2>&1 &
pid=$!
echo $pid >> pids/node_$1.pid

#echo "ScioDB is running with pid $pid"
