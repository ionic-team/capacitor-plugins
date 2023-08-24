#!/bin/bash

CPU_ARCH=`uname -p`""

SDKMANAGER=$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager
AVDMANAGER=$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/avdmanager
ABD=$ANDROID_SDK_ROOT/platform-tools/adb
EMU=$ANDROID_SDK_ROOT/emulator/emulator

E2E_DEVICE_EXISTS=`${EMU} -list-avds | grep -c e2eDevice`

if [[ $E2E_DEVICE_EXISTS -lt 1 ]]; then
  echo "Creating e2eDevice AVD..."
  echo ""

  echo "CPU_ARCH: $CPU_ARCH"
  echo "JAVA_HOME: $JAVA_HOME"
  echo "ANDROID_SDK_ROOT: $ANDROID_SDK_ROOT"
  echo "" 
  echo "--------------------------"
  echo "" 

  echo "Accepting Lics..."
  sh -c \yes | ${SDKMANAGER} --licenses > /dev/null

  echo "Installing build-tools..." 
  ${SDKMANAGER} --install 'build-tools;31.0.0' platform-tools 'platforms;android-31' 
  echo "Installing emulator..." 
  ${SDKMANAGER} --install emulator --channel=0 
  echo "Installing sys-image..." 

  # If we are on arm (m1 mac) use arm images, else use x86_64
  if [ "$CPU_ARCH" = "arm" ]; then
    ${SDKMANAGER} --install 'system-images;android-31;google_apis;arm64-v8a' --channel=0 
  else
    ${SDKMANAGER} --install 'system-images;android-31;google_apis;x86_64' --channel=0 
  fi

  echo "Killing all running emulators..." 
  ${ABD} devices | grep emulator | cut -f1 | while read line; do ${ABD} -s $line emu kill; done;

  echo "Creating AVD..." 
  if [ "$CPU_ARCH" = "arm" ]; then
    ${AVDMANAGER} --verbose create avd -n e2eDevice -k "system-images;android-31;google_apis;arm64-v8a" --device "pixel_3a"
  else
    ${AVDMANAGER} --verbose create avd -n e2eDevice -k "system-images;android-31;google_apis;x86_64" --device "pixel_3a"
  fi

else
  echo "e2eDevice AVD exists!"
fi

echo ""


