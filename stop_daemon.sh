#!/bin/bash

WD=$(cd $(dirname $0) && pwd)
if [ -e $WD/run.pid ]; then
    kill `cat $WD/run.pid`
    rm $WD/run.pid
fi
