# @capacitor/keyboard

The Keyboard API provides keyboard display and visibility control, along with event tracking when the keyboard shows and hides.

## Install

```bash
npm install @capacitor/keyboard
npx cap sync
```

## Example

```typescript
import { Keyboard } from '@capacitor/keyboard';

Keyboard.addListener('keyboardWillShow', info => {
  console.log('keyboard will show with height:', info.keyboardHeight);
});

Keyboard.addListener('keyboardDidShow', info => {
  console.log('keyboard did show with height:', info.keyboardHeight);
});

Keyboard.addListener('keyboardWillHide', () => {
  console.log('keyboard will hide');
});

Keyboard.addListener('keyboardDidHide', () => {
  console.log('keyboard did hide');
});
```

## Configuration

On iOS, the keyboard can be configured with the following options:

| Prop                     | Type                                                      | Description                                                                                                                                                                                                                                                                                                       | Default             | Since |
| ------------------------ | --------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------- | ----- |
| **`resize`**             | <code><a href="#keyboardresize">KeyboardResize</a></code> | Configure the way the app is resized when the Keyboard appears. Only available on iOS.                                                                                                                                                                                                                            | <code>native</code> | 1.0.0 |
| **`style`**              | <code><a href="#keyboardstyle">KeyboardStyle</a></code>   | Override the keyboard style if your app doesn't support dark/light theme changes. If not set, the keyboard style will depend on the device appearance. Only available on iOS.                                                                                                                                     |                     | 1.0.0 |
| **`resizeOnFullScreen`** | <code>boolean</code>                                      | There is an Android bug that prevents the keyboard from resizing the WebView when the app is in full screen (i.e. if StatusBar plugin is used to overlay the status bar). This setting, if set to true, add a workaround that resizes the WebView even when the app is in full screen. Only available for Android |                     | 1.1.0 |

### Examples

In `capacitor.config.json`:

```json
{
  "plugins": {
    "Keyboard": {
      "resize": "body",
      "style": "DARK",
      "resizeOnFullScreen": true
    }
  }
}
```

In `capacitor.config.ts`:

```ts
/// <reference types="@capacitor/keyboard" />

import { CapacitorConfig } from '@capacitor/cli';
import { KeyboardResize, KeyboardStyle } from '@capacitor/keyboard';

const config: CapacitorConfig = {
  plugins: {
    Keyboard: {
      resize: KeyboardResize.Body,
      style: KeyboardStyle.Dark,
      resizeOnFullScreen: true,
    },
  },
};

export default config;
```

## Compatibility with `cordova-plugin-ionic-keyboard`

