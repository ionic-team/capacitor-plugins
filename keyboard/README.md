# @capacitor/keyboard

The Keyboard API provides keyboard display and visibility control, along with event tracking when the keyboard shows and hides.

<!--DOCGEN_INDEX_START-->
* [`show()`](#show)
* [`hide()`](#hide)
* [`setAccessoryBarVisible(...)`](#setaccessorybarvisible)
* [`setScroll(...)`](#setscroll)
* [`setStyle(...)`](#setstyle)
* [`setResizeMode(...)`](#setresizemode)
* [`addListener(...)`](#addlistener)
* [`addListener(...)`](#addlistener)
* [`addListener(...)`](#addlistener)
* [`addListener(...)`](#addlistener)
* [`removeAllListeners()`](#removealllisteners)
* [Interfaces](#interfaces)
* [Enums](#enums)
<!--DOCGEN_INDEX_END-->

<!--DOCGEN_API_START-->
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->
## API

### show()

```typescript
show() => Promise<void>
```

Show the keyboard. This method is alpha and may have issues.

This method is only supported on Android.

**Returns:** <code>Promise&lt;void&gt;</code>

**Since:** 1.0.0

--------------------


### hide()

```typescript
hide() => Promise<void>
```

Hide the keyboard.

**Returns:** <code>Promise&lt;void&gt;</code>

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

**Returns:** <code>Promise&lt;void&gt;</code>

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

**Returns:** <code>Promise&lt;void&gt;</code>

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

**Returns:** <code>Promise&lt;void&gt;</code>

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

**Returns:** <code>Promise&lt;void&gt;</code>

**Since:** 1.0.0

--------------------


### addListener(...)

```typescript
addListener(eventName: 'keyboardWillShow', listenerFunc: (info: KeyboardInfo) => void) => PluginListenerHandle
```

Listen for when the keyboard is about to be shown.

| Param              | Type                                      |
| ------------------ | ----------------------------------------- |
| **`eventName`**    | <code>"keyboardWillShow"</code>           |
| **`listenerFunc`** | <code>(info: KeyboardInfo) => void</code> |

**Returns:** <code><a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

**Since:** 1.0.0

--------------------


### addListener(...)

```typescript
addListener(eventName: 'keyboardDidShow', listenerFunc: (info: KeyboardInfo) => void) => PluginListenerHandle
```

Listen for when the keyboard is shown.

| Param              | Type                                      |
| ------------------ | ----------------------------------------- |
| **`eventName`**    | <code>"keyboardDidShow"</code>            |
| **`listenerFunc`** | <code>(info: KeyboardInfo) => void</code> |

**Returns:** <code><a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

**Since:** 1.0.0

--------------------


### addListener(...)

```typescript
addListener(eventName: 'keyboardWillHide', listenerFunc: () => void) => PluginListenerHandle
```

Listen for when the keyboard is about to be hidden.

| Param              | Type                            |
| ------------------ | ------------------------------- |
| **`eventName`**    | <code>"keyboardWillHide"</code> |
| **`listenerFunc`** | <code>() => void</code>         |

**Returns:** <code><a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

**Since:** 1.0.0

--------------------


### addListener(...)

```typescript
addListener(eventName: 'keyboardDidHide', listenerFunc: () => void) => PluginListenerHandle
```

Listen for when the keyboard is hidden.

| Param              | Type                           |
| ------------------ | ------------------------------ |
| **`eventName`**    | <code>"keyboardDidHide"</code> |
| **`listenerFunc`** | <code>() => void</code>        |

**Returns:** <code><a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

**Since:** 1.0.0

--------------------


### removeAllListeners()

```typescript
removeAllListeners() => void
```

Remove all native listeners for this plugin.

**Returns:** <code>void</code>

**Since:** 1.0.0

--------------------


### Interfaces


#### KeyboardStyleOptions

| Prop        | Type                                                    | Description            | Since |
| ----------- | ------------------------------------------------------- | ---------------------- | ----- |
| **`style`** | <code><a href="#keyboardstyle">KeyboardStyle</a></code> | Style of the keyboard. | 1.0.0 |


#### KeyboardResizeOptions

| Prop       | Type                                                      | Description                                             | Since |
| ---------- | --------------------------------------------------------- | ------------------------------------------------------- | ----- |
| **`mode`** | <code><a href="#keyboardresize">KeyboardResize</a></code> | Mode used to resize elements when the keyboard appears. | 1.0.0 |


#### PluginListenerHandle

| Prop         | Type                    |
| ------------ | ----------------------- |
| **`remove`** | <code>() => void</code> |


#### KeyboardInfo

| Prop                 | Type                | Description             | Since |
| -------------------- | ------------------- | ----------------------- | ----- |
| **`keyboardHeight`** | <code>number</code> | Height of the heyboard. | 1.0.0 |


### Enums


#### KeyboardStyle

| Members     | Value                | Description     | Since |
| ----------- | -------------------- | --------------- | ----- |
| **`Dark`**  | <code>'DARK'</code>  | Dark keyboard.  | 1.0.0 |
| **`Light`** | <code>'LIGHT'</code> | Light keyboard. | 1.0.0 |


#### KeyboardResize

| Members      | Value                 | Description            | Since |
| ------------ | --------------------- | ---------------------- | ----- |
| **`Body`**   | <code>'body'</code>   | Resizes the html body. | 1.0.0 |
| **`Ionic`**  | <code>'ionic'</code>  | Resizes Ionic app      | 1.0.0 |
| **`Native`** | <code>'native'</code> | Resizes the WebView.   | 1.0.0 |
| **`None`**   | <code>'none'</code>   | Don't resize anything. | 1.0.0 |


<!--DOCGEN_API_END-->