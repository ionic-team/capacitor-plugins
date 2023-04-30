# Change Log

All notable changes to this project will be documented in this file.
See [Conventional Commits](https://conventionalcommits.org) for commit guidelines.

# [5.0.0-beta.1](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/google-maps@5.0.0-beta.0...@capacitor/google-maps@5.0.0-beta.1) (2023-04-21)


### Bug Fixes

* **google-maps:** Cast iOS marker id to string ([#1222](https://github.com/ionic-team/capacitor-plugins/issues/1222)) ([0ffb621](https://github.com/ionic-team/capacitor-plugins/commit/0ffb62159bbdb38cf3657ca15f7e69573d83a792))
* **google-maps:** fix custom icon on marker when clustering is enabled ([#1522](https://github.com/ionic-team/capacitor-plugins/issues/1522)) ([12be06a](https://github.com/ionic-team/capacitor-plugins/commit/12be06a1de1adc2e961cdd6837f203c19343ad8d))


### Features

* **google-maps:** Circles support ([#1553](https://github.com/ionic-team/capacitor-plugins/issues/1553)) ([c39d84f](https://github.com/ionic-team/capacitor-plugins/commit/c39d84fbae464fc8984e26f970e85fd1862ff2ac))
* **google-maps:** Polygons support ([#1534](https://github.com/ionic-team/capacitor-plugins/issues/1534)) ([e2fd153](https://github.com/ionic-team/capacitor-plugins/commit/e2fd15318d0ba9a07386d0389d985ec65071341e))
* **google-maps:** Polylines support ([#1532](https://github.com/ionic-team/capacitor-plugins/issues/1532)) ([415f42b](https://github.com/ionic-team/capacitor-plugins/commit/415f42b351d4ac8fd555efc3d6b0dfa9b09a80b9))
* Update gradle to 8.0.2 and gradle plugin to 8.0.0 ([#1542](https://github.com/ionic-team/capacitor-plugins/issues/1542)) ([e7210b4](https://github.com/ionic-team/capacitor-plugins/commit/e7210b47867644f5983e37acdbf0247214ec232d))





# [5.0.0-beta.0](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/google-maps@5.0.0-alpha.1...@capacitor/google-maps@5.0.0-beta.0) (2023-03-31)


### Bug Fixes

* **google-maps:** setMapType not working on web ([#1508](https://github.com/ionic-team/capacitor-plugins/issues/1508)) ([4efa7d7](https://github.com/ionic-team/capacitor-plugins/commit/4efa7d7ccc0a48f61131bb8f729de13ac7b00fb3))


### Features

* **google-maps:** add extend to LatLngBounds ([fb2dc0d](https://github.com/ionic-team/capacitor-plugins/commit/fb2dc0d42f9eaa928e9bdb38980ac9b84ae4c4b7))





# [5.0.0-alpha.1](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/google-maps@4.5.0...@capacitor/google-maps@5.0.0-alpha.1) (2023-03-16)


### Features

* **android:** Removing enableJetifier ([d66f9cb](https://github.com/ionic-team/capacitor-plugins/commit/d66f9cbd9da7e3b1d8c64ca6a5b45156867d4a04))
* **google-maps:** add zIndex to markers ([53a0436](https://github.com/ionic-team/capacitor-plugins/commit/53a04366ad331ada081589aef9c726b4356688df))





# [4.5.0](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/google-maps@4.4.0...@capacitor/google-maps@4.5.0) (2023-02-22)


### Bug Fixes

* **google-maps/android:** dispatch touch events to correct y position ([#1415](https://github.com/ionic-team/capacitor-plugins/issues/1415)) ([87e9a6e](https://github.com/ionic-team/capacitor-plugins/commit/87e9a6e97f843228c62245a80eeb6cb86d43500e))


### Features

* **google-maps:** add contains to LatLngBounds ([a6b9962](https://github.com/ionic-team/capacitor-plugins/commit/a6b9962f1b6d6c2f873dd8c373b7aa16a49eff6d))
* **google-maps:** Add getMapType method ([#1420](https://github.com/ionic-team/capacitor-plugins/issues/1420)) ([c2fa96f](https://github.com/ionic-team/capacitor-plugins/commit/c2fa96fc709b59fa55a6f61ecb4d80be214982c5))





# [4.4.0](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/google-maps@4.3.2...@capacitor/google-maps@4.4.0) (2023-02-03)


### Bug Fixes

* **google-maps/android:** update clusters on map move ([#1398](https://github.com/ionic-team/capacitor-plugins/issues/1398)) ([3a23238](https://github.com/ionic-team/capacitor-plugins/commit/3a2323833779d9b545140057070f5acda38d9b32))


### Features

* **google-maps:** Add minClusterSize setting for enableClustering ([#1399](https://github.com/ionic-team/capacitor-plugins/issues/1399)) ([fba34b8](https://github.com/ionic-team/capacitor-plugins/commit/fba34b823e60aba43f82761bf7c3b42675702b7f))





## [4.3.2](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/google-maps@4.3.1...@capacitor/google-maps@4.3.2) (2022-12-01)

**Note:** Version bump only for package @capacitor/google-maps





## [4.3.1](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/google-maps@4.3.0...@capacitor/google-maps@4.3.1) (2022-11-16)


### Bug Fixes

* **google-maps:** install google.maps types as dependency ([#1269](https://github.com/ionic-team/capacitor-plugins/issues/1269)) ([6cc7783](https://github.com/ionic-team/capacitor-plugins/commit/6cc77835d301f9c7734ae8a25dd0fc08ea91c74a))
* **google-maps:** Remove log of marker options ([#1247](https://github.com/ionic-team/capacitor-plugins/issues/1247)) ([5bb3000](https://github.com/ionic-team/capacitor-plugins/commit/5bb30005df075ce1679695792e33f398eee298c4))
* **google-maps:** Retry getting the map size if it's 0 ([#1252](https://github.com/ionic-team/capacitor-plugins/issues/1252)) ([9bffdb5](https://github.com/ionic-team/capacitor-plugins/commit/9bffdb529f41cadc2e5a33c4a09d5ebfacf4baed))





# [4.3.0](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/google-maps@4.2.1...@capacitor/google-maps@4.3.0) (2022-10-21)


### Bug Fixes

* **google-maps:** allow remote marker icons on native platforms ([#1216](https://github.com/ionic-team/capacitor-plugins/issues/1216)) ([8b4de64](https://github.com/ionic-team/capacitor-plugins/commit/8b4de6435decf5d37c29ac6d543191f32b5a6c6e))
* **google-maps:** allow to resize icons with same image ([#1228](https://github.com/ionic-team/capacitor-plugins/issues/1228)) ([f6522d3](https://github.com/ionic-team/capacitor-plugins/commit/f6522d37c1418933207ee9a54d08222fcf264334))
* **google-maps:** event listeners not firing when clustering enabled ([dd80da2](https://github.com/ionic-team/capacitor-plugins/commit/dd80da263ba684463fa35c9cff3a237ffd257058))


### Features

* **google-maps:** Add more config options ([#1233](https://github.com/ionic-team/capacitor-plugins/issues/1233)) ([8bebf97](https://github.com/ionic-team/capacitor-plugins/commit/8bebf9745f6538d4bc7f1622b2b4a0317f306bfb))





## [4.2.1](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/google-maps@4.2.0...@capacitor/google-maps@4.2.1) (2022-09-29)


### Bug Fixes

* **google-maps:** adding missing marker draggable property for web ([#1184](https://github.com/ionic-team/capacitor-plugins/issues/1184)) ([2eb0e7e](https://github.com/ionic-team/capacitor-plugins/commit/2eb0e7e6db9d3f6a684d2c9a0e2e7ec94d5dfa63))
* **google-maps:** onClusterClickHandler not getting marker ids on web ([#1039](https://github.com/ionic-team/capacitor-plugins/issues/1039)) ([b04e32d](https://github.com/ionic-team/capacitor-plugins/commit/b04e32d6fcab39285ba6dbe49408e001177cf13f))





# [4.2.0](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/google-maps@4.1.0...@capacitor/google-maps@4.2.0) (2022-09-12)


### Features

* **google-maps:** Marker Customization Options ([#1146](https://github.com/ionic-team/capacitor-plugins/issues/1146)) ([bb77432](https://github.com/ionic-team/capacitor-plugins/commit/bb77432ac28ec5de5c5d2584f4f2ccf874e5c197))
* **google-maps:** Marker Drag Listeners ([833f28d](https://github.com/ionic-team/capacitor-plugins/commit/833f28dc8e28553673c861619a2ac9540f39e33a))





# [4.1.0](https://github.com/ionic-team/capacitor-plugins/compare/@capacitor/google-maps@1.1.0...@capacitor/google-maps@4.1.0) (2022-08-24)


### Bug Fixes

* **google-maps:** Check for WKScrollView and WKChildScrollView ([#1109](https://github.com/ionic-team/capacitor-plugins/issues/1109)) ([7513602](https://github.com/ionic-team/capacitor-plugins/commit/7513602c6c9830de305e6097db3023e42a8afa30))





## [4.0.1](https://github.com/ionic-team/capacitor-plugins/compare/4.0.0...4.0.1) (2022-07-28)

**Note:** Version bump only for package @capacitor/google-maps





# [4.0.0](https://github.com/ionic-team/capacitor-plugins/compare/4.0.0-beta.2...4.0.0) (2022-07-27)

**Note:** Version bump only for package @capacitor/google-maps





# [4.0.0-beta.2](https://github.com/ionic-team/capacitor-plugins/compare/4.0.0-beta.0...4.0.0-beta.2) (2022-07-08)


### Features

* **google-maps:** provides variables for configuring dependencies ([#1063](https://github.com/ionic-team/capacitor-plugins/issues/1063)) ([5c077f1](https://github.com/ionic-team/capacitor-plugins/commit/5c077f199cbd16b459a77061509e0504029f78db))





# 4.0.0-beta.0 (2022-06-27)


### Bug Fixes

* **google-maps:** correctly typed event listeners ([656f916](https://github.com/ionic-team/capacitor-plugins/commit/656f9169ccd8d7fa880143b13ca5f62bb546edb0))
* **google-maps:** Fixing map touch events on Android ([7cb89fb](https://github.com/ionic-team/capacitor-plugins/commit/7cb89fb788e05aa9e90c39698e041ebe094132ea))


### Features

* **google-maps:** Google Maps Bounds ([14a045d](https://github.com/ionic-team/capacitor-plugins/commit/14a045d3880124996b2770cec7b3e28a9f13d231))





# 1.1.0 (2022-06-17)


### Bug Fixes

* **google-maps:** correctly typed event listeners ([656f916](https://github.com/ionic-team/capacitor-plugins/commit/656f9169ccd8d7fa880143b13ca5f62bb546edb0))
* **google-maps:** Fixing map touch events on Android ([7cb89fb](https://github.com/ionic-team/capacitor-plugins/commit/7cb89fb788e05aa9e90c39698e041ebe094132ea))


### Features

* **google-maps:** Google Maps Bounds ([14a045d](https://github.com/ionic-team/capacitor-plugins/commit/14a045d3880124996b2770cec7b3e28a9f13d231))






# [1.0.0](https://github.com/ionic-team/capacitor-plugins/tree/main/google-maps) (2022-04-28)

**Note:** Version bump only for package @capacitor/google-maps
