#!/bin/bash

WD=$(cd $(dirname $0) && pwd)
DEPS="$WD/target/dependency"
CLASSES="$WD/target/classes"

check_pid() {
    echo $(ps -p $1 > /dev/null 2>&1; echo $?)
}

if [ ! -e $DEPS ]; then
    echo "$DEPS does not exist. If source is checked out, please run:"
    echo " $ mvn clean package"
    exit 1
fi

CONFIG=$1; shift
if [ "x" == "x$CONFIG" ] || [ ! -e $CONFIG ]; then
    echo "config does not exist, or no config specified. Example usage:"
    echo " $ ./start.sh example/single-node-local/config.properties # single node setup"
    exit 1
fi

if [ -e $WD/run.pid ] && [ "$(check_pid `cat $WD/run.pid`)" == "0" ]; then
    echo "Daemon currently running. Must be stopped before starting another."
    exit 0
fi

MAIN="com.comcast.xfinity.sirius.refapplication.StartServer"
CP="$CLASSES"
for JAR in $(find $DEPS); do
    CP="$CP:$JAR"
done

nohup java -cp "$CP" $MAIN $CONFIG $@ > $WD/output.log 2>&1 &
echo $! > $WD/run.pid
sleep .5
