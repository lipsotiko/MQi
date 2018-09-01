#!/usr/bin/env bash

cd ./server && mvn flyway:clean && mvn clean && mvn spring-boot:run