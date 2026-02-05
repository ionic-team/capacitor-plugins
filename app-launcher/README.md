# @capacitor/app-launcher

The AppLauncher API allows your app to check if an app can be opened and open it.

On iOS you can only open apps if you know their url scheme.

On Android you can open apps if you know their url scheme or use their public package name.

**Note:** On [Android 11](https://developer.android.com/about/versions/11/privacy/package-visibility) and newer you have to add the app package names or url schemes you want to query in the `AndroidManifest.xml` inside the `queries` tag.

Example:
```xml
<queries>
  <!-- Query by package name -->
  <package android:name="com.twitter.android" />
  <!-- Query by url scheme -->
  <intent>
      <action android:name="android.intent.action.VIEW"/>
      <data android:scheme="twitter"/>
  </intent>
</queries>
```

## Install

```bash
npm install @capacitor/app-launcher
npx cap sync
```

## Example

```typescript
import { AppLauncher } from '@capacitor/app-launcher';

const checkCanOpenTwitterUrl = async () => {
  const { value } = await AppLauncher.canOpenUrl({ url: 'twitter://timeline' });
  console.log('Can open url: ', value);
};

const openTwitterUrl = async () => {
  const { completed } = await AppLauncher.openUrl({ url: 'twitter://timeline' });
  console.log('openUrl completed: ', completed);
};

// Android only
const checkCanOpenTwitterPackage = async () => {
  const { value } = await AppLauncher.canOpenUrl({ url: 'com.twitter.android' });
  console.log('Can open package: ', value);
};

// Android only
const openTwitterPackage = async () => {
  const { completed } = await AppLauncher.openUrl({ url: 'com.twitter.android' });
  console.log('openUrl package completed: ', completed);
};
```

## API

<docgen-index>

* [`canOpenUrl(...)`](#canopenurl)
* [`openUrl(...)`](#openurl)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### canOpenUrl(...)

```typescript
canOpenUrl(options: CanOpenURLOptions) => Promise<CanOpenURLResult>
```

Check if an app can be opened with the given URL.

On iOS you must declare the URL schemes you pass to this method by adding
the `LSApplicationQueriesSchemes` key to your app's `Info.plist` file.
Learn more about configuring
[`Info.plist`](https://capacitorjs.com/docs/ios/configuration#configuring-infoplist).

This method always returns false for undeclared schemes, whether or not an
appropriate app is installed. To learn more about the key, see
[LSApplicationQueriesSchemes](https://developer.apple.com/library/archive/documentation/General/Reference/InfoPlistKeyReference/Articles/LaunchServicesKeys.html#//apple_ref/doc/plist/info/LSApplicationQueriesSchemes).

On Android the URL can be a known URLScheme or an app package name.

On [Android 11](https://developer.android.com/about/versions/11/privacy/package-visibility)
and newer you have to add the app package names or url schemes you want to query in the `AndroidManifest.xml`
inside the `queries` tag.

| Param         | Type                                                            |
| ------------- | --------------------------------------------------------------- |
| **`options`** | <code><a href="#canopenurloptions">CanOpenURLOptions</a></code> |

**Returns:** <code>Promise&lt;<a href="#canopenurlresult">CanOpenURLResult</a>&gt;</code>

**Since:** 1.0.0

--------------------


### openUrl(...)

```typescript
openUrl(options: OpenURLOptions) => Promise<OpenURLResult>
```

Open an app with the given URL.
On iOS the URL should be a known URLScheme.
On Android the URL can be a known URLScheme or an app package name.

| Param         | Type                                                      |
| ------------- | --------------------------------------------------------- |
| **`options`** | <code><a href="#openurloptions">OpenURLOptions</a></code> |

**Returns:** <code>Promise&lt;<a href="#openurlresult">OpenURLResult</a>&gt;</code>

**Since:** 1.0.0

--------------------


### Interfaces


#### CanOpenURLResult

| Prop        | Type                 |
| ----------- | -------------------- |
| **`value`** | <code>boolean</code> |


#### CanOpenURLOptions

| Prop      | Type                |
| --------- | ------------------- |
| **`url`** | <code>string</code> |


#### OpenURLResult

| Prop            | Type                 |
| --------------- | -------------------- |
| **`completed`** | <code>boolean</code> |


#### OpenURLOptions

| Prop      | Type                |
| --------- | ------------------- |
| **`url`** | <code>string</code> |

</docgen-api>
