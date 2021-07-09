# Change Log

All notable changes to this project will be documented in this file.
See [Conventional Commits](https://conventionalcommits.org) for commit guidelines.

## [1.0.3](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/local-notifications@1.0.2...@capacitor/local-notifications@1.0.3) (2021-07-07)


### Bug Fixes

* **local-notifications:** requestPermissions and checkPermissions return if enabled ([#494](https://github.com/ionic-team/capacitor-plugins/issues/494)) ([555bb1f](https://github.com/ionic-team/capacitor-plugins/commit/555bb1f9bd02ccd999891a316e7ee0f8c1844e92))





## [1.0.2](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/local-notifications@1.0.1...@capacitor/local-notifications@1.0.2) (2021-06-23)

**Note:** Version bump only for package @capacitor/local-notifications





## [1.0.1](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/local-notifications@1.0.0...@capacitor/local-notifications@1.0.1) (2021-06-09)

**Note:** Version bump only for package @capacitor/local-notifications





# [1.0.0](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/local-notifications@0.6.4...@capacitor/local-notifications@1.0.0) (2021-05-19)

**Note:** Version bump only for package @capacitor/local-notifications





## [0.6.4](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/local-notifications@0.6.3...@capacitor/local-notifications@0.6.4) (2021-05-11)

**Note:** Version bump only for package @capacitor/local-notifications





## [0.6.3](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/local-notifications@0.6.2...@capacitor/local-notifications@0.6.3) (2021-05-10)

**Note:** Version bump only for package @capacitor/local-notifications





## [0.6.2](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/local-notifications@0.6.1...@capacitor/local-notifications@0.6.2) (2021-05-07)

**Note:** Version bump only for package @capacitor/local-notifications





## [0.6.1](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/local-notifications@0.6.0...@capacitor/local-notifications@0.6.1) (2021-04-29)


### Bug Fixes

* **local-notifications:** don't store notifications if not scheduled ([#310](https://github.com/ionic-team/capacitor-plugins/issues/310)) ([c1445fd](https://github.com/ionic-team/capacitor-plugins/commit/c1445fddc69db27506f83b3ea56d8e90d4384346))
* **local-notifications:** extra not being returned on notification events ([#340](https://github.com/ionic-team/capacitor-plugins/issues/340)) ([5b03a7f](https://github.com/ionic-team/capacitor-plugins/commit/5b03a7fdbfd2e8293a9fc3726185b516d7efa9a9))





# [0.6.0](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/local-notifications@0.5.1...@capacitor/local-notifications@0.6.0) (2021-03-10)


### Bug Fixes

* **local-notification:** Throw unavailable if Notification API not supported ([#285](https://github.com/ionic-team/capacitor-plugins/issues/285)) ([a90a88b](https://github.com/ionic-team/capacitor-plugins/commit/a90a88b217f5fa2a257416050afb476dd84d8051))
* **local-notifications:** Adding check for `new Notification` support ([#295](https://github.com/ionic-team/capacitor-plugins/issues/295)) ([a806f22](https://github.com/ionic-team/capacitor-plugins/commit/a806f22577209322bdc93ef7fe5490d3b0b6e42f))


### Features

* **local-notifications:** Adding summary text to grouped notifications ([#296](https://github.com/ionic-team/capacitor-plugins/issues/296)) ([f625bd2](https://github.com/ionic-team/capacitor-plugins/commit/f625bd28bc00dbd0b51d7bdecf5e6f3077dcc7a9))
* **local-notifications:** Support for Big Text and Inbox Notification Style ([#280](https://github.com/ionic-team/capacitor-plugins/issues/280)) ([dc96ef9](https://github.com/ionic-team/capacitor-plugins/commit/dc96ef923725f5b53346431d35f82d5ff13f4e17))





## [0.5.1](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/local-notifications@0.5.0...@capacitor/local-notifications@0.5.1) (2021-03-02)

**Note:** Version bump only for package @capacitor/local-notifications





# [0.5.0](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/local-notifications@0.4.0...@capacitor/local-notifications@0.5.0) (2021-02-27)


### Bug Fixes

* **local-notifications:** Checking for null schedule in notification JSObject ([#258](https://github.com/ionic-team/capacitor-plugins/issues/258)) ([73cb416](https://github.com/ionic-team/capacitor-plugins/commit/73cb4168329622bb5a6625c900090a01fc5eca99))
* **local-notifications:** Make getPending not return already fired notifications ([#256](https://github.com/ionic-team/capacitor-plugins/issues/256)) ([fb96f8a](https://github.com/ionic-team/capacitor-plugins/commit/fb96f8ab8c4776528e5825be6c2e19567462eef8))
* **local-notifications:** Opt out of Capacitor date serialization ([#264](https://github.com/ionic-team/capacitor-plugins/issues/264)) ([6e447d5](https://github.com/ionic-team/capacitor-plugins/commit/6e447d54aff3cac47df540addf2a0bf05238c158))


### Features

* **local-notifications:** Support setting seconds in Schedule "on" ([#253](https://github.com/ionic-team/capacitor-plugins/issues/253)) ([4ec8d06](https://github.com/ionic-team/capacitor-plugins/commit/4ec8d06e0cb52403e541a05e5c3518d4c5ea754e))





# [0.4.0](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/local-notifications@0.3.0...@capacitor/local-notifications@0.4.0) (2021-02-10)


### Features

* **android:** implements Activity Result API changes for permissions and activity results ([#222](https://github.com/ionic-team/capacitor-plugins/issues/222)) ([f671b9f](https://github.com/ionic-team/capacitor-plugins/commit/f671b9f4b472806ef43db6dcf302d4503cf1828c))





# [0.3.0](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/local-notifications@0.2.1...@capacitor/local-notifications@0.3.0) (2021-02-05)


### Bug Fixes

* **android:** fire localNotificationReceived event on Android ([#217](https://github.com/ionic-team/capacitor-plugins/issues/217)) ([d97682d](https://github.com/ionic-team/capacitor-plugins/commit/d97682d9f3d6f612993716c3bc35d3015c4e0c07))
* normalize use of integers for notification IDs ([#195](https://github.com/ionic-team/capacitor-plugins/issues/195)) ([b56e111](https://github.com/ionic-team/capacitor-plugins/commit/b56e1118227ee58d1872dbb32a18b8484290d3c7))
* **web:** fix scheduled notifications not being sent ([#220](https://github.com/ionic-team/capacitor-plugins/issues/220)) ([c8e92d6](https://github.com/ionic-team/capacitor-plugins/commit/c8e92d6a178f8b3278b1d3a9c364eb8120d28848))


### Features

* **local-notifications:** add more info to pending notifications ([#211](https://github.com/ionic-team/capacitor-plugins/issues/211)) ([7c50487](https://github.com/ionic-team/capacitor-plugins/commit/7c50487d40836380a27bd4c8d3655d83e0c3a720))
* **local-notifications:** Fire local notifications while app is idle ([#237](https://github.com/ionic-team/capacitor-plugins/issues/237)) ([43380ef](https://github.com/ionic-team/capacitor-plugins/commit/43380efa8901adf9d669d0c1ef20038a2fd7df8e))
* **web:** implement ActionPerformed and Received events ([#219](https://github.com/ionic-team/capacitor-plugins/issues/219)) ([e062901](https://github.com/ionic-team/capacitor-plugins/commit/e062901fc2e55cf6b6dc1ab20258d80a0be8b2d9))





## [0.2.1](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/local-notifications@0.2.0...@capacitor/local-notifications@0.2.1) (2021-01-26)


### Bug Fixes

* Use the event names from Capacitor 2 ([#215](https://github.com/ionic-team/capacitor-plugins/issues/215)) ([008fe9e](https://github.com/ionic-team/capacitor-plugins/commit/008fe9e9bf6a960b0ab7b6fc4d5014f10ba13df8))





# [0.2.0](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/local-notifications@0.1.0...@capacitor/local-notifications@0.2.0) (2021-01-14)

**Note:** Version bump only for package @capacitor/local-notifications





# 0.1.0 (2021-01-13)


### Bug Fixes

* add es2017 lib to tsconfig ([#180](https://github.com/ionic-team/capacitor-plugins/issues/180)) ([2c3776c](https://github.com/ionic-team/capacitor-plugins/commit/2c3776c38ca025c5ee965dec10ccf1cdb6c02e2f))


### Features

* add commonjs output format ([#179](https://github.com/ionic-team/capacitor-plugins/issues/179)) ([8e9e098](https://github.com/ionic-team/capacitor-plugins/commit/8e9e09862064b3f6771d7facbc4008e995d9b463))
* Local Notifications plugin ([#94](https://github.com/ionic-team/capacitor-plugins/issues/94)) ([e59ba9c](https://github.com/ionic-team/capacitor-plugins/commit/e59ba9ceea78a26ec60e521825f228baa9d74577))
