# @capacitor/share

The Share API provides methods for sharing content in any sharing-enabled apps the user may have installed.

The Share API works on iOS, Android, and the Web (using the new [Web Share
API](https://web.dev/web-share/)), though web support is currently spotty.

## Install

```bash
npm install @capacitor/share
npx cap sync
```
## Android

By default, Capacitor apps only allow to share files from caches folder. To make other Android folders shareable, they have to be added in `android/app/src/main/res/xml/file_paths.xml` file. Check the Specifying Available Files section in [FileProvider docs](https://developer.android.com/reference/androidx/core/content/FileProvider) for the available locations.

## Example

```typescript
import { Share } from '@capacitor/share';

await Share.share({
  title: 'See cool stuff',
  text: 'Really awesome thing you need to see right meow',
  url: 'http://ionicframework.com/',
  dialogTitle: 'Share with buddies',
});

// Share text only
await Share.share({
  text: 'Really awesome thing you need to see right meow',
});

// Share url only
await Share.share({
  url: 'http://ionicframework.com/',
});

// Share local file using url parameter
const photo = await Camera.getPhoto(options);
await Share.share({
  url: photo.path,
});

// Share multiple files using files parameter
const { photos } = await Camera.pickImages(options);
await Share.share({
  files: photos.map(photo => photo.path!),
});
```

Each platform uses a different set of fields, but you should supply them all.

## API

<docgen-index>

* [`canShare()`](#canshare)
* [`share(...)`](#share)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### canShare()

```typescript
canShare() => Promise<CanShareResult>
```

Check if sharing is supported.

**Returns:** <code>Promise&lt;<a href="#canshareresult">CanShareResult</a>&gt;</code>

**Since:** 1.1.0

--------------------


### share(...)

```typescript
share(options: ShareOptions) => Promise<ShareResult>
```

Show a Share modal for sharing content with other apps

| Param         | Type                                                  |
| ------------- | ----------------------------------------------------- |
| **`options`** | <code><a href="#shareoptions">ShareOptions</a></code> |

**Returns:** <code>Promise&lt;<a href="#shareresult">ShareResult</a>&gt;</code>

**Since:** 1.0.0

--------------------


### Interfaces


#### CanShareResult

| Prop        | Type                 | Description                          | Since |
| ----------- | -------------------- | ------------------------------------ | ----- |
| **`value`** | <code>boolean</code> | Whether sharing is supported or not. | 1.1.0 |


#### ShareResult

| Prop               | Type                | Description                                                                                                              | Since |
| ------------------ | ------------------- | ------------------------------------------------------------------------------------------------------------------------ | ----- |
| **`activityType`** | <code>string</code> | Identifier of the app that received the share action. Can be an empty string in some cases. On web it will be undefined. | 1.0.0 |


#### ShareOptions

| Prop              | Type                  | Description                                                                         | Since |
| ----------------- | --------------------- | ----------------------------------------------------------------------------------- | ----- |
| **`title`**       | <code>string</code>   | Set a title for any message. This will be the subject if sharing to email           | 1.0.0 |
| **`text`**        | <code>string</code>   | Set some text to share                                                              | 1.0.0 |
| **`url`**         | <code>string</code>   | Set a URL to share, can be http, https or file:// URL                               | 1.0.0 |
| **`files`**       | <code>string[]</code> | Array of file:// URLs of the files to be shared. Only supported on iOS and Android. | 4.1.0 |
| **`dialogTitle`** | <code>string</code>   | Set a title for the share modal. This option is only supported on Android.          | 1.0.0 |

</docgen-api>