To maintain compatibility with
[`cordova-plugin-ionic-keyboard`](https://github.com/ionic-team/cordova-plugin-ionic-keyboard),
the following events also work with `window.addEventListener`:

- `keyboardWillShow`
- `keyboardDidShow`
- `keyboardWillHide`
- `keyboardDidHide`

## API

<docgen-index>

* [`show()`](#show)
* [`hide()`](#hide)
* [`setAccessoryBarVisible(...)`](#setaccessorybarvisible)
* [`setScroll(...)`](#setscroll)
* [`setStyle(...)`](#setstyle)
* [`setResizeMode(...)`](#setresizemode)
* [`getResizeMode()`](#getresizemode)
* [`addListener('keyboardWillShow', ...)`](#addlistenerkeyboardwillshow-)
* [`addListener('keyboardDidShow', ...)`](#addlistenerkeyboarddidshow-)
* [`addListener('keyboardWillHide', ...)`](#addlistenerkeyboardwillhide-)
* [`addListener('keyboardDidHide', ...)`](#addlistenerkeyboarddidhide-)
* [`removeAllListeners()`](#removealllisteners)
* [Interfaces](#interfaces)
* [Enums](#enums)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### show()

```typescript
show() => Promise<void>
```

Show the keyboard. This method is alpha and may have issues.

This method is only supported on Android.

**Since:** 1.0.0

--------------------


### hide()

```typescript
hide() => Promise<void>
```

Hide the keyboard.

**Since:** 1.0.0

--------------------


### setAccessoryBarVisible(...)

```typescript
setAccessoryBarVisible(options: { isVisible: boolean; }) => Promise<void>
```

Set whether the accessory bar should be visible on the keyboard. We recommend disabling
the accessory bar for short forms (login, signup, etc.) to provide a cleaner UI.

This method is only supported on iPhone devices.

| Param         | Type                                 |
| ------------- | ------------------------------------ |
| **`options`** | <code>{ isVisible: boolean; }</code> |

**Since:** 1.0.0

--------------------


### setScroll(...)

```typescript
setScroll(options: { isDisabled: boolean; }) => Promise<void>
```

Programmatically enable or disable the WebView scroll.

This method is only supported on iOS.

| Param         | Type                                  |
| ------------- | ------------------------------------- |
| **`options`** | <code>{ isDisabled: boolean; }</code> |

**Since:** 1.0.0

--------------------


### setStyle(...)

```typescript
setStyle(options: KeyboardStyleOptions) => Promise<void>
```

Programmatically set the keyboard style.

This method is only supported on iOS.

| Param         | Type                                                                  |
| ------------- | --------------------------------------------------------------------- |
| **`options`** | <code><a href="#keyboardstyleoptions">KeyboardStyleOptions</a></code> |

**Since:** 1.0.0

--------------------


### setResizeMode(...)

```typescript
setResizeMode(options: KeyboardResizeOptions) => Promise<void>
```

Programmatically set the resize mode.

This method is only supported on iOS.

| Param         | Type                                                                    |
| ------------- | ----------------------------------------------------------------------- |
| **`options`** | <code><a href="#keyboardresizeoptions">KeyboardResizeOptions</a></code> |

**Since:** 1.0.0

--------------------


### getResizeMode()

```typescript
getResizeMode() => Promise<KeyboardResizeOptions>
```

Get the currently set resize mode.

This method is only supported on iOS.

**Returns:** <code>Promise&lt;<a href="#keyboardresizeoptions">KeyboardResizeOptions</a>&gt;</code>

**Since:** 4.0.0

--------------------


### addListener('keyboardWillShow', ...)

```typescript
addListener(eventName: 'keyboardWillShow', listenerFunc: (info: KeyboardInfo) => void) => Promise<PluginListenerHandle> & PluginListenerHandle
```

Listen for when the keyboard is about to be shown.

On Android keyboardWillShow and keyboardDidShow fire almost at the same time.

| Param              | Type                                                                     |
| ------------------ | ------------------------------------------------------------------------ |
| **`eventName`**    | <code>'keyboardWillShow'</code>                                          |
| **`listenerFunc`** | <code>(info: <a href="#keyboardinfo">KeyboardInfo</a>) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt; & <a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

**Since:** 1.0.0

--------------------


### addListener('keyboardDidShow', ...)

```typescript
addListener(eventName: 'keyboardDidShow', listenerFunc: (info: KeyboardInfo) => void) => Promise<PluginListenerHandle> & PluginListenerHandle
```

Listen for when the keyboard is shown.

On Android keyboardWillShow and keyboardDidShow fire almost at the same time.

| Param              | Type                                                                     |
| ------------------ | ------------------------------------------------------------------------ |
| **`eventName`**    | <code>'keyboardDidShow'</code>                                           |
| **`listenerFunc`** | <code>(info: <a href="#keyboardinfo">KeyboardInfo</a>) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt; & <a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

**Since:** 1.0.0

--------------------


### addListener('keyboardWillHide', ...)

```typescript
addListener(eventName: 'keyboardWillHide', listenerFunc: () => void) => Promise<PluginListenerHandle> & PluginListenerHandle
```

Listen for when the keyboard is about to be hidden.

On Android keyboardWillHide and keyboardDidHide fire almost at the same time.

| Param              | Type                            |
| ------------------ | ------------------------------- |
| **`eventName`**    | <code>'keyboardWillHide'</code> |
| **`listenerFunc`** | <code>() =&gt; void</code>      |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt; & <a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

**Since:** 1.0.0

--------------------


### addListener('keyboardDidHide', ...)

```typescript
addListener(eventName: 'keyboardDidHide', listenerFunc: () => void) => Promise<PluginListenerHandle> & PluginListenerHandle
```

Listen for when the keyboard is hidden.

On Android keyboardWillHide and keyboardDidHide fire almost at the same time.

| Param              | Type                           |
| ------------------ | ------------------------------ |
| **`eventName`**    | <code>'keyboardDidHide'</code> |
| **`listenerFunc`** | <code>() =&gt; void</code>     |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt; & <a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

**Since:** 1.0.0

--------------------


### removeAllListeners()

```typescript
removeAllListeners() => Promise<void>
```

Remove all native listeners for this plugin.

**Since:** 1.0.0

--------------------


### Interfaces


#### KeyboardStyleOptions

| Prop        | Type                                                    | Description            | Default                            | Since |
| ----------- | ------------------------------------------------------- | ---------------------- | ---------------------------------- | ----- |
| **`style`** | <code><a href="#keyboardstyle">KeyboardStyle</a></code> | Style of the keyboard. | <code>KeyboardStyle.Default</code> | 1.0.0 |


#### KeyboardResizeOptions

| Prop       | Type                                                      | Description                                             | Since |
| ---------- | --------------------------------------------------------- | ------------------------------------------------------- | ----- |
| **`mode`** | <code><a href="#keyboardresize">KeyboardResize</a></code> | Mode used to resize elements when the keyboard appears. | 1.0.0 |


#### PluginListenerHandle

| Prop         | Type                                      |
| ------------ | ----------------------------------------- |
| **`remove`** | <code>() =&gt; Promise&lt;void&gt;</code> |


#### KeyboardInfo

| Prop                 | Type                | Description             | Since |
| -------------------- | ------------------- | ----------------------- | ----- |
| **`keyboardHeight`** | <code>number</code> | Height of the heyboard. | 1.0.0 |


### Enums


#### KeyboardStyle

| Members       | Value                  | Description                                                                                                                                                                                                                                 | Since |
| ------------- | ---------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ----- |
| **`Dark`**    | <code>'DARK'</code>    | Dark keyboard.                                                                                                                                                                                                                              | 1.0.0 |
| **`Light`**   | <code>'LIGHT'</code>   | Light keyboard.                                                                                                                                                                                                                             | 1.0.0 |
| **`Default`** | <code>'DEFAULT'</code> | On iOS 13 and newer the keyboard style is based on the device appearance. If the device is using Dark mode, the keyboard will be dark. If the device is using Light mode, the keyboard will be light. On iOS 12 the keyboard will be light. | 1.0.0 |


#### KeyboardResize

| Members      | Value                 | Description                                                                                                          | Since |
| ------------ | --------------------- | -------------------------------------------------------------------------------------------------------------------- | ----- |
| **`Body`**   | <code>'body'</code>   | Only the `body` HTML element will be resized. Relative units are not affected, because the viewport does not change. | 1.0.0 |
| **`Ionic`**  | <code>'ionic'</code>  | Only the `ion-app` HTML element will be resized. Use it only for Ionic Framework apps.                               | 1.0.0 |
| **`Native`** | <code>'native'</code> | The whole native Web View will be resized when the keyboard shows/hides. This affects the `vh` relative unit.        | 1.0.0 |
| **`None`**   | <code>'none'</code>   | Neither the app nor the Web View are resized.                                                                        | 1.0.0 |

</docgen-api>
