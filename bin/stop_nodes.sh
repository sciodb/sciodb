#!/usr/bin/env bash


# how to use:
#
# > ./stop.sh 20
#
# it is stopping 20 nodes, starting in port 9000

port=9000

for i in $(seq 0 $1) ; do
  ./stop.sh $port
  port=$((port+1))
done