#!/usr/bin/env bash

# how to use:
#
# > ./startup.sh 9090
#
# start up node with seeds:
#
# > ./startup.sh 9091 0.0.0.0:9090

# Create a list of folders for testing (logs, pids, data)
./create_test_env.sh

export JAVA_OPTS_SCIODB="-Xms128m -Xmx256m -XX:MaxMetaspaceSize=128m"

export CLASSPATH=".";
for i in ../lib/*.jar ; do
  CLASSPATH=$CLASSPATH:$i
done

CLASSPATH=$CLASSPATH:../target/classes/
seeds=$2
if [ -z "$seeds" ]
then
  echo "[WARNING] There is no seeds present"
  nohup java $JAVA_OPTS_SCIODB -cp $CLASSPATH org.sciodb.ScioDB -p $1 > logs/node_$1.log 2>&1 &
else
  nohup java $JAVA_OPTS_SCIODB -cp $CLASSPATH org.sciodb.ScioDB -p $1 -s $2 > logs/node_$1.log 2>&1 &
fi

pid=$!
echo $pid >> pids/node_$1.pid

echo "ScioDB is running with pid $pid"
