# @capacitor/splash-screen

The Splash Screen API provides methods for showing or hiding a Splash image.

## Install

```bash
npm install @capacitor/splash-screen
npx cap sync
```

## Example

```typescript
import { SplashScreen } from '@capacitor/splash-screen';

// Hide the splash (you should do this on app launch)
await SplashScreen.hide();

// Show the splash for an indefinite amount of time:
await SplashScreen.show({
  autoHide: false,
});

// Show the splash for two seconds and then automatically hide it:
await SplashScreen.show({
  showDuration: 2000,
  autoHide: true,
});
```

## Hiding the Splash Screen

By default, the Splash Screen is set to automatically hide after 500 ms.

If you want to be sure the splash screen never disappears before your app is ready, set `launchAutoHide` to `false`; the splash screen will then stay visible until manually hidden. For the best user experience, your app should call `hide()` as soon as possible.

If, instead, you want to show the splash screen for a fixed amount of time, set `launchShowDuration` in your [Capacitor configuration file](https://capacitorjs.com/docs/config).

## Background Color

In certain conditions, especially if the splash screen does not fully cover the device screen, it might happen that the app screen is visible around the corners (due to transparency). Instead of showing a transparent color, you can set a `backgroundColor` to cover those areas.

Possible values for `backgroundColor` are either `#RRGGBB` or `#RRGGBBAA`.

## Spinner

If you want to show a spinner on top of the splash screen, set `showSpinner` to `true` in your [Capacitor configuration file](https://capacitorjs.com/docs/config).

You can customize the appearance of the spinner with the following configuration.

For Android, `androidSpinnerStyle` has the following options:

- `horizontal`
- `small`
- `large` (default)
- `inverse`
- `smallInverse`
- `largeInverse`

For iOS, `iosSpinnerStyle` has the following options:

- `large` (default)
- `small`

To set the color of the spinner use `spinnerColor`, values are either `#RRGGBB` or `#RRGGBBAA`.

## Configuration

<docgen-config>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

These config values are available:

| Prop                            | Type                                                                                                                          | Description                                                                                                                                                                                                                                             | Default             | Since |
| ------------------------------- | ----------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------- | ----- |
| **`launchShowDuration`**        | <code>number</code>                                                                                                           | How long to show the launch splash screen when autoHide is enabled (in ms)                                                                                                                                                                              | <code>500</code>    | 1.0.0 |
| **`launchAutoHide`**            | <code>boolean</code>                                                                                                          | Whether to auto hide the splash after launchShowDuration.                                                                                                                                                                                               | <code>true</code>   | 1.0.0 |
| **`backgroundColor`**           | <code>string</code>                                                                                                           | Color of the background of the Splash Screen in hex format, #RRGGBB or #RRGGBBAA. Doesn't work if `useDialog` is true or on launch when using the Android 12 API.                                                                                       |                     | 1.0.0 |
| **`androidSplashResourceName`** | <code>string</code>                                                                                                           | Name of the resource to be used as Splash Screen. Doesn't work on launch when using the Android 12 API. Only available on Android.                                                                                                                      | <code>splash</code> | 1.0.0 |
| **`androidScaleType`**          | <code>'CENTER' \| 'CENTER_CROP' \| 'CENTER_INSIDE' \| 'FIT_CENTER' \| 'FIT_END' \| 'FIT_START' \| 'FIT_XY' \| 'MATRIX'</code> | The [ImageView.ScaleType](https://developer.android.com/reference/android/widget/ImageView.ScaleType) used to scale the Splash Screen image. Doesn't work if `useDialog` is true or on launch when using the Android 12 API. Only available on Android. | <code>FIT_XY</code> | 1.0.0 |
| **`showSpinner`**               | <code>boolean</code>                                                                                                          | Show a loading spinner on the Splash Screen. Doesn't work if `useDialog` is true or on launch when using the Android 12 API.                                                                                                                            |                     | 1.0.0 |
| **`androidSpinnerStyle`**       | <code>'horizontal' \| 'small' \| 'large' \| 'inverse' \| 'smallInverse' \| 'largeInverse'</code>                              | Style of the Android spinner. Doesn't work if `useDialog` is true or on launch when using the Android 12 API.                                                                                                                                           | <code>large</code>  | 1.0.0 |
| **`iosSpinnerStyle`**           | <code>'small' \| 'large'</code>                                                                                               | Style of the iOS spinner. Doesn't work if `useDialog` is true. Only available on iOS.                                                                                                                                                                   | <code>large</code>  | 1.0.0 |
| **`spinnerColor`**              | <code>string</code>                                                                                                           | Color of the spinner in hex format, #RRGGBB or #RRGGBBAA. Doesn't work if `useDialog` is true or on launch when using the Android 12 API.                                                                                                               |                     | 1.0.0 |
| **`splashFullScreen`**          | <code>boolean</code>                                                                                                          | Hide the status bar on the Splash Screen. Doesn't work on launch when using the Android 12 API. Only available on Android.                                                                                                                              |                     | 1.0.0 |
| **`splashImmersive`**           | <code>boolean</code>                                                                                                          | Hide the status bar and the software navigation buttons on the Splash Screen. Doesn't work on launch when using the Android 12 API. Only available on Android.                                                                                          |                     | 1.0.0 |
| **`layoutName`**                | <code>string</code>                                                                                                           | If `useDialog` is set to true, configure the Dialog layout. If `useDialog` is not set or false, use a layout instead of the ImageView. Doesn't work on launch when using the Android 12 API. Only available on Android.                                 |                     | 1.1.0 |
| **`useDialog`**                 | <code>boolean</code>                                                                                                          | Use a Dialog instead of an ImageView. If `layoutName` is not configured, it will use a layout that uses the splash image as background. Doesn't work on launch when using the Android 12 API. Only available on Android.                                |                     | 1.1.0 |

### Examples

In `capacitor.config.json`:

```json
{
  "plugins": {
    "SplashScreen": {
      "launchShowDuration": 3000,
      "launchAutoHide": true,
      "backgroundColor": "#ffffffff",
      "androidSplashResourceName": "splash",
      "androidScaleType": "CENTER_CROP",
      "showSpinner": true,
      "androidSpinnerStyle": "large",
      "iosSpinnerStyle": "small",
      "spinnerColor": "#999999",
      "splashFullScreen": true,
      "splashImmersive": true,
      "layoutName": "launch_screen",
      "useDialog": true
    }
  }
}
```

In `capacitor.config.ts`:

```ts
/// <reference types="@capacitor/splash-screen" />

import { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  plugins: {
    SplashScreen: {
      launchShowDuration: 3000,
      launchAutoHide: true,
      backgroundColor: "#ffffffff",
      androidSplashResourceName: "splash",
      androidScaleType: "CENTER_CROP",
      showSpinner: true,
      androidSpinnerStyle: "large",
      iosSpinnerStyle: "small",
      spinnerColor: "#999999",
      splashFullScreen: true,
      splashImmersive: true,
      layoutName: "launch_screen",
      useDialog: true,
    },
  },
};

export default config;
```

</docgen-config>

### Android

To use splash screen images named something other than `splash.png`, set `androidSplashResourceName` to the new resource name. Additionally, in `android/app/src/main/res/values/styles.xml`, change the resource name in the following block:

```xml
<style name="AppTheme.NoActionBarLaunch" parent="AppTheme.NoActionBar">
    <item name="android:background">@drawable/NAME</item>
</style>
```

### Android 12 Splash Screen API

_**This only affects the launch splash screen and is not used when utilizing the programmatic `show()` method.**_

To enable the new recommended **[Android 12 Splash Screen API](https://developer.android.com/guide/topics/ui/splash-screen)** in SDK Versions 30 or below:

_This is enabled by default and required for SDK version 31+._

- Add `$coreSplashScreenVersion` to the `variables.gradle` file.
  _See Variables Section for more information_

- Add `implementation "androidx.core:core-splashscreen:$coreSplashScreenVersion"` in the dependencies section of `/android/app/build.gradle`.

- In `android/app/src/main/res/values/styles.xml`, edit the theme `parent` attribute on the Applications's MainActivity Theme to `Theme.SplashScreen` and add desired options to the theme.

```xml
<style name="AppTheme.NoActionBarLaunch" parent="Theme.SplashScreen">
    <item name="android:background">@drawable/splash</item>
</style>
```

**NOTE**: Some issues may exist on SDK >= 31 when using the new API for the splash screen, which pertain to the splash screen only showing when launched from the launcher icon.
**[Google Issue Tracker](https://issuetracker.google.com/issues/205021357)**

## Variables

This plugin will use the following project variables (defined in your app's `variables.gradle` file):

`$coreSplashScreenVersion` version of `androidx.core:core-splashscreen:$coreSplashScreenVersion` (default: `1.0.0-rc01`)

## Example Guides

[Adding Your Own Icons and Splash Screen Images &#8250;](https://www.joshmorony.com/adding-icons-splash-screens-launch-images-to-capacitor-projects/)

[Creating a Dynamic/Adaptable Splash Screen for Capacitor (Android) &#8250;](https://www.joshmorony.com/creating-a-dynamic-universal-splash-screen-for-capacitor-android/)

## API

<docgen-index>

* [`show(...)`](#show)
* [`hide(...)`](#hide)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### show(...)

```typescript
show(options?: ShowOptions | undefined) => Promise<void>
```

Show the splash screen

| Param         | Type                                                |
| ------------- | --------------------------------------------------- |
| **`options`** | <code><a href="#showoptions">ShowOptions</a></code> |

**Since:** 1.0.0

--------------------


### hide(...)

```typescript
hide(options?: HideOptions | undefined) => Promise<void>
```

Hide the splash screen

| Param         | Type                                                |
| ------------- | --------------------------------------------------- |
| **`options`** | <code><a href="#hideoptions">HideOptions</a></code> |

**Since:** 1.0.0

--------------------


### Interfaces


#### ShowOptions

| Prop                  | Type                 | Description                                                         | Default           | Since |
| --------------------- | -------------------- | ------------------------------------------------------------------- | ----------------- | ----- |
| **`autoHide`**        | <code>boolean</code> | Whether to auto hide the splash after showDuration                  |                   | 1.0.0 |
| **`fadeInDuration`**  | <code>number</code>  | How long (in ms) to fade in.                                        | <code>200</code>  | 1.0.0 |
| **`fadeOutDuration`** | <code>number</code>  | How long (in ms) to fade out.                                       | <code>200</code>  | 1.0.0 |
| **`showDuration`**    | <code>number</code>  | How long to show the splash screen when autoHide is enabled (in ms) | <code>3000</code> | 1.0.0 |


#### HideOptions

| Prop                  | Type                | Description                   | Default          | Since |
| --------------------- | ------------------- | ----------------------------- | ---------------- | ----- |
| **`fadeOutDuration`** | <code>number</code> | How long (in ms) to fade out. | <code>200</code> | 1.0.0 |

</docgen-api>
