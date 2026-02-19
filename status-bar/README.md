# @capacitor/status-bar

The StatusBar API Provides methods for configuring the style of the Status Bar, along with showing or hiding it.

## Install

```bash
npm install @capacitor/status-bar
npx cap sync
```

## Android 16+ behavior change

For apps targeting **Android 16 (API level 36)** and higher using **Capacitor 8**, the following Status Bar configuration options **no longer work**:

- `overlaysWebView`
- `backgroundColor`

These options relied on the ability to opt out of Android’s **edge-to-edge** system UI behavior, which allowed apps to control how the status bar overlays and its background color.

In **Android 15 (API level 35)**, it was still possible to opt out of this enforced behavior by setting the `windowOptOutEdgeToEdgeEnforcement` property in the application layout file.
Without that property, the application assumed `overlaysWebView` as always `true`.
See more details in the Android documentation: [https://developer.android.com/reference/android/R.attr#windowOptOutEdgeToEdgeEnforcement](https://developer.android.com.reference/android/R.attr#windowOptOutEdgeToEdgeEnforcement)

Starting with **Android 16**, this opt-out is **no longer available**, and the behavior is enforced by the system.  
As a result, the `overlaysWebView` and `backgroundColor` configuration options no longer have any effect.

## iOS Note

This plugin requires "View controller-based status bar appearance"
(`UIViewControllerBasedStatusBarAppearance`) set to `YES` in `Info.plist`. Read
about [Configuring iOS](https://capacitorjs.com/docs/ios/configuration) for
help.

The status bar visibility defaults to visible and the style defaults to
`Style.Default`. You can change these defaults by adding
`UIStatusBarHidden` and/or `UIStatusBarStyle` in `Info.plist`.

## Example

```typescript
import { StatusBar, Style } from '@capacitor/status-bar';

// iOS only
window.addEventListener('statusTap', function () {
  console.log('statusbar tapped');
});

// Display content under transparent status bar
StatusBar.setOverlaysWebView({ overlay: true });

const setStatusBarStyleDark = async () => {
  await StatusBar.setStyle({ style: Style.Dark });
};

const setStatusBarStyleLight = async () => {
  await StatusBar.setStyle({ style: Style.Light });
};

const hideStatusBar = async () => {
  await StatusBar.hide();
};

const showStatusBar = async () => {
  await StatusBar.show();
};
```

## Configuration

<docgen-config>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

These config values are available:

| Prop                  | Type                 | Description                                                                                                                               | Default              | Since |
| --------------------- | -------------------- | ----------------------------------------------------------------------------------------------------------------------------------------- | -------------------- | ----- |
| **`overlaysWebView`** | <code>boolean</code> | Whether the statusbar is overlaid or not. Not available on Android 15+.                                                                   | <code>true</code>    | 1.0.0 |
| **`style`**           | <code>string</code>  | <a href="#style">Style</a> of the text of the status bar.                                                                                 | <code>default</code> | 1.0.0 |
| **`backgroundColor`** | <code>string</code>  | Color of the background of the statusbar in hex format, #RRGGBB. Doesn't work if `overlaysWebView` is true. Not available on Android 15+. | <code>#000000</code> | 1.0.0 |

### Examples

In `capacitor.config.json`:

```json
{
  "plugins": {
    "StatusBar": {
      "overlaysWebView": false,
      "style": "DARK",
      "backgroundColor": "#ffffffff"
    }
  }
}
```

In `capacitor.config.ts`:

```ts
/// <reference types="@capacitor/status-bar" />

import { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  plugins: {
    StatusBar: {
      overlaysWebView: false,
      style: "DARK",
      backgroundColor: "#ffffffff",
    },
  },
};

export default config;
```

</docgen-config>

## API

<docgen-index>

* [`setStyle(...)`](#setstyle)
* [`setBackgroundColor(...)`](#setbackgroundcolor)
* [`show(...)`](#show)
* [`hide(...)`](#hide)
* [`getInfo()`](#getinfo)
* [`setOverlaysWebView(...)`](#setoverlayswebview)
* [Interfaces](#interfaces)
* [Enums](#enums)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### setStyle(...)

```typescript
setStyle(options: StyleOptions) => Promise<void>
```

Set the current style of the status bar.

| Param         | Type                                                  |
| ------------- | ----------------------------------------------------- |
| **`options`** | <code><a href="#styleoptions">StyleOptions</a></code> |

**Since:** 1.0.0

--------------------


### setBackgroundColor(...)

```typescript
setBackgroundColor(options: BackgroundColorOptions) => Promise<void>
```

Set the background color of the status bar.
Calling this function updates the foreground color of the status bar if the style is set to default, except on iOS versions lower than 17.
Not available on Android 15+.

| Param         | Type                                                                      |
| ------------- | ------------------------------------------------------------------------- |
| **`options`** | <code><a href="#backgroundcoloroptions">BackgroundColorOptions</a></code> |

**Since:** 1.0.0

--------------------


### show(...)

```typescript
show(options?: AnimationOptions | undefined) => Promise<void>
```

Show the status bar.
On iOS, if the status bar is initially hidden and the initial style is set to
`UIStatusBarStyleLightContent`, first show call might present a glitch on the
animation showing the text as dark and then transition to light. It's recommended
to use <a href="#animation">`Animation.None`</a> as the animation on the first call.

| Param         | Type                                                          |
| ------------- | ------------------------------------------------------------- |
| **`options`** | <code><a href="#animationoptions">AnimationOptions</a></code> |

**Since:** 1.0.0

--------------------


### hide(...)

```typescript
hide(options?: AnimationOptions | undefined) => Promise<void>
```

Hide the status bar.

| Param         | Type                                                          |
| ------------- | ------------------------------------------------------------- |
| **`options`** | <code><a href="#animationoptions">AnimationOptions</a></code> |

**Since:** 1.0.0

--------------------


### getInfo()

```typescript
getInfo() => Promise<StatusBarInfo>
```

Get info about the current state of the status bar.

**Returns:** <code>Promise&lt;<a href="#statusbarinfo">StatusBarInfo</a>&gt;</code>

**Since:** 1.0.0

--------------------


### setOverlaysWebView(...)

```typescript
setOverlaysWebView(options: SetOverlaysWebViewOptions) => Promise<void>
```

Set whether or not the status bar should overlay the webview to allow usage
of the space underneath it.
Not available on Android 15+.

| Param         | Type                                                                            |
| ------------- | ------------------------------------------------------------------------------- |
| **`options`** | <code><a href="#setoverlayswebviewoptions">SetOverlaysWebViewOptions</a></code> |

**Since:** 1.0.0

--------------------


### Interfaces


#### StyleOptions

| Prop        | Type                                    | Description                                               | Since |
| ----------- | --------------------------------------- | --------------------------------------------------------- | ----- |
| **`style`** | <code><a href="#style">Style</a></code> | <a href="#style">Style</a> of the text of the status bar. | 1.0.0 |


#### BackgroundColorOptions

| Prop        | Type                | Description                                       | Since |
| ----------- | ------------------- | ------------------------------------------------- | ----- |
| **`color`** | <code>string</code> | A hex color to which the status bar color is set. | 1.0.0 |


#### AnimationOptions

| Prop            | Type                                            | Description                                                                                         | Default                     | Since |
| --------------- | ----------------------------------------------- | --------------------------------------------------------------------------------------------------- | --------------------------- | ----- |
| **`animation`** | <code><a href="#animation">Animation</a></code> | The type of status bar animation used when showing or hiding. This option is only supported on iOS. | <code>Animation.Fade</code> | 1.0.0 |


#### StatusBarInfo

| Prop           | Type                                    | Description                               | Since |
| -------------- | --------------------------------------- | ----------------------------------------- | ----- |
| **`visible`**  | <code>boolean</code>                    | Whether the status bar is visible or not. | 1.0.0 |
| **`style`**    | <code><a href="#style">Style</a></code> | The current status bar style.             | 1.0.0 |
| **`color`**    | <code>string</code>                     | The current status bar color.             | 1.0.0 |
| **`overlays`** | <code>boolean</code>                    | Whether the statusbar is overlaid or not. | 1.0.0 |


#### SetOverlaysWebViewOptions

| Prop          | Type                 | Description                               | Since |
| ------------- | -------------------- | ----------------------------------------- | ----- |
| **`overlay`** | <code>boolean</code> | Whether to overlay the status bar or not. | 1.0.0 |


### Enums


#### Style

| Members       | Value                  | Description                                                                                                                                                                          | Since |
| ------------- | ---------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | ----- |
| **`Dark`**    | <code>'DARK'</code>    | Light text for dark backgrounds.                                                                                                                                                     | 1.0.0 |
| **`Light`**   | <code>'LIGHT'</code>   | Dark text for light backgrounds.                                                                                                                                                     | 1.0.0 |
| **`Default`** | <code>'DEFAULT'</code> | The style is based on the device appearance. If the device is using Dark mode, the statusbar text will be light. If the device is using Light mode, the statusbar text will be dark. | 1.0.0 |


#### Animation

| Members     | Value                | Description                                                   | Since |
| ----------- | -------------------- | ------------------------------------------------------------- | ----- |
| **`None`**  | <code>'NONE'</code>  | No animation during show/hide.                                | 1.0.0 |
| **`Slide`** | <code>'SLIDE'</code> | Slide animation during show/hide. It doesn't work on iOS 15+. | 1.0.0 |
| **`Fade`**  | <code>'FADE'</code>  | Fade animation during show/hide.                              | 1.0.0 |

</docgen-api>
