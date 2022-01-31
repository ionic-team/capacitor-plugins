#!/bin/bash

cd ../ && npm install && npm run pack-local
cd ./e2e-tests && npm install ../capacitor-google-maps.tgz