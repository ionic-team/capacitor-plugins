exports.config = {
    "autoCompileOpts": {
      "autoCompile": true,
      "tsNodeOpts": {
        "transpileOnly": true
      },
      "tsConfigPathsOpts": {
        "paths": {},
        "baseUrl": "./"
      }
    },
    "runner": "local",
    "specs": [
      [
        "./tests/specs/**/*.spec.ts"
      ]
    ],
    "logLevel": "trace",
    "bail": 0,
    "waitforTimeout": 45000,
    "connectionRetryTimeout": 120000,
    "connectionRetryCount": 3,
    "framework": "mocha",
    "reporters": [
      "spec"
    ],
    "mochaOpts": {
      "timeout": 1200000
    },
    "services": [
      [
        "appium",
        {
          "command": "appium",
          "args": {
            "relaxedSecurity": true
          }
        }
      ],
      [
        "chromedriver",
        {
          "args": [
            "--use-fake-ui-for-media-stream",
            "--use-fake-device-for-media-stream"
          ]
        }
      ]
    ],
    "port": 4723,
    "ios:simulator": {
      "platformName": "iOS",
      "maxInstances": 1,
      "appium:isHeadless": true,
      "appium:deviceName": "iPhone 13 Pro Max",
      "appium:platformVersion": "15.2",
      "appium:orientation": "PORTRAIT",
      "appium:automationName": "XCUITest",
      "appium:app": "./.ionic/App-ios-simulator.zip",
      "appium:newCommandTimeout": 240,
      "appium:platformName": "iOS",
      "appium:wdaLaunchTimeout": 600000
    },
    "ios:device": {
      "platformName": "iOS",
      "maxInstances": 1,
      "appium:isHeadless": false,
      "appium:deviceName": "iPhone 12 Pro Max",
      "appium:platformVersion": "15.2",
      "appium:orientation": "PORTRAIT",
      "appium:automationName": "XCUITest",
      "appium:app": "./.ionic/App-ios-simulator.zip",
      "appium:newCommandTimeout": 240
    },
    "ios:browser": {
      "browserName": "safari",
      "platformName": "iOS",
      "maxInstances": 1,
      "appium:isHeadless": false,
      "appium:deviceName": "iPhone 13 Pro Max",
      "appium:platformVersion": "15.0",
      "appium:orientation": "PORTRAIT",
      "appium:automationName": "XCUITest",
      "appium:newCommandTimeout": 240
    },
    "android:emulator": {
      "platformName": "Android",
      "maxInstances": 1,
      "appium:isHeadless": true,
      "appium:deviceName": "e2eDevice",
      "appium:platformVersion": "11.0",
      "appium:orientation": "PORTRAIT",
      "appium:automationName": "UiAutomator2",
      "appium:app": "./.ionic/app-debug.apk",
      "appium:appWaitActivity": "io.ionic.starter.MainActivity",
      "appium:newCommandTimeout": 300,
      "appium:platformName": "Android",
      "appium:avd": "e2eDevice",
      "appium:appPackage": "io.ionic.starter",
      "appium:autoGrantPermissions": true,
      "appium:allowTestPackages": true,
      "appium:appWaitDuration": 60000,
      "appium:adbExecTimeout": 300000,
      "appium:deviceReadyTimeout": 3000,
      "appium:androidDeviceReadyTimeout": 3000,
      "appium:avdLaunchTimeout": 300000,
      "appium:avdReadyTimeout": 300000,
      "appium:appWaitForLaunch": false,
      "appium:avdArgs": "-no-window -noaudio -verbose -accel on -no-boot-anim -no-snapshot-save"
    },
    "android:device": {
      "platformName": "Android",
      "maxInstances": 1,
      "appium:isHeadless": false,
      "appium:deviceName": "G8X",
      "appium:platformVersion": "11",
      "appium:orientation": "PORTRAIT",
      "appium:automationName": "UiAutomator2",
      "appium:app": "./.ionic/app-debug.apk",
      "appium:appWaitActivity": "io.ionic.starter.MainActivity",
      "appium:newCommandTimeout": 240
    },
    "android:browser": {
      "platformName": "Android",
      "browserName": "chrome",
      "maxInstances": 1,
      "appium:isHeadless": false,
      "appium:deviceName": "e2eDevice",
      "appium:platformVersion": "12",
      "appium:orientation": "PORTRAIT",
      "appium:newCommandTimeout": 240
    },
    "web:chrome": {
      "maxInstances": 1,
      "browserName": "chrome",
      "wdio:devtoolsOptions": {
        "headless": true
      },
      "goog:chromeOptions": {
        "prefs": {
          "profile.default_content_setting_values.media_stream_camera": 1,
          "profile.default_content_setting_values.media_stream_mic": 1,
          "profile.default_content_setting_values.notifications": 1
        }
      }
    },
    "capabilities": [
      {
        "platformName": "Android",
        "maxInstances": 1,
        "appium:isHeadless": true,
        "appium:deviceName": "e2eDevice",
        "appium:platformVersion": "11.0",
        "appium:orientation": "PORTRAIT",
        "appium:automationName": "UiAutomator2",
        "appium:app": "./.ionic/app-debug.apk",
        "appium:appWaitActivity": "io.ionic.starter.MainActivity",
        "appium:newCommandTimeout": 300,
        "appium:platformName": "Android",
        "appium:avd": "e2eDevice",
        "appium:appPackage": "io.ionic.starter",
        "appium:autoGrantPermissions": true,
        "appium:allowTestPackages": true,
        "appium:appWaitDuration": 60000,
        "appium:adbExecTimeout": 300000,
        "appium:deviceReadyTimeout": 3000,
        "appium:androidDeviceReadyTimeout": 3000,
        "appium:avdLaunchTimeout": 300000,
        "appium:avdReadyTimeout": 300000,
        "appium:appWaitForLaunch": false,
        "appium:avdArgs": "-no-window -noaudio -verbose -accel on -no-boot-anim -no-snapshot-save"
      }
    ]
  }