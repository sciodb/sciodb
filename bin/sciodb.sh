#!/usr/bin/env bash

export JAVA_OPTS_SCIODB="-Xms128m -Xmx256m -XX:MaxMetaspaceSize=128m"

function check_java {
    JAVA=$(type -p java)
    if [ -z "$JAVA" ]; then
        echo "[ERROR] There is no Java installed in this machine, go to https://www.oracle.com/java and download JDK 1.8 or later"
        exit
    fi
}

function check_java_version {
    if [[ "$_java" ]]; then
        version=$("$_java" -version 2>&1 | awk -F '"' '/version/ {print $2}')
        echo "Current Java version $version"
        if [[ "$version" < "1.8" ]]; then
            echo "[ERROR] Scio DB requires JDK version 1.8 or later, the current version is $version "
            exit 1
        fi
    fi
}

check_java
check_java_version

export CLASSPATH=".";
for i in ../lib/*.jar ; do
  CLASSPATH=$CLASSPATH:$i
done

CLASSPATH=$CLASSPATH:../target/classes/

nohup java $JAVA_OPTS_SCIODB -cp $CLASSPATH org.sciodb.ScioDB "$@" > logs/node.log 2>&1 &
pid=$!

echo "ScioDB is running with pid $pid"
