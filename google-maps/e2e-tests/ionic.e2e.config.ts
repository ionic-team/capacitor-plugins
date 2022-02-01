/* eslint-disable import/no-anonymous-default-export */
export default {
  appRootDir: '.',
  wdio: {
    logLevel: 'trace', // options are "trace" or "error"
    'ios:simulator': {
      'appium:platformName': 'iOS',
      'appium:deviceName': 'iPhone 13 Pro Max',
      'appium:platformVersion': '15.2',
      'appium:isHeadless': true,
      'appium:wdaLaunchTimeout': 600000,
    },
    'ios:device': {},
    'android:emulator': {
      'appium:platformName': 'Android',
      'appium:deviceName': 'e2eDevice',
      'appium:avd': 'e2eDevice',
      'appium:isHeadless': true,
      'appium:platformVersion': '11.0',
      'appium:appPackage': 'io.ionic.starter',
      'appium:appWaitActivity': 'io.ionic.starter.MainActivity',
      'appium:autoGrantPermissions': true,
      'appium:allowTestPackages': true,
      'appium:appWaitDuration': 60000,
      'appium:adbExecTimeout': 300000,
      'appium:deviceReadyTimeout': 3000,
      'appium:androidDeviceReadyTimeout': 3000,
      'appium:avdLaunchTimeout': 300000,
      'appium:avdReadyTimeout': 300000,
      'appium:appWaitForLaunch': false,
      'appium:newCommandTimeout': 300,
      'appium:avdArgs': '-no-window -noaudio -verbose -accel on -no-boot-anim -no-snapshot-save',
    },
    'android:device': {},
  },
};
