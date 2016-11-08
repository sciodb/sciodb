#!/usr/bin/env bash

export JAVA_OPTS_SCIODB="-Xms128m -Xmx256m -XX:MaxMetaspaceSize=128m"

function check_java {
    if type -p java; then
        echo found java executable in PATH
        _java=java
    elif [[ -n "$JAVA_HOME" ]] && [[ -x "$JAVA_HOME/bin/java" ]];  then
        echo found java executable in JAVA_HOME
        _java="$JAVA_HOME/bin/java"
    else
        echo "[ERROR] There is no Java installed in this machine, go to https://www.oracle.com/java and download JDK 1.8 or later"
    fi
}

function check_java_version {
    if [[ "$_java" ]]; then
        version=$("$_java" -version 2>&1 | awk -F '"' '/version/ {print $2}')
        echo version "$version"
        if [[ "$version" > "1.8" ]]; then
            echo version is more than 1.8
        else
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

java $JAVA_OPTS_SCIODB -cp $CLASSPATH org.sciodb.ScioDB "$@"

#nohup java $JAVA_OPTS_SCIODB -cp $CLASSPATH org.sciodb.ScioDB "$@" > logs/node.log 2>&1 &
#pid=$!

#echo "ScioDB is running with pid $pid"
