#!/bin/bash

set -e 

cd "$(dirname "$0")/.."

mvn -N install
mvn -DskipTests=true -T 4 clean install

(cd arquivo-crawler && mvn -DskipTests=true package jdeb:jdeb)
(cd arquivo-processor && mvn -DskipTests=true package jdeb:jdeb)
(cd arquivo-text && mvn -DskipTests=true package jdeb:jdeb)
(cd arquivo-rest && mvn -DskipTests=true package jdeb:jdeb)

echo
echo "-------------------------------------------------------------------------"
echo "Built DEB files:"
echo "-------------------------------------------------------------------------"
echo
find -name "*.deb"
