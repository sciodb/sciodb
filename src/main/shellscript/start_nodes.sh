#!/usr/bin/env bash


# how to use:
#
# > ./star_nodes.sh 20
#
# it is starting 20 nodes, starting in port 9000

# Create a list of folders for testing (logs, pids, data)
./create_test_env.sh

port=9000

for i in $(seq 0 $1) ; do
  ./startup.sh $port 0.0.0.0:9090
  port=$((port+1))
done
