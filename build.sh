#!/usr/bin/env bash

cd client && yarn build
cd ..
rm -rf src/main/resources/public
cp -r client/build src/main/resources/public
mvn clean && mvn package -DskipTests
