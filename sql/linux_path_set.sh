#!/bin/sh
CLASSPATH=.
for file in ./md5libs/*.jar; do
    CLASSPATH=$CLASSPATH:$file
done 
export CLASSPATH