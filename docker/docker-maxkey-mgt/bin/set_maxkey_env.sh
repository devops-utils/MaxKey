#!/bin/bash
echo "-------------------------------------------------------------------------------"
echo "--    Maxkey Sigle Sign On  System                                           --"
echo "--    Set JAVA_HOME  ....                                                    --"
echo "--    JAVA_HOME   JDK                                                        --"

JAVA_HOME=/usr/local/openjdk-8

export JAVA_HOME=/usr/local/openjdk-8

$JAVA_HOME/bin/java -version
echo "--    JAVA_HOME  $JAVA_HOME "
echo "-------------------------------------------------------------------------------"