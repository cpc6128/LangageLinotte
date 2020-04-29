#!/bin/sh

if [ $JAVA_HOME ]; then
   $JAVA_HOME/bin/java -jar ../../../../jinotte.jar -x crazy_pong.liv
else
         /usr/bin/java -jar ../../../../jinotte.jar -x crazy_pong.liv
fi
