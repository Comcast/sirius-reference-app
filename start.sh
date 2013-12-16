#!/bin/bash

WD=$(cd $(dirname $0) && pwd)
DEPS="$WD/target/dependency"
CLASSES="$WD/target/classes"

if [ ! -e $DEPS ]; then
    echo "$DEPS does not exist. Please run:"
    echo " $ mvn clean package"
    exit 1
fi

CONFIG=$1; shift
echo $CONFIG
if [ "x" == "x$CONFIG" ] || [ ! -e $CONFIG ]; then
    echo "config does not exist, or no config specified. Usage:"
    echo " $ ./start.sh /path/to/config.properties"
    exit 1
fi

MAIN="com.comcast.xfinity.sirius.refapplication.StartServer"
CP="$CLASSES"
for JAR in $(find $DEPS); do
    CP="$CP:$JAR"
done

echo "java -cp '$CP' $MAIN $CONFIG $@"
java -cp "$CP" $MAIN $CONFIG $@

