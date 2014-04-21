#!/bin/bash

WD=$(cd $(dirname $0) && pwd)
DEPS="$WD/target/dependency"
CLASSES="$WD/target/classes"

if [ ! -e $DEPS ]; then
    echo "$DEPS does not exist. If source is checked out, please run:"
    echo " $ mvn clean package"
    exit 1
fi

CONFIG=$1; shift
echo $CONFIG
if [ "x" == "x$CONFIG" ] || [ ! -e $CONFIG ]; then
    echo "config does not exist, or no config specified. Example usage:"
    echo " $ ./start.sh example/single-node-local/config.properties # single node setup"
    exit 1
fi

MAIN="com.comcast.xfinity.sirius.refapplication.StartServer"
CP="$CLASSES"
for JAR in $(find $DEPS); do
    CP="$CP:$JAR"
done

java -cp "$CP" $MAIN $CONFIG $@

