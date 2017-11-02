#!/usr/bin/env bash

DIR=`dirname $0`
JAR=$(find $DIR/target/ -name 'stretcher-fat*.jar')
java -jar $JAR "$@"
