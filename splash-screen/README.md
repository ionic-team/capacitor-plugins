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
  autoHide: false
});

// Show the splash for two seconds and then automatically hide it:
await SplashScreen.show({
  showDuration: 2000,
  autoHide: true
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

## Animated Splash Screen
### iOS
If you want to have an animated splash screen on iOS, set `animated` to `true`, and `launchAnimationDuration` to the desired animation length in milliseconds in your [Capacitor configuration file] (https://capacitorjs.com/docs/config).

Once done, you can add the splash screen assets into your app's `Assets.xcassets` file inside a folder called `Splash`. Each frame should be placed in order, with `Splash_xx` (where xx is the number it belongs to in sequence, e.g. splash_1.png, splash_2.png, ...).

## Android
If you want to have an animated splash screen on Android, create a `splash.xml` in your app's `res/drawable` folder with an animation list showing the location and duration of each frame.

For example:

```
<?xml version="1.0" encoding="utf-8"?>
<animation-list xmlns:android="http://schemas.android.com/apk/res/android" android:oneshot="false">
	<item android:duration="22" android:drawable="@drawable/splash_1">
	</item>
	<item android:duration="22" android:drawable="@drawable/splash_2">
	</item>
	<item android:duration="22" android:drawable="@drawable/splash_3">
	</item>
	<item android:duration="22" android:drawable="@drawable/splash_4">
	</item>
	<item android:duration="22" android:drawable="@drawable/splash_5">
	</item>
	<item android:duration="22" android:drawable="@drawable/splash_6">
	</item>
	<item android:duration="22" android:drawable="@drawable/splash_7">
	</item>
</animation-list>
```

Be sure to correctly scale your images as well, as not scaling them may cause significant performance degradation when loading your app.

## Configuration

<docgen-config>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

These config values are available:

| Prop                            | Type                                                                                                                          | Description                                                                                                                                                                                                  | Default             | Since |
| ------------------------------- | ----------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | ------------------- | ----- |
| **`launchShowDuration`**        | <code>number</code>                                                                                                           | How long to show the launch splash screen when autoHide is enabled (in ms)                                                                                                                                   | <code>500</code>    | 1.0.0 |
| **`launchAutoHide`**            | <code>boolean</code>                                                                                                          | Whether to auto hide the splash after launchShowDuration.                                                                                                                                                    | <code>true</code>   | 1.0.0 |
| **`backgroundColor`**           | <code>string</code>                                                                                                           | Color of the background of the Splash Screen in hex format, #RRGGBB or #RRGGBBAA. Doesn't work if `useDialog` is true.                                                                                       |                     | 1.0.0 |
| **`androidSplashResourceName`** | <code>string</code>                                                                                                           | Name of the resource to be used as Splash Screen. Only available on Android.                                                                                                                                 | <code>splash</code> | 1.0.0 |
| **`androidScaleType`**          | <code>'CENTER' \| 'CENTER_CROP' \| 'CENTER_INSIDE' \| 'FIT_CENTER' \| 'FIT_END' \| 'FIT_START' \| 'FIT_XY' \| 'MATRIX'</code> | The [ImageView.ScaleType](https://developer.android.com/reference/android/widget/ImageView.ScaleType) used to scale the Splash Screen image. Doesn't work if `useDialog` is true. Only available on Android. | <code>FIT_XY</code> | 1.0.0 |
| **`showSpinner`**               | <code>boolean</code>                                                                                                          | Show a loading spinner on the Splash Screen. Doesn't work if `useDialog` is true.                                                                                                                            |                     | 1.0.0 |
| **`androidSpinnerStyle`**       | <code>'horizontal' \| 'small' \| 'large' \| 'inverse' \| 'smallInverse' \| 'largeInverse'</code>                              | Style of the Android spinner. Doesn't work if `useDialog` is true.                                                                                                                                           | <code>large</code>  | 1.0.0 |
| **`iosSpinnerStyle`**           | <code>'small' \| 'large'</code>                                                                                               | Style of the iOS spinner. Doesn't work if `useDialog` is true. Only available on iOS.                                                                                                                        | <code>large</code>  | 1.0.0 |
| **`spinnerColor`**              | <code>string</code>                                                                                                           | Color of the spinner in hex format, #RRGGBB or #RRGGBBAA. Doesn't work if `useDialog` is true.                                                                                                               |                     | 1.0.0 |
| **`splashFullScreen`**          | <code>boolean</code>                                                                                                          | Hide the status bar on the Splash Screen. Only available on Android.                                                                                                                                         |                     | 1.0.0 |
| **`splashImmersive`**           | <code>boolean</code>                                                                                                          | Hide the status bar and the software navigation buttons on the Splash Screen. Only available on Android.                                                                                                     |                     | 1.0.0 |
| **`layoutName`**                | <code>string</code>                                                                                                           | If `useDialog` is set to true, configure the Dialog layout. If `useDialog` is not set or false, use a layout instead of the ImageView. Only available on Android.                                            |                     | 1.1.0 |
| **`useDialog`**                 | <code>boolean</code>                                                                                                          | Use a Dialog instead of an ImageView. If `layoutName` is not configured, it will use a layout that uses the splash image as background. Only available on Android.                                           |                     | 1.1.0 |
| **`animated`**                  | <code>boolean</code>                                                                                                          | Animate the splash screen using a series of image files.                                                                                                                                                     |                     | 1.2.3 |
| **`launchAnimationDuration`**   | <code>number</code>                                                                                                           | Play the multiple frames across the amount of milliseconds specified.                                                                                                                                        |                     | 1.2.3 |

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
      "useDialog": true,
      "animated": true,
      "launchAnimationDuration": 3000
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
      animated: true,
      launchAnimationDuration: 3000,
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

## Example Guides

[Adding Your Own Icons and Splash Screen Images &#8250;](https://www.joshmorony.com/adding-icons-splash-screens-launch-images-to-capacitor-projects/)

[Creating a Dynamic/Adaptable Splash Screen for Capacitor (Android) &#8250;](https://www.joshmorony.com/creating-a-dynamic-universal-splash-screen-for-capacitor-android/)

## API

<docgen-index>

* [`show(...)`](#show)
* [`updateProgress(...)`](#updateprogress)
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


### updateProgress(...)

```typescript
updateProgress(options: UpdateProgressOptions) => Promise<void>
```

Update progress of splash screen

| Param         | Type                                                                    |
| ------------- | ----------------------------------------------------------------------- |
| **`options`** | <code><a href="#updateprogressoptions">UpdateProgressOptions</a></code> |

**Since:** 1.2.3

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


#### UpdateProgressOptions

| Prop           | Type                | Description                     | Since |
| -------------- | ------------------- | ------------------------------- | ----- |
| **`progress`** | <code>number</code> | Set percentage of progress bar. | 1.2.3 |


#### HideOptions

| Prop                  | Type                | Description                   | Default          | Since |
| --------------------- | ------------------- | ----------------------------- | ---------------- | ----- |
| **`fadeOutDuration`** | <code>number</code> | How long (in ms) to fade out. | <code>200</code> | 1.0.0 |

</docgen-api>
