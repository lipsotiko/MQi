#!/usr/bin/env bash

cd client && npm run build
cd ..
rm -rf src/main/resources/public
cp -r client/build src/main/resources/public
