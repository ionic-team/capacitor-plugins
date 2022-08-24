# Change Log

All notable changes to this project will be documented in this file.
See [Conventional Commits](https://conventionalcommits.org) for commit guidelines.

# [4.1.0](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/filesystem@1.1.0...@capacitor/filesystem@4.1.0) (2022-08-24)


### Bug Fixes

* **filesystem:** failing to remove folder content on rmdir ([#1112](https://github.com/ionic-team/capacitor-plugins/issues/1112)) ([ae451aa](https://github.com/ionic-team/capacitor-plugins/commit/ae451aa08beb2138ecebdfcdd26101660aa00fde))
* **filesystem:** make iOS return proper url on readdir ([#1142](https://github.com/ionic-team/capacitor-plugins/issues/1142)) ([77dc02f](https://github.com/ionic-team/capacitor-plugins/commit/77dc02fb829ad3479144368da16f9fff324f2706))



## 4.0.1 (2022-07-28)



# 4.0.0 (2022-07-27)



# 4.0.0-beta.2 (2022-07-08)



# 4.0.0-beta.0 (2022-06-27)


### Bug Fixes

* **filesystem:** Prevent android crash on invalid base64 write ([#937](https://github.com/ionic-team/capacitor-plugins/issues/937)) ([1af0bfe](https://github.com/ionic-team/capacitor-plugins/commit/1af0bfe24d2a36bc2949fe52866131c3327b321e))
* **filesystem:** web appendFile with base64 data ([#928](https://github.com/ionic-team/capacitor-plugins/issues/928)) ([80253cf](https://github.com/ionic-team/capacitor-plugins/commit/80253cf2652bf7fa9c07933989cbdffeadd52a27))


### Features

* set targetSDK default value to 32 ([#970](https://github.com/ionic-team/capacitor-plugins/issues/970)) ([fa70d96](https://github.com/ionic-team/capacitor-plugins/commit/fa70d96f141af751aae53ceb5642c46b204f5958))
* **filesystem:** Make readDir return files information ([#949](https://github.com/ionic-team/capacitor-plugins/issues/949)) ([0a9f43d](https://github.com/ionic-team/capacitor-plugins/commit/0a9f43dffd3815f600c35ed4528c017644fdb55e))
* **filesystem:** Return path of copied file ([#931](https://github.com/ionic-team/capacitor-plugins/issues/931)) ([310f583](https://github.com/ionic-team/capacitor-plugins/commit/310f583ccec58730ab8046a1618782c950c60656))
* set targetSDK default value to 31 ([#824](https://github.com/ionic-team/capacitor-plugins/issues/824)) ([3ee10de](https://github.com/ionic-team/capacitor-plugins/commit/3ee10de98067984c1a4e75295d001c5a895c47f4))
* Upgrade gradle to 7.4 ([#826](https://github.com/ionic-team/capacitor-plugins/issues/826)) ([5db0906](https://github.com/ionic-team/capacitor-plugins/commit/5db0906f6264287c4f8e69dbaecf19d4d387824b))
* Use java 11 ([#910](https://github.com/ionic-team/capacitor-plugins/issues/910)) ([5acb2a2](https://github.com/ionic-team/capacitor-plugins/commit/5acb2a288a413492b163e4e97da46a085d9e4be0))





## [4.0.1](https://github.com/ionic-team/capacitor-plugins/compare/4.0.0...4.0.1) (2022-07-28)

**Note:** Version bump only for package @capacitor/filesystem





# [4.0.0](https://github.com/ionic-team/capacitor-plugins/compare/4.0.0-beta.2...4.0.0) (2022-07-27)

**Note:** Version bump only for package @capacitor/filesystem





# [4.0.0-beta.2](https://github.com/ionic-team/capacitor-plugins/compare/4.0.0-beta.0...4.0.0-beta.2) (2022-07-08)

**Note:** Version bump only for package @capacitor/filesystem





# 4.0.0-beta.0 (2022-06-27)


### Bug Fixes

* **filesystem:** Prevent android crash on invalid base64 write ([#937](https://github.com/ionic-team/capacitor-plugins/issues/937)) ([1af0bfe](https://github.com/ionic-team/capacitor-plugins/commit/1af0bfe24d2a36bc2949fe52866131c3327b321e))
* **filesystem:** Throw errors instead of strings ([#746](https://github.com/ionic-team/capacitor-plugins/issues/746)) ([af4b875](https://github.com/ionic-team/capacitor-plugins/commit/af4b8750be512b869af07bcf96c1602eedc6758e))
* **filesystem:** web appendFile with base64 data ([#928](https://github.com/ionic-team/capacitor-plugins/issues/928)) ([80253cf](https://github.com/ionic-team/capacitor-plugins/commit/80253cf2652bf7fa9c07933989cbdffeadd52a27))
* correct addListeners links ([#655](https://github.com/ionic-team/capacitor-plugins/issues/655)) ([f9871e7](https://github.com/ionic-team/capacitor-plugins/commit/f9871e7bd53478addb21155e148829f550c0e457))
* inline source code in esm map files ([#760](https://github.com/ionic-team/capacitor-plugins/issues/760)) ([a960489](https://github.com/ionic-team/capacitor-plugins/commit/a960489a19db0182b90d187a50deff9dfbe51038))
* remove postpublish scripts ([#656](https://github.com/ionic-team/capacitor-plugins/issues/656)) ([ed6ac49](https://github.com/ionic-team/capacitor-plugins/commit/ed6ac499ebf4a47525071ccbfc36c27503e11f60))
* **android:** permissions use "publicStorage" as alias ([#202](https://github.com/ionic-team/capacitor-plugins/issues/202)) ([2dfc7a3](https://github.com/ionic-team/capacitor-plugins/commit/2dfc7a3261a4f98871a86fe6d47fab084a2d1deb))
* **android:** support writing files without scheme ([#241](https://github.com/ionic-team/capacitor-plugins/issues/241)) ([4285cb1](https://github.com/ionic-team/capacitor-plugins/commit/4285cb1d37ec3361e7ec4da4786502693b04d478))
* **filesystem:** allow copy if from is not parent of to ([#546](https://github.com/ionic-team/capacitor-plugins/issues/546)) ([a70414e](https://github.com/ionic-team/capacitor-plugins/commit/a70414e79189579ff1a0b5c2a90d12491f5c23cf))
* **filesystem:** Append doesn't resolve on iOS ([#305](https://github.com/ionic-team/capacitor-plugins/issues/305)) ([98e91cd](https://github.com/ionic-team/capacitor-plugins/commit/98e91cd745fb12bf46f99233bb527f147dbba58b))
* **filesystem:** Convert stat ctime/mtime timestamp to milliseconds ([#321](https://github.com/ionic-team/capacitor-plugins/issues/321)) ([d978986](https://github.com/ionic-team/capacitor-plugins/commit/d97898662d0ba037e5f8448990a91de5ec6a4234))
* **filesystem:** copy doesn't resolve on Android ([#233](https://github.com/ionic-team/capacitor-plugins/issues/233)) ([17cbf3b](https://github.com/ionic-team/capacitor-plugins/commit/17cbf3b0ada97f1279fba32b551c380c0e669406))
* **filesystem:** is not requesting permission on public directories ([#246](https://github.com/ionic-team/capacitor-plugins/issues/246)) ([aa897ab](https://github.com/ionic-team/capacitor-plugins/commit/aa897ab4269e34cd5d762ed645030054ddda7dd6))
* **filesystem:** Make ctime optional ([#373](https://github.com/ionic-team/capacitor-plugins/issues/373)) ([e3c6212](https://github.com/ionic-team/capacitor-plugins/commit/e3c6212b94c75cf747a8768af5056963683953b2))
* **filesystem:** rmdir doesn't resolve on iOS ([#239](https://github.com/ionic-team/capacitor-plugins/issues/239)) ([7ca538b](https://github.com/ionic-team/capacitor-plugins/commit/7ca538bb47e2e00080eadfe8d875323c1e198cb2))
* add es2017 lib to tsconfig ([#180](https://github.com/ionic-team/capacitor-plugins/issues/180)) ([2c3776c](https://github.com/ionic-team/capacitor-plugins/commit/2c3776c38ca025c5ee965dec10ccf1cdb6c02e2f))
* support deprecated types from Capacitor 2 ([#139](https://github.com/ionic-team/capacitor-plugins/issues/139)) ([2d7127a](https://github.com/ionic-team/capacitor-plugins/commit/2d7127a488e26f0287951921a6db47c49d817336))
* **filesystem:** Use PermissionState from @capacitor/core ([#148](https://github.com/ionic-team/capacitor-plugins/issues/148)) ([5ce3c5d](https://github.com/ionic-team/capacitor-plugins/commit/5ce3c5d491a35b8771661f3e4eb98aac6df15911))


### Features

* set targetSDK default value to 32 ([#970](https://github.com/ionic-team/capacitor-plugins/issues/970)) ([fa70d96](https://github.com/ionic-team/capacitor-plugins/commit/fa70d96f141af751aae53ceb5642c46b204f5958))
* **filesystem:** Make readDir return files information ([#949](https://github.com/ionic-team/capacitor-plugins/issues/949)) ([0a9f43d](https://github.com/ionic-team/capacitor-plugins/commit/0a9f43dffd3815f600c35ed4528c017644fdb55e))
* **filesystem:** Return path of copied file ([#931](https://github.com/ionic-team/capacitor-plugins/issues/931)) ([310f583](https://github.com/ionic-team/capacitor-plugins/commit/310f583ccec58730ab8046a1618782c950c60656))
* add commonjs output format ([#179](https://github.com/ionic-team/capacitor-plugins/issues/179)) ([8e9e098](https://github.com/ionic-team/capacitor-plugins/commit/8e9e09862064b3f6771d7facbc4008e995d9b463))
* Filesystem plugin ([#19](https://github.com/ionic-team/capacitor-plugins/issues/19)) ([3b86a4a](https://github.com/ionic-team/capacitor-plugins/commit/3b86a4a972e00eaed1d078bfcc69af6136222dc4))
* set targetSDK default value to 31 ([#824](https://github.com/ionic-team/capacitor-plugins/issues/824)) ([3ee10de](https://github.com/ionic-team/capacitor-plugins/commit/3ee10de98067984c1a4e75295d001c5a895c47f4))
* Upgrade gradle to 7.4 ([#826](https://github.com/ionic-team/capacitor-plugins/issues/826)) ([5db0906](https://github.com/ionic-team/capacitor-plugins/commit/5db0906f6264287c4f8e69dbaecf19d4d387824b))
* Use java 11 ([#910](https://github.com/ionic-team/capacitor-plugins/issues/910)) ([5acb2a2](https://github.com/ionic-team/capacitor-plugins/commit/5acb2a288a413492b163e4e97da46a085d9e4be0))
* **android:** implements Activity Result API changes for permissions and activity results ([#222](https://github.com/ionic-team/capacitor-plugins/issues/222)) ([f671b9f](https://github.com/ionic-team/capacitor-plugins/commit/f671b9f4b472806ef43db6dcf302d4503cf1828c))
* **filesystem:** Allow the use of absolute urls on iOS and web ([#250](https://github.com/ionic-team/capacitor-plugins/issues/250)) ([03ad97c](https://github.com/ionic-team/capacitor-plugins/commit/03ad97c1b7450e864504198853aac2b3bdc4b8a4))
* **filesystem:** support Library directory ([#666](https://github.com/ionic-team/capacitor-plugins/issues/666)) ([ce7ee95](https://github.com/ionic-team/capacitor-plugins/commit/ce7ee958b141f1dd4f86493923455f8264d0b6db))





# [1.1.0](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/filesystem@1.0.7...@capacitor/filesystem@1.1.0) (2022-01-19)


### Bug Fixes

* inline source code in esm map files ([#760](https://github.com/ionic-team/capacitor-plugins/issues/760)) ([a960489](https://github.com/ionic-team/capacitor-plugins/commit/a960489a19db0182b90d187a50deff9dfbe51038))


### Features

* **filesystem:** support Library directory ([#666](https://github.com/ionic-team/capacitor-plugins/issues/666)) ([ce7ee95](https://github.com/ionic-team/capacitor-plugins/commit/ce7ee958b141f1dd4f86493923455f8264d0b6db))





## [1.0.7](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/filesystem@1.0.6...@capacitor/filesystem@1.0.7) (2022-01-10)


### Bug Fixes

* **filesystem:** Throw errors instead of strings ([#746](https://github.com/ionic-team/capacitor-plugins/issues/746)) ([af4b875](https://github.com/ionic-team/capacitor-plugins/commit/af4b8750be512b869af07bcf96c1602eedc6758e))





## [1.0.6](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/filesystem@1.0.5...@capacitor/filesystem@1.0.6) (2021-11-03)

**Note:** Version bump only for package @capacitor/filesystem





## [1.0.5](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/filesystem@1.0.4...@capacitor/filesystem@1.0.5) (2021-10-14)


### Bug Fixes

* remove postpublish scripts ([#656](https://github.com/ionic-team/capacitor-plugins/issues/656)) ([ed6ac49](https://github.com/ionic-team/capacitor-plugins/commit/ed6ac499ebf4a47525071ccbfc36c27503e11f60))





## [1.0.4](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/filesystem@1.0.3...@capacitor/filesystem@1.0.4) (2021-10-13)


### Bug Fixes

* correct addListeners links ([#655](https://github.com/ionic-team/capacitor-plugins/issues/655)) ([f9871e7](https://github.com/ionic-team/capacitor-plugins/commit/f9871e7bd53478addb21155e148829f550c0e457))





## [1.0.3](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/filesystem@1.0.2...@capacitor/filesystem@1.0.3) (2021-09-01)


### Bug Fixes

* **filesystem:** allow copy if from is not parent of to ([#546](https://github.com/ionic-team/capacitor-plugins/issues/546)) ([a70414e](https://github.com/ionic-team/capacitor-plugins/commit/a70414e79189579ff1a0b5c2a90d12491f5c23cf))





## [1.0.2](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/filesystem@1.0.1...@capacitor/filesystem@1.0.2) (2021-06-23)

**Note:** Version bump only for package @capacitor/filesystem





## [1.0.1](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/filesystem@1.0.0...@capacitor/filesystem@1.0.1) (2021-06-09)

**Note:** Version bump only for package @capacitor/filesystem





# [1.0.0](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/filesystem@0.5.6...@capacitor/filesystem@1.0.0) (2021-05-19)

**Note:** Version bump only for package @capacitor/filesystem





## [0.5.6](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/filesystem@0.5.5...@capacitor/filesystem@0.5.6) (2021-05-11)

**Note:** Version bump only for package @capacitor/filesystem





## [0.5.5](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/filesystem@0.5.4...@capacitor/filesystem@0.5.5) (2021-05-10)

**Note:** Version bump only for package @capacitor/filesystem





## [0.5.4](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/filesystem@0.5.3...@capacitor/filesystem@0.5.4) (2021-05-07)


### Bug Fixes

* **filesystem:** Make ctime optional ([#373](https://github.com/ionic-team/capacitor-plugins/issues/373)) ([e3c6212](https://github.com/ionic-team/capacitor-plugins/commit/e3c6212b94c75cf747a8768af5056963683953b2))





## [0.5.3](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/filesystem@0.5.2...@capacitor/filesystem@0.5.3) (2021-04-29)


### Bug Fixes

* **filesystem:** Append doesn't resolve on iOS ([#305](https://github.com/ionic-team/capacitor-plugins/issues/305)) ([98e91cd](https://github.com/ionic-team/capacitor-plugins/commit/98e91cd745fb12bf46f99233bb527f147dbba58b))
* **filesystem:** Convert stat ctime/mtime timestamp to milliseconds ([#321](https://github.com/ionic-team/capacitor-plugins/issues/321)) ([d978986](https://github.com/ionic-team/capacitor-plugins/commit/d97898662d0ba037e5f8448990a91de5ec6a4234))





## [0.5.2](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/filesystem@0.5.1...@capacitor/filesystem@0.5.2) (2021-03-10)

**Note:** Version bump only for package @capacitor/filesystem





## [0.5.1](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/filesystem@0.5.0...@capacitor/filesystem@0.5.1) (2021-03-02)

**Note:** Version bump only for package @capacitor/filesystem





# [0.5.0](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/filesystem@0.4.0...@capacitor/filesystem@0.5.0) (2021-02-27)


### Bug Fixes

* **filesystem:** is not requesting permission on public directories ([#246](https://github.com/ionic-team/capacitor-plugins/issues/246)) ([aa897ab](https://github.com/ionic-team/capacitor-plugins/commit/aa897ab4269e34cd5d762ed645030054ddda7dd6))


### Features

* **filesystem:** Allow the use of absolute urls on iOS and web ([#250](https://github.com/ionic-team/capacitor-plugins/issues/250)) ([03ad97c](https://github.com/ionic-team/capacitor-plugins/commit/03ad97c1b7450e864504198853aac2b3bdc4b8a4))





# [0.4.0](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/filesystem@0.3.2...@capacitor/filesystem@0.4.0) (2021-02-10)


### Features

* **android:** implements Activity Result API changes for permissions and activity results ([#222](https://github.com/ionic-team/capacitor-plugins/issues/222)) ([f671b9f](https://github.com/ionic-team/capacitor-plugins/commit/f671b9f4b472806ef43db6dcf302d4503cf1828c))





## [0.3.2](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/filesystem@0.3.1...@capacitor/filesystem@0.3.2) (2021-02-05)


### Bug Fixes

* **android:** support writing files without scheme ([#241](https://github.com/ionic-team/capacitor-plugins/issues/241)) ([4285cb1](https://github.com/ionic-team/capacitor-plugins/commit/4285cb1d37ec3361e7ec4da4786502693b04d478))
* **filesystem:** copy doesn't resolve on Android ([#233](https://github.com/ionic-team/capacitor-plugins/issues/233)) ([17cbf3b](https://github.com/ionic-team/capacitor-plugins/commit/17cbf3b0ada97f1279fba32b551c380c0e669406))
* **filesystem:** rmdir doesn't resolve on iOS ([#239](https://github.com/ionic-team/capacitor-plugins/issues/239)) ([7ca538b](https://github.com/ionic-team/capacitor-plugins/commit/7ca538bb47e2e00080eadfe8d875323c1e198cb2))





## [0.3.1](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/filesystem@0.3.0...@capacitor/filesystem@0.3.1) (2021-01-26)


### Bug Fixes

* **android:** permissions use "publicStorage" as alias ([#202](https://github.com/ionic-team/capacitor-plugins/issues/202)) ([2dfc7a3](https://github.com/ionic-team/capacitor-plugins/commit/2dfc7a3261a4f98871a86fe6d47fab084a2d1deb))





# [0.3.0](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/filesystem@0.2.0...@capacitor/filesystem@0.3.0) (2021-01-14)

**Note:** Version bump only for package @capacitor/filesystem





# [0.2.0](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/filesystem@0.1.3...@capacitor/filesystem@0.2.0) (2021-01-13)


### Bug Fixes

* add es2017 lib to tsconfig ([#180](https://github.com/ionic-team/capacitor-plugins/issues/180)) ([2c3776c](https://github.com/ionic-team/capacitor-plugins/commit/2c3776c38ca025c5ee965dec10ccf1cdb6c02e2f))


### Features

* add commonjs output format ([#179](https://github.com/ionic-team/capacitor-plugins/issues/179)) ([8e9e098](https://github.com/ionic-team/capacitor-plugins/commit/8e9e09862064b3f6771d7facbc4008e995d9b463))





## [0.1.3](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/filesystem@0.1.2...@capacitor/filesystem@0.1.3) (2021-01-13)

**Note:** Version bump only for package @capacitor/filesystem





## [0.1.2](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/filesystem@0.1.1...@capacitor/filesystem@0.1.2) (2021-01-08)

**Note:** Version bump only for package @capacitor/filesystem





## [0.1.1](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/filesystem@0.1.0...@capacitor/filesystem@0.1.1) (2020-12-27)

**Note:** Version bump only for package @capacitor/filesystem





# 0.1.0 (2020-12-20)


### Bug Fixes

* support deprecated types from Capacitor 2 ([#139](https://github.com/ionic-team/capacitor-plugins/issues/139)) ([2d7127a](https://github.com/ionic-team/capacitor-plugins/commit/2d7127a488e26f0287951921a6db47c49d817336))
* **filesystem:** Use PermissionState from @capacitor/core ([#148](https://github.com/ionic-team/capacitor-plugins/issues/148)) ([5ce3c5d](https://github.com/ionic-team/capacitor-plugins/commit/5ce3c5d491a35b8771661f3e4eb98aac6df15911))


### Features

* Filesystem plugin ([#19](https://github.com/ionic-team/capacitor-plugins/issues/19)) ([3b86a4a](https://github.com/ionic-team/capacitor-plugins/commit/3b86a4a972e00eaed1d078bfcc69af6136222dc4))
