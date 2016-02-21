#! /bin/bash

kill -9 $(cat pids/node.pid)
rm pids/node.pid
