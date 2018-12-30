#!/usr/bin/env bash

export CI=true
cd client && yarn test
cd ..
mvn test
