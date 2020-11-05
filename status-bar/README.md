# @capacitor/status-bar

The StatusBar API Provides methods for configuring the style of the Status Bar, along with showing or hiding it.

## Install

```bash
npm install @capacitor/status-bar
npx cap sync
```

## iOS Note

This plugin requires "View controller-based status bar appearance"
(`UIViewControllerBasedStatusBarAppearance`) set to `YES` in `Info.plist`. Read
about [Configuring iOS](https://capacitorjs.com/docs/ios/configuration) for
help.

The status bar visibility defaults to visible and the style defaults to
`StatusBarStyle.Light`. You can change these defaults by adding
`UIStatusBarHidden` and/or `UIStatusBarStyle` in `Info.plist`.

`setBackgroundColor` and `setOverlaysWebView` are currently not supported on
iOS devices.

## Example

```typescript
import { StatusBar, StatusBarStyle } from '@capacitor/status-bar';

// iOS only
window.addEventListener('statusTap', function () {
  console.log('statusbar tapped');
});

// Display content under transparent status bar (Android only)
StatusBar.setOverlaysWebView({ overlay: true });

const setStatusBarStyleDark = async () => {
  await StatusBar.setStyle({ style: StatusBarStyle.Dark });
};

const setStatusBarStyleLight = async () => {
  await StatusBar.setStyle({ style: StatusBarStyle.Light });
};

const hideStatusBar = async () => {
  await StatusBar.hide();
};

const showStatusBar = async () => {
  await StatusBar.show();
};
```

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
setStyle(options: StatusBarStyleOptions) => Promise<void>
```

Set the current style of the status bar.

| Param         | Type                                                                    |
| ------------- | ----------------------------------------------------------------------- |
| **`options`** | <code><a href="#statusbarstyleoptions">StatusBarStyleOptions</a></code> |

**Since:** 1.0.0

--------------------


### setBackgroundColor(...)

```typescript
setBackgroundColor(options: StatusBarBackgroundColorOptions) => Promise<void>
```

Set the background color of the status bar.

This method is only supported on Android.

| Param         | Type                                                                                        |
| ------------- | ------------------------------------------------------------------------------------------- |
| **`options`** | <code><a href="#statusbarbackgroundcoloroptions">StatusBarBackgroundColorOptions</a></code> |

**Since:** 1.0.0

--------------------


### show(...)

```typescript
show(options?: StatusBarAnimationOptions | undefined) => Promise<void>
```

Show the status bar.

| Param         | Type                                                                            |
| ------------- | ------------------------------------------------------------------------------- |
| **`options`** | <code><a href="#statusbaranimationoptions">StatusBarAnimationOptions</a></code> |

**Since:** 1.0.0

--------------------


### hide(...)

```typescript
hide(options?: StatusBarAnimationOptions | undefined) => Promise<void>
```

Hide the status bar.

| Param         | Type                                                                            |
| ------------- | ------------------------------------------------------------------------------- |
| **`options`** | <code><a href="#statusbaranimationoptions">StatusBarAnimationOptions</a></code> |

**Since:** 1.0.0

--------------------


### getInfo()

```typescript
getInfo() => Promise<StatusBarInfoResult>
```

Get info about the current state of the status bar.

**Returns:** <code>Promise&lt;<a href="#statusbarinforesult">StatusBarInfoResult</a>&gt;</code>

**Since:** 1.0.0

--------------------


### setOverlaysWebView(...)

```typescript
setOverlaysWebView(options: StatusBarOverlaysWebviewOptions) => Promise<void>
```

Set whether or not the status bar should overlay the webview to allow usage
of the space underneath it.

This method is only supported on Android.

| Param         | Type                                                                                        |
| ------------- | ------------------------------------------------------------------------------------------- |
| **`options`** | <code><a href="#statusbaroverlayswebviewoptions">StatusBarOverlaysWebviewOptions</a></code> |

**Since:** 1.0.0

--------------------


### Interfaces


#### StatusBarStyleOptions

| Prop        | Type                                                      | Description                          | Since |
| ----------- | --------------------------------------------------------- | ------------------------------------ | ----- |
| **`style`** | <code><a href="#statusbarstyle">StatusBarStyle</a></code> | Style of the text of the status bar. | 1.0.0 |


#### StatusBarBackgroundColorOptions

| Prop        | Type                | Description                                                                                 | Since |
| ----------- | ------------------- | ------------------------------------------------------------------------------------------- | ----- |
| **`color`** | <code>string</code> | A hex color to which the status bar color is set. This option is only supported on Android. | 1.0.0 |


#### StatusBarAnimationOptions

| Prop            | Type                                                              | Description                                                                                         | Since |
| --------------- | ----------------------------------------------------------------- | --------------------------------------------------------------------------------------------------- | ----- |
| **`animation`** | <code><a href="#statusbaranimation">StatusBarAnimation</a></code> | The type of status bar animation used when showing or hiding. This option is only supported on iOS. | 1.0.0 |


#### StatusBarInfoResult

| Prop           | Type                                                      | Description                                                                         | Since |
| -------------- | --------------------------------------------------------- | ----------------------------------------------------------------------------------- | ----- |
| **`visible`**  | <code>boolean</code>                                      | Whether the status bar is visible or not.                                           | 1.0.0 |
| **`style`**    | <code><a href="#statusbarstyle">StatusBarStyle</a></code> | The current status bar style.                                                       | 1.0.0 |
| **`color`**    | <code>string</code>                                       | The current status bar color. This option is only supported on Android.             | 1.0.0 |
| **`overlays`** | <code>boolean</code>                                      | Whether the statusbar is overlaid or not. This option is only supported on Android. | 1.0.0 |


#### StatusBarOverlaysWebviewOptions

| Prop          | Type                 | Description                               | Since |
| ------------- | -------------------- | ----------------------------------------- | ----- |
| **`overlay`** | <code>boolean</code> | Whether to overlay the status bar or not. | 1.0.0 |


### Enums


#### StatusBarStyle

| Members       | Value                  | Description                                                                                                                                                                                                                                                                                                                     | Since |
| ------------- | ---------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ----- |
| **`Dark`**    | <code>'DARK'</code>    | Light text for dark backgrounds.                                                                                                                                                                                                                                                                                                | 1.0.0 |
| **`Light`**   | <code>'LIGHT'</code>   | Dark text for light backgrounds.                                                                                                                                                                                                                                                                                                | 1.0.0 |
| **`Default`** | <code>'DEFAULT'</code> | On iOS 13 and newer the style is based on the device appearance. If the device is using Dark mode, the statusbar text will be light. If the device is using Light mode, the statusbar text will be dark. On iOS 12 and older the statusbar text will be dark. On Android the default will be the one the app was launched with. | 1.0.0 |


#### StatusBarAnimation

| Members     | Value                | Description                       | Since |
| ----------- | -------------------- | --------------------------------- | ----- |
| **`None`**  | <code>'NONE'</code>  | No animation during show/hide.    | 1.0.0 |
| **`Slide`** | <code>'SLIDE'</code> | Slide animation during show/hide. | 1.0.0 |
| **`Fade`**  | <code>'FADE'</code>  | Fade animation during show/hide.  | 1.0.0 |

</docgen-api>
