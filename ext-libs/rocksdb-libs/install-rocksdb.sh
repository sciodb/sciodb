#!/bin/bash

mvn install:install-file -Dfile=../rocksdb-libs/rocksdbjni-4.6.0-osx.jar -DgroupId=com.facebook -DartifactId=rocksdbjni -Dversion=4.6.0-osx -Dpackaging=jar