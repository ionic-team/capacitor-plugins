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
SplashScreen.hide();

// Show the splash for an indefinite amount of time:
SplashScreen.show({
  autoHide: false
});

// Show the splash for two seconds and then automatically hide it:
SplashScreen.show({
  showDuration: 2000,
  autoHide: true
});
```

## Hiding the Splash Screen

By default, the Splash Screen is set to automatically hide after a certain amount of time (3 seconds). However, your app should boot much faster than this!

To make sure you provide the fastest app loading experience to your users, you should hide the splash screen when your app is ready to be used by calling `hide()` as soon as possible.

If your app needs longer than 3 seconds to load, configure the default duration by setting `launchShowDuration` in your [Capacitor configuration file](https://capacitorjs.com/docs/config).

If you want to be sure the splash never hides before the app is fully loaded, set `launchAutoHide` to `false`.

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

## Full Screen & Immersive (Android only)

You can enable `splashFullScreen` to hide status bar, or `splashImmersive` to hide both status bar and software navigation buttons. If both options are enabled `splashImmersive` takes priority, as it also fulfils `splashFullScreen` functionality.

## Configuration

These config values are available:

```json
{
  "plugins": {
    "SplashScreen": {
      "launchShowDuration": 3000,
      "launchAutoHide": true,
      "backgroundColor": "#ffffffff",
      "androidSplashResourceName": "splash",
      "androidScaleType": "CENTER_CROP",
      "androidSpinnerStyle": "large",
      "iosSpinnerStyle": "small",
      "spinnerColor": "#999999",
      "showSpinner": true,
      "splashFullScreen": true,
      "splashImmersive": true
    }
  }
}
```

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
