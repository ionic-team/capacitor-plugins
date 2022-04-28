#!/bin/bash

set -e

if [[ -z "$ANDROID_SDK_ROOT" ]]; then
  export ANDROID_SDK_ROOT=~/Library/Android/sdk
fi
if [[ -n "$JAVA_HOME_11_X64" ]]; then
  export JAVA_HOME=$JAVA_HOME_11_X64
else
  export JAVA_HOME=/Applications/Android\ Studio.app/Contents/jre/Contents/Home
fi

# Check to see if we are not in a Github Action
if [[ -z $GITHUB_ACTION ]]; then

  ABD=$ANDROID_SDK_ROOT/platform-tools/adb
  EMU=$ANDROID_SDK_ROOT/emulator/emulator
  
  echo "Not in GH Action. Checking for e2eDevice Emulator..."
  $PWD/scripts/create_E2E_AVD.sh

  set +e

  echo 'Searching for e2eDevice...'
  devicesCount=`${ABD} devices | grep -c emulator`

  if [[ $devicesCount =~ 0 ]]; then 
    echo 'Starting emulator...'
    ${EMU} -avd "e2eDevice" -no-window -noaudio -accel on -no-boot-anim -no-snapshot-save &
  fi

  bootanim=""
  failcounter=0
  timeout_in_sec=360

  until [[ "$bootanim" =~ "stopped" ]]; do
    bootanim=`${ABD} -e shell getprop init.svc.bootanim 2>&1 &`
    if [[ "$bootanim" =~ "device not found" || "$bootanim" =~ "device offline"
      || "$bootanim" =~ "running" ]]; then
      let "failcounter += 1"
      echo "Waiting for emulator to start"
      if [[ $failcounter -gt timeout_in_sec ]]; then
        echo "Timeout ($timeout_in_sec seconds) reached; failed to start emulator"
        exit 1
      fi
    fi
    sleep 1
  done

  echo "Emulator is ready"

  sleep 4

  set -e

fi

E2E_MODE=simulator

npm run sync 
npm run e2e:android:build
npm run e2e:android:run
