#!/bin/bash

cd ../ && npm install && npm run build && npm pack
cd ../e2e-tests && npm install ../capacitor-google-maps.tgz