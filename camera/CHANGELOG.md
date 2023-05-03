# Change Log

All notable changes to this project will be documented in this file.
See [Conventional Commits](https://conventionalcommits.org) for commit guidelines.

# [5.0.0-beta.1](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/camera@5.0.0-beta.0...@capacitor/camera@5.0.0-beta.1) (2023-04-21)


### Features

* Update gradle to 8.0.2 and gradle plugin to 8.0.0 ([#1542](https://github.com/ionic-team/capacitor-plugins/issues/1542)) ([e7210b4](https://github.com/ionic-team/capacitor-plugins/commit/e7210b47867644f5983e37acdbf0247214ec232d))





# [5.0.0-beta.0](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/camera@5.0.0-alpha.1...@capacitor/camera@5.0.0-beta.0) (2023-03-31)


### Bug Fixes

* **camera:** add proper permissions for Android 13 ([#1509](https://github.com/ionic-team/capacitor-plugins/issues/1509)) ([0dcbe56](https://github.com/ionic-team/capacitor-plugins/commit/0dcbe56bc554b1919c3e26d2f6b7ff8e5b7a0f5e))
* **camera:** prevent iOS crash with 0 limited images selected ([#1495](https://github.com/ionic-team/capacitor-plugins/issues/1495)) ([33f5c8e](https://github.com/ionic-team/capacitor-plugins/commit/33f5c8ebc7705c0e697caee4b2177ebc27d46311))





# [5.0.0-alpha.1](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/camera@4.1.4...@capacitor/camera@5.0.0-alpha.1) (2023-03-16)


### Bug Fixes

* **camera:** Handle null permissions list ([#1457](https://github.com/ionic-team/capacitor-plugins/issues/1457)) ([fcd28e9](https://github.com/ionic-team/capacitor-plugins/commit/fcd28e95420207f0e194f9fecab12415c400cba5))


### Features

* **android:** Removing enableJetifier ([d66f9cb](https://github.com/ionic-team/capacitor-plugins/commit/d66f9cbd9da7e3b1d8c64ca6a5b45156867d4a04))





## [4.1.4](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/camera@4.1.3...@capacitor/camera@4.1.4) (2022-11-16)

**Note:** Version bump only for package @capacitor/camera





## [4.1.3](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/camera@4.1.2...@capacitor/camera@4.1.3) (2022-10-21)

**Note:** Version bump only for package @capacitor/camera





## [4.1.2](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/camera@4.1.1...@capacitor/camera@4.1.2) (2022-09-29)


### Bug Fixes

* **camera:** make pickLimitedLibraryPhotos return photos on iOS 15+ ([#1191](https://github.com/ionic-team/capacitor-plugins/issues/1191)) ([a65c8ca](https://github.com/ionic-team/capacitor-plugins/commit/a65c8ca8582c15e16ece369b77d1eac5df43e60e))





## [4.1.1](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/camera@4.1.0...@capacitor/camera@4.1.1) (2022-09-12)

**Note:** Version bump only for package @capacitor/camera





# [4.1.0](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/camera@1.3.1...@capacitor/camera@4.1.0) (2022-08-24)


### Features

* **camera:** Add support for iOS limited photo library mode ([#1125](https://github.com/ionic-team/capacitor-plugins/issues/1125)) ([cc5e4e6](https://github.com/ionic-team/capacitor-plugins/commit/cc5e4e683a2b9a9d216fac9ee88e8653a7ca68c6))




## [4.0.1](https://github.com/ionic-team/capacitor-plugins/compare/4.0.0...4.0.1) (2022-07-28)

**Note:** Version bump only for package @capacitor/camera





# [4.0.0](https://github.com/ionic-team/capacitor-plugins/compare/4.0.0-beta.2...4.0.0) (2022-07-27)

**Note:** Version bump only for package @capacitor/camera





# [4.0.0-beta.2](https://github.com/ionic-team/capacitor-plugins/compare/4.0.0-beta.0...4.0.0-beta.2) (2022-07-08)

**Note:** Version bump only for package @capacitor/camera





# 4.0.0-beta.0 (2022-06-27)


### Bug Fixes

* **camera:** Append change listener only once ([#486](https://github.com/ionic-team/capacitor-plugins/issues/486)) ([5b7021e](https://github.com/ionic-team/capacitor-plugins/commit/5b7021e210649f8501a20ba6549903ecb6d42dcd))
* **camera:** Append exif to android images ([#480](https://github.com/ionic-team/capacitor-plugins/issues/480)) ([cad8a30](https://github.com/ionic-team/capacitor-plugins/commit/cad8a30c562202fb819a4d260d5307f1b6b8fa44))
* **camera:** avoid error if image has no orientation ([#554](https://github.com/ionic-team/capacitor-plugins/issues/554)) ([dc8a55a](https://github.com/ionic-team/capacitor-plugins/commit/dc8a55a71cdaaf7ad86aee8470a0c7b8284653c4))
* **camera:** cleanup camera images if not needed ([#563](https://github.com/ionic-team/capacitor-plugins/issues/563)) ([a2e4f43](https://github.com/ionic-team/capacitor-plugins/commit/a2e4f4339119698e8dd066a5f2f8f065ab2e4727))
* **camera:** correct photo resizing on iOS ([#460](https://github.com/ionic-team/capacitor-plugins/issues/460)) ([bc56e03](https://github.com/ionic-team/capacitor-plugins/commit/bc56e034c711b172a7ff503cabd2970adbc14b86))
* **camera:** decode content uri when retrieving image from gallery ([#277](https://github.com/ionic-team/capacitor-plugins/issues/277)) ([a6cd1ad](https://github.com/ionic-team/capacitor-plugins/commit/a6cd1adc241bf21e4f7f06d24c0db4a4d7382dbc))
* **camera:** Don't save gallery images on iOS 14+ ([#696](https://github.com/ionic-team/capacitor-plugins/issues/696)) ([7b2cc88](https://github.com/ionic-team/capacitor-plugins/commit/7b2cc88f6e83265c991ae9f81cfc3f6bed346250))
* **camera:** fix camera source on Android ([#164](https://github.com/ionic-team/capacitor-plugins/issues/164)) ([e67f7c6](https://github.com/ionic-team/capacitor-plugins/commit/e67f7c6b06b20d7c3e8f0925c40fd75d23d9d717))
* **camera:** Make allowEdit work on all devices ([#552](https://github.com/ionic-team/capacitor-plugins/issues/552)) ([5224177](https://github.com/ionic-team/capacitor-plugins/commit/5224177f77bdce1c8f028e2cef41614fa687502f))
* **camera:** Make input file hidden ([#484](https://github.com/ionic-team/capacitor-plugins/issues/484)) ([cdc1835](https://github.com/ionic-team/capacitor-plugins/commit/cdc1835f3bbfb8db8e18fccace6103d83dd9edaa))
* **camera:** Make web use source options ([#487](https://github.com/ionic-team/capacitor-plugins/issues/487)) ([7870e6b](https://github.com/ionic-team/capacitor-plugins/commit/7870e6b6ca196265640fc0ba3c1f52ddca075607))
* **camera:** process picked image only once ([#782](https://github.com/ionic-team/capacitor-plugins/issues/782)) ([897dcaf](https://github.com/ionic-team/capacitor-plugins/commit/897dcaf839a6cb83256485c32df2ca0e7b439124))
* **camera:** Properly reset orientation exif if corrected ([#545](https://github.com/ionic-team/capacitor-plugins/issues/545)) ([ad8c325](https://github.com/ionic-team/capacitor-plugins/commit/ad8c325af0a2459f5a7788be08a8da4118717671))
* **camera:** query IMAGE_CAPTURE intent required by SDK 30 ([#160](https://github.com/ionic-team/capacitor-plugins/issues/160)) ([6484991](https://github.com/ionic-team/capacitor-plugins/commit/6484991d76d57bac0cbc82b9f050e146ec4732da))
* **camera:** Remove capture attribute from multiple photo picker ([#687](https://github.com/ionic-team/capacitor-plugins/issues/687)) ([e551ef7](https://github.com/ionic-team/capacitor-plugins/commit/e551ef77eebe331cc7bf13c9c0eab5a0bd2da0d1))
* **camera:** Remove unused saveCall ([#401](https://github.com/ionic-team/capacitor-plugins/issues/401)) ([95920da](https://github.com/ionic-team/capacitor-plugins/commit/95920da4d1844ed76a162651d5492a22a4038d26))
* **camera:** Reset exif orientation if corrected ([#510](https://github.com/ionic-team/capacitor-plugins/issues/510)) ([a65c05e](https://github.com/ionic-team/capacitor-plugins/commit/a65c05e0de8f53e7371c194047a75797d53879b5))
* **camera:** Resize not respecting aspect ratio on iOS ([#568](https://github.com/ionic-team/capacitor-plugins/issues/568)) ([ea2b801](https://github.com/ionic-team/capacitor-plugins/commit/ea2b8012aab7e5ea34cfa34735f7f55ba76a3882))
* **camera:** return original image if editing is cancelled ([#566](https://github.com/ionic-team/capacitor-plugins/issues/566)) ([4786841](https://github.com/ionic-team/capacitor-plugins/commit/4786841099403a4d3d59aaf9103e8fa02aa8e4e2))
* **camera:** Return proper exif when picking multiple images ([#712](https://github.com/ionic-team/capacitor-plugins/issues/712)) ([8451237](https://github.com/ionic-team/capacitor-plugins/commit/8451237e46f24c59e74e350eaa9b31e6d99a68a0))
* **camera:** return single picture on pickImages ([#783](https://github.com/ionic-team/capacitor-plugins/issues/783)) ([9d65db1](https://github.com/ionic-team/capacitor-plugins/commit/9d65db1e74117fd1c1e7cd9bbba7efaeb4c13e0c))
* **camera:** saveToGallery for edited images ([#602](https://github.com/ionic-team/capacitor-plugins/issues/602)) ([b5ac27d](https://github.com/ionic-team/capacitor-plugins/commit/b5ac27d59181ec3acc2909b2569d8ab45a829b1c))
* **camera:** set camera direction for web ([#665](https://github.com/ionic-team/capacitor-plugins/issues/665)) ([4afedb9](https://github.com/ionic-team/capacitor-plugins/commit/4afedb96f3b745a86d9cacd33ca71c42ae3fb8d4))
* **camera:** Use Locale.ROOT on toUpperCase ([#812](https://github.com/ionic-team/capacitor-plugins/issues/812)) ([6d689ac](https://github.com/ionic-team/capacitor-plugins/commit/6d689acc48e3746ddd35bd5e1e8d7f239cb7f8df))
* add es2017 lib to tsconfig ([#180](https://github.com/ionic-team/capacitor-plugins/issues/180)) ([2c3776c](https://github.com/ionic-team/capacitor-plugins/commit/2c3776c38ca025c5ee965dec10ccf1cdb6c02e2f))
* correct addListeners links ([#655](https://github.com/ionic-team/capacitor-plugins/issues/655)) ([f9871e7](https://github.com/ionic-team/capacitor-plugins/commit/f9871e7bd53478addb21155e148829f550c0e457))
* Correct missing source_files path ([#590](https://github.com/ionic-team/capacitor-plugins/issues/590)) ([24e0fc2](https://github.com/ionic-team/capacitor-plugins/commit/24e0fc27cc314049012ab9915fa5e7bfb03313e1))
* inline source code in esm map files ([#760](https://github.com/ionic-team/capacitor-plugins/issues/760)) ([a960489](https://github.com/ionic-team/capacitor-plugins/commit/a960489a19db0182b90d187a50deff9dfbe51038))
* remove postpublish scripts ([#656](https://github.com/ionic-team/capacitor-plugins/issues/656)) ([ed6ac49](https://github.com/ionic-team/capacitor-plugins/commit/ed6ac499ebf4a47525071ccbfc36c27503e11f60))
* **camera:** return file URL for path, not system path ([#170](https://github.com/ionic-team/capacitor-plugins/issues/170)) ([8a9e5c3](https://github.com/ionic-team/capacitor-plugins/commit/8a9e5c3dba3b232a1cca9f9a1e9b4520022abc09))
* **camera:** Return the full webPath ([#502](https://github.com/ionic-team/capacitor-plugins/issues/502)) ([e849732](https://github.com/ionic-team/capacitor-plugins/commit/e849732dbcf5e85d1df09835c53ff5738fbb4ded))
* **camera:** set settings again on callbacks ([#595](https://github.com/ionic-team/capacitor-plugins/issues/595)) ([908bd68](https://github.com/ionic-team/capacitor-plugins/commit/908bd688767e374cf8e96b3def08bd33dcdfd2aa))
* support deprecated types from Capacitor 2 ([#139](https://github.com/ionic-team/capacitor-plugins/issues/139)) ([2d7127a](https://github.com/ionic-team/capacitor-plugins/commit/2d7127a488e26f0287951921a6db47c49d817336))


### Features

* set targetSDK default value to 31 ([#824](https://github.com/ionic-team/capacitor-plugins/issues/824)) ([3ee10de](https://github.com/ionic-team/capacitor-plugins/commit/3ee10de98067984c1a4e75295d001c5a895c47f4))
* set targetSDK default value to 32 ([#970](https://github.com/ionic-team/capacitor-plugins/issues/970)) ([fa70d96](https://github.com/ionic-team/capacitor-plugins/commit/fa70d96f141af751aae53ceb5642c46b204f5958))
* Upgrade gradle to 7.4 ([#826](https://github.com/ionic-team/capacitor-plugins/issues/826)) ([5db0906](https://github.com/ionic-team/capacitor-plugins/commit/5db0906f6264287c4f8e69dbaecf19d4d387824b))
* Use java 11 ([#910](https://github.com/ionic-team/capacitor-plugins/issues/910)) ([5acb2a2](https://github.com/ionic-team/capacitor-plugins/commit/5acb2a288a413492b163e4e97da46a085d9e4be0))
* **android:** implements Activity Result API changes for permissions and activity results ([#222](https://github.com/ionic-team/capacitor-plugins/issues/222)) ([f671b9f](https://github.com/ionic-team/capacitor-plugins/commit/f671b9f4b472806ef43db6dcf302d4503cf1828c))
* **camera:** Add new method for multiple image picking from gallery ([#671](https://github.com/ionic-team/capacitor-plugins/issues/671)) ([a49c590](https://github.com/ionic-team/capacitor-plugins/commit/a49c5901683da12438fbafbd1bf6ae91133d18ed))
* **camera:** Return if image was saved to gallery ([#599](https://github.com/ionic-team/capacitor-plugins/issues/599)) ([594af3b](https://github.com/ionic-team/capacitor-plugins/commit/594af3be0982371e6c61e4bdb830c6bbb3963913))
* **camera:** Support for 1 Gallery app ([#791](https://github.com/ionic-team/capacitor-plugins/issues/791)) ([77e8c97](https://github.com/ionic-team/capacitor-plugins/commit/77e8c979394d5fb1804fc097ecaeee46a973e640))
* **camera:** Support for Samsung Gallery app on pickImages ([#706](https://github.com/ionic-team/capacitor-plugins/issues/706)) ([fd059fc](https://github.com/ionic-team/capacitor-plugins/commit/fd059fcd2e53661e95e230f684a6d32408db6787))
* **camera:** use a distinguishable permission denied string for camera and photos ([#379](https://github.com/ionic-team/capacitor-plugins/issues/379)) ([c71657f](https://github.com/ionic-team/capacitor-plugins/commit/c71657f7e14eae4efd4d2c7d00d77a7b329a7920))
* **camera:** Use same error messages for permission deny ([#404](https://github.com/ionic-team/capacitor-plugins/issues/404)) ([fffcd47](https://github.com/ionic-team/capacitor-plugins/commit/fffcd47f0237b6997bfa4ce430ef29392047ea0e))
* add commonjs output format ([#179](https://github.com/ionic-team/capacitor-plugins/issues/179)) ([8e9e098](https://github.com/ionic-team/capacitor-plugins/commit/8e9e09862064b3f6771d7facbc4008e995d9b463))
* Camera plugin ([#33](https://github.com/ionic-team/capacitor-plugins/issues/33)) ([4864928](https://github.com/ionic-team/capacitor-plugins/commit/48649288b1ba45e1901ad077b3b7b7314de04d4a))





## [1.3.1](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/camera@1.3.0...@capacitor/camera@1.3.1) (2022-03-03)


### Bug Fixes

* **camera:** Return the image on dismiss completion ([#849](https://github.com/ionic-team/capacitor-plugins/issues/849)) ([f083841](https://github.com/ionic-team/capacitor-plugins/commit/f0838416c6cf731aaae83fcb4986568357878b41))





# [1.3.0](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/camera@1.2.4...@capacitor/camera@1.3.0) (2022-02-10)


### Bug Fixes

* **camera:** process picked image only once ([#782](https://github.com/ionic-team/capacitor-plugins/issues/782)) ([897dcaf](https://github.com/ionic-team/capacitor-plugins/commit/897dcaf839a6cb83256485c32df2ca0e7b439124))
* **camera:** return single picture on pickImages ([#783](https://github.com/ionic-team/capacitor-plugins/issues/783)) ([9d65db1](https://github.com/ionic-team/capacitor-plugins/commit/9d65db1e74117fd1c1e7cd9bbba7efaeb4c13e0c))
* **camera:** Use Locale.ROOT on toUpperCase ([#812](https://github.com/ionic-team/capacitor-plugins/issues/812)) ([6d689ac](https://github.com/ionic-team/capacitor-plugins/commit/6d689acc48e3746ddd35bd5e1e8d7f239cb7f8df))


### Features

* **camera:** Support for 1 Gallery app ([#791](https://github.com/ionic-team/capacitor-plugins/issues/791)) ([77e8c97](https://github.com/ionic-team/capacitor-plugins/commit/77e8c979394d5fb1804fc097ecaeee46a973e640))
* **camera:** Support for Samsung Gallery app on pickImages ([#706](https://github.com/ionic-team/capacitor-plugins/issues/706)) ([fd059fc](https://github.com/ionic-team/capacitor-plugins/commit/fd059fcd2e53661e95e230f684a6d32408db6787))





## [1.2.4](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/camera@1.2.3...@capacitor/camera@1.2.4) (2022-01-19)


### Bug Fixes

* inline source code in esm map files ([#760](https://github.com/ionic-team/capacitor-plugins/issues/760)) ([a960489](https://github.com/ionic-team/capacitor-plugins/commit/a960489a19db0182b90d187a50deff9dfbe51038))





## [1.2.3](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/camera@1.2.2...@capacitor/camera@1.2.3) (2022-01-10)


### Bug Fixes

* **camera:** set camera direction for web ([#665](https://github.com/ionic-team/capacitor-plugins/issues/665)) ([4afedb9](https://github.com/ionic-team/capacitor-plugins/commit/4afedb96f3b745a86d9cacd33ca71c42ae3fb8d4))





## [1.2.2](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/camera@1.2.1...@capacitor/camera@1.2.2) (2021-12-08)


### Bug Fixes

* **camera:** Return proper exif when picking multiple images ([#712](https://github.com/ionic-team/capacitor-plugins/issues/712)) ([8451237](https://github.com/ionic-team/capacitor-plugins/commit/8451237e46f24c59e74e350eaa9b31e6d99a68a0))





## [1.2.1](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/camera@1.2.0...@capacitor/camera@1.2.1) (2021-11-17)


### Bug Fixes

* **camera:** Don't save gallery images on iOS 14+ ([#696](https://github.com/ionic-team/capacitor-plugins/issues/696)) ([7b2cc88](https://github.com/ionic-team/capacitor-plugins/commit/7b2cc88f6e83265c991ae9f81cfc3f6bed346250))
* **camera:** Remove capture attribute from multiple photo picker ([#687](https://github.com/ionic-team/capacitor-plugins/issues/687)) ([e551ef7](https://github.com/ionic-team/capacitor-plugins/commit/e551ef77eebe331cc7bf13c9c0eab5a0bd2da0d1))





# [1.2.0](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/camera@1.1.2...@capacitor/camera@1.2.0) (2021-11-03)


### Features

* **camera:** Add new method for multiple image picking from gallery ([#671](https://github.com/ionic-team/capacitor-plugins/issues/671)) ([a49c590](https://github.com/ionic-team/capacitor-plugins/commit/a49c5901683da12438fbafbd1bf6ae91133d18ed))





## [1.1.2](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/camera@1.1.1...@capacitor/camera@1.1.2) (2021-10-14)


### Bug Fixes

* remove postpublish scripts ([#656](https://github.com/ionic-team/capacitor-plugins/issues/656)) ([ed6ac49](https://github.com/ionic-team/capacitor-plugins/commit/ed6ac499ebf4a47525071ccbfc36c27503e11f60))





## [1.1.1](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/camera@1.1.0...@capacitor/camera@1.1.1) (2021-10-13)


### Bug Fixes

* correct addListeners links ([#655](https://github.com/ionic-team/capacitor-plugins/issues/655)) ([f9871e7](https://github.com/ionic-team/capacitor-plugins/commit/f9871e7bd53478addb21155e148829f550c0e457))





# [1.1.0](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/camera@1.0.5...@capacitor/camera@1.1.0) (2021-09-15)


### Bug Fixes

* **camera:** saveToGallery for edited images ([#602](https://github.com/ionic-team/capacitor-plugins/issues/602)) ([b5ac27d](https://github.com/ionic-team/capacitor-plugins/commit/b5ac27d59181ec3acc2909b2569d8ab45a829b1c))
* **camera:** set settings again on callbacks ([#595](https://github.com/ionic-team/capacitor-plugins/issues/595)) ([908bd68](https://github.com/ionic-team/capacitor-plugins/commit/908bd688767e374cf8e96b3def08bd33dcdfd2aa))


### Features

* **camera:** Return if image was saved to gallery ([#599](https://github.com/ionic-team/capacitor-plugins/issues/599)) ([594af3b](https://github.com/ionic-team/capacitor-plugins/commit/594af3be0982371e6c61e4bdb830c6bbb3963913))





## [1.0.5](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/camera@1.0.4...@capacitor/camera@1.0.5) (2021-09-01)


### Bug Fixes

* Correct missing source_files path ([#590](https://github.com/ionic-team/capacitor-plugins/issues/590)) ([24e0fc2](https://github.com/ionic-team/capacitor-plugins/commit/24e0fc27cc314049012ab9915fa5e7bfb03313e1))
* **camera:** cleanup camera images if not needed ([#563](https://github.com/ionic-team/capacitor-plugins/issues/563)) ([a2e4f43](https://github.com/ionic-team/capacitor-plugins/commit/a2e4f4339119698e8dd066a5f2f8f065ab2e4727))
* **camera:** Resize not respecting aspect ratio on iOS ([#568](https://github.com/ionic-team/capacitor-plugins/issues/568)) ([ea2b801](https://github.com/ionic-team/capacitor-plugins/commit/ea2b8012aab7e5ea34cfa34735f7f55ba76a3882))
* **camera:** return original image if editing is cancelled ([#566](https://github.com/ionic-team/capacitor-plugins/issues/566)) ([4786841](https://github.com/ionic-team/capacitor-plugins/commit/4786841099403a4d3d59aaf9103e8fa02aa8e4e2))





## [1.0.4](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/camera@1.0.3...@capacitor/camera@1.0.4) (2021-08-18)


### Bug Fixes

* **camera:** avoid error if image has no orientation ([#554](https://github.com/ionic-team/capacitor-plugins/issues/554)) ([dc8a55a](https://github.com/ionic-team/capacitor-plugins/commit/dc8a55a71cdaaf7ad86aee8470a0c7b8284653c4))
* **camera:** Make allowEdit work on all devices ([#552](https://github.com/ionic-team/capacitor-plugins/issues/552)) ([5224177](https://github.com/ionic-team/capacitor-plugins/commit/5224177f77bdce1c8f028e2cef41614fa687502f))
* **camera:** Properly reset orientation exif if corrected ([#545](https://github.com/ionic-team/capacitor-plugins/issues/545)) ([ad8c325](https://github.com/ionic-team/capacitor-plugins/commit/ad8c325af0a2459f5a7788be08a8da4118717671))





## [1.0.3](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/camera@1.0.2...@capacitor/camera@1.0.3) (2021-07-07)


### Bug Fixes

* **camera:** Reset exif orientation if corrected ([#510](https://github.com/ionic-team/capacitor-plugins/issues/510)) ([a65c05e](https://github.com/ionic-team/capacitor-plugins/commit/a65c05e0de8f53e7371c194047a75797d53879b5))
* **camera:** Return the full webPath ([#502](https://github.com/ionic-team/capacitor-plugins/issues/502)) ([e849732](https://github.com/ionic-team/capacitor-plugins/commit/e849732dbcf5e85d1df09835c53ff5738fbb4ded))





## [1.0.2](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/camera@1.0.1...@capacitor/camera@1.0.2) (2021-06-23)


### Bug Fixes

* **camera:** Append change listener only once ([#486](https://github.com/ionic-team/capacitor-plugins/issues/486)) ([5b7021e](https://github.com/ionic-team/capacitor-plugins/commit/5b7021e210649f8501a20ba6549903ecb6d42dcd))
* **camera:** Append exif to android images ([#480](https://github.com/ionic-team/capacitor-plugins/issues/480)) ([cad8a30](https://github.com/ionic-team/capacitor-plugins/commit/cad8a30c562202fb819a4d260d5307f1b6b8fa44))
* **camera:** correct photo resizing on iOS ([#460](https://github.com/ionic-team/capacitor-plugins/issues/460)) ([bc56e03](https://github.com/ionic-team/capacitor-plugins/commit/bc56e034c711b172a7ff503cabd2970adbc14b86))
* **camera:** Make input file hidden ([#484](https://github.com/ionic-team/capacitor-plugins/issues/484)) ([cdc1835](https://github.com/ionic-team/capacitor-plugins/commit/cdc1835f3bbfb8db8e18fccace6103d83dd9edaa))
* **camera:** Make web use source options ([#487](https://github.com/ionic-team/capacitor-plugins/issues/487)) ([7870e6b](https://github.com/ionic-team/capacitor-plugins/commit/7870e6b6ca196265640fc0ba3c1f52ddca075607))





## [1.0.1](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/camera@1.0.0...@capacitor/camera@1.0.1) (2021-06-09)

**Note:** Version bump only for package @capacitor/camera





# [1.0.0](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/camera@0.4.7...@capacitor/camera@1.0.0) (2021-05-19)


### Bug Fixes

* **camera:** decode content uri when retrieving image from gallery ([#277](https://github.com/ionic-team/capacitor-plugins/issues/277)) ([a6cd1ad](https://github.com/ionic-team/capacitor-plugins/commit/a6cd1adc241bf21e4f7f06d24c0db4a4d7382dbc))
* **camera:** Remove unused saveCall ([#401](https://github.com/ionic-team/capacitor-plugins/issues/401)) ([95920da](https://github.com/ionic-team/capacitor-plugins/commit/95920da4d1844ed76a162651d5492a22a4038d26))


### Features

* **camera:** use a distinguishable permission denied string for camera and photos ([#379](https://github.com/ionic-team/capacitor-plugins/issues/379)) ([c71657f](https://github.com/ionic-team/capacitor-plugins/commit/c71657f7e14eae4efd4d2c7d00d77a7b329a7920))
* **camera:** Use same error messages for permission deny ([#404](https://github.com/ionic-team/capacitor-plugins/issues/404)) ([fffcd47](https://github.com/ionic-team/capacitor-plugins/commit/fffcd47f0237b6997bfa4ce430ef29392047ea0e))





## [0.4.7](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/camera@0.4.6...@capacitor/camera@0.4.7) (2021-05-11)

**Note:** Version bump only for package @capacitor/camera





## [0.4.6](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/camera@0.4.5...@capacitor/camera@0.4.6) (2021-05-10)

**Note:** Version bump only for package @capacitor/camera





## [0.4.5](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/camera@0.4.4...@capacitor/camera@0.4.5) (2021-05-07)

**Note:** Version bump only for package @capacitor/camera





## [0.4.4](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/camera@0.4.3...@capacitor/camera@0.4.4) (2021-04-29)

**Note:** Version bump only for package @capacitor/camera





## [0.4.3](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/camera@0.4.2...@capacitor/camera@0.4.3) (2021-03-10)

**Note:** Version bump only for package @capacitor/camera





## [0.4.2](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/camera@0.4.1...@capacitor/camera@0.4.2) (2021-03-02)

**Note:** Version bump only for package @capacitor/camera





## [0.4.1](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/camera@0.4.0...@capacitor/camera@0.4.1) (2021-02-27)

**Note:** Version bump only for package @capacitor/camera





# [0.4.0](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/camera@0.3.2...@capacitor/camera@0.4.0) (2021-02-10)


### Features

* **android:** implements Activity Result API changes for permissions and activity results ([#222](https://github.com/ionic-team/capacitor-plugins/issues/222)) ([f671b9f](https://github.com/ionic-team/capacitor-plugins/commit/f671b9f4b472806ef43db6dcf302d4503cf1828c))





## [0.3.2](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/camera@0.3.1...@capacitor/camera@0.3.2) (2021-02-05)

**Note:** Version bump only for package @capacitor/camera





## [0.3.1](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/camera@0.3.0...@capacitor/camera@0.3.1) (2021-01-26)

**Note:** Version bump only for package @capacitor/camera





# [0.3.0](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/camera@0.2.0...@capacitor/camera@0.3.0) (2021-01-14)

**Note:** Version bump only for package @capacitor/camera





# [0.2.0](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/camera@0.1.4...@capacitor/camera@0.2.0) (2021-01-13)


### Bug Fixes

* add es2017 lib to tsconfig ([#180](https://github.com/ionic-team/capacitor-plugins/issues/180)) ([2c3776c](https://github.com/ionic-team/capacitor-plugins/commit/2c3776c38ca025c5ee965dec10ccf1cdb6c02e2f))


### Features

* add commonjs output format ([#179](https://github.com/ionic-team/capacitor-plugins/issues/179)) ([8e9e098](https://github.com/ionic-team/capacitor-plugins/commit/8e9e09862064b3f6771d7facbc4008e995d9b463))





## [0.1.4](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/camera@0.1.3...@capacitor/camera@0.1.4) (2021-01-13)

**Note:** Version bump only for package @capacitor/camera





## [0.1.3](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/camera@0.1.2...@capacitor/camera@0.1.3) (2021-01-08)


### Bug Fixes

* **camera:** return file URL for path, not system path ([#170](https://github.com/ionic-team/capacitor-plugins/issues/170)) ([8a9e5c3](https://github.com/ionic-team/capacitor-plugins/commit/8a9e5c3dba3b232a1cca9f9a1e9b4520022abc09))





## [0.1.2](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/camera@0.1.1...@capacitor/camera@0.1.2) (2020-12-28)


### Bug Fixes

* **camera:** fix camera source on Android ([#164](https://github.com/ionic-team/capacitor-plugins/issues/164)) ([e67f7c6](https://github.com/ionic-team/capacitor-plugins/commit/e67f7c6b06b20d7c3e8f0925c40fd75d23d9d717))





## [0.1.1](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/camera@0.1.0...@capacitor/camera@0.1.1) (2020-12-27)


### Bug Fixes

* **camera:** query IMAGE_CAPTURE intent required by SDK 30 ([#160](https://github.com/ionic-team/capacitor-plugins/issues/160)) ([6484991](https://github.com/ionic-team/capacitor-plugins/commit/6484991d76d57bac0cbc82b9f050e146ec4732da))





# 0.1.0 (2020-12-20)


### Bug Fixes

* support deprecated types from Capacitor 2 ([#139](https://github.com/ionic-team/capacitor-plugins/issues/139)) ([2d7127a](https://github.com/ionic-team/capacitor-plugins/commit/2d7127a488e26f0287951921a6db47c49d817336))


### Features

* Camera plugin ([#33](https://github.com/ionic-team/capacitor-plugins/issues/33)) ([4864928](https://github.com/ionic-team/capacitor-plugins/commit/48649288b1ba45e1901ad077b3b7b7314de04d4a))
