#! /bin/bash

kill -9 $(cat pids/node_$1.pid)
rm pids/node_$1.pid
