#! /bin/bash

#TODO change this mechanism
kill -9 $(cat pids/node_$1.pid)
rm pids/node_$1.pid
