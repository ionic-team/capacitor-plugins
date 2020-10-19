# @capacitor/app

The App API handles high level App state and events.For example, this API emits events when the app enters and leaves the foreground, handles deeplinks, opens other apps, and manages persisted plugin state.

<!--DOCGEN_INDEX_START-->
<div class="docgen docgen-index">

* [`exitApp()`](#exitapp)
* [`getInfo()`](#getinfo)
* [`getState()`](#getstate)
* [`getLaunchUrl()`](#getlaunchurl)
* [`addListener(...)`](#addlistener)
* [`addListener(...)`](#addlistener)
* [`addListener(...)`](#addlistener)
* [`addListener(...)`](#addlistener)
* [`removeAllListeners()`](#removealllisteners)
* [Interfaces](#interfaces)

</div>
<!--DOCGEN_INDEX_END-->

<!--DOCGEN_API_START-->
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->
<div class="docgen docgen-api">

## API

### exitApp()

```typescript
exitApp() => never
```

Force exit the app. This should only be used in conjunction with the `backButton` handler for Android to
exit the app when navigation is complete.

Ionic handles this itself so you shouldn't need to call this if using Ionic.

**Returns:** <code>never</code>

**Since:** 1.0.0

--------------------


### getInfo()

```typescript
getInfo() => Promise<AppInfo>
```

Return information about the app.

**Returns:** <code>Promise&lt;<a href="#appinfo">AppInfo</a>&gt;</code>

**Since:** 1.0.0

--------------------


### getState()

```typescript
getState() => Promise<AppState>
```

Gets the current app state.

**Returns:** <code>Promise&lt;<a href="#appstate">AppState</a>&gt;</code>

**Since:** 1.0.0

--------------------


### getLaunchUrl()

```typescript
getLaunchUrl() => Promise<AppLaunchUrl>
```

Get the URL the app was launched with, if any.

**Returns:** <code>Promise&lt;<a href="#applaunchurl">AppLaunchUrl</a>&gt;</code>

**Since:** 1.0.0

--------------------


### addListener(...)

```typescript
addListener(eventName: 'appStateChange', listenerFunc: (state: AppState) => void) => PluginListenerHandle
```

Listen for changes in the App's active state (whether the app is in the foreground or background)

| Param              | Type                                   |
| ------------------ | -------------------------------------- |
| **`eventName`**    | <code>"appStateChange"</code>          |
| **`listenerFunc`** | <code>(state: AppState) => void</code> |

**Returns:** <code><a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

**Since:** 1.0.0

--------------------


### addListener(...)

```typescript
addListener(eventName: 'appUrlOpen', listenerFunc: (data: AppUrlOpen) => void) => PluginListenerHandle
```

Listen for url open events for the app. This handles both custom URL scheme links as well
as URLs your app handles (Universal Links on iOS and App Links on Android)

| Param              | Type                                    |
| ------------------ | --------------------------------------- |
| **`eventName`**    | <code>"appUrlOpen"</code>               |
| **`listenerFunc`** | <code>(data: AppUrlOpen) => void</code> |

**Returns:** <code><a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

**Since:** 1.0.0

--------------------


### addListener(...)

```typescript
addListener(eventName: 'appRestoredResult', listenerFunc: (data: AppRestoredResult) => void) => PluginListenerHandle
```

If the app was launched with previously persisted plugin call data, such as on Android
when an activity returns to an app that was closed, this call will return any data
the app was launched with, converted into the form of a result from a plugin call.

| Param              | Type                                           |
| ------------------ | ---------------------------------------------- |
| **`eventName`**    | <code>"appRestoredResult"</code>               |
| **`listenerFunc`** | <code>(data: AppRestoredResult) => void</code> |

**Returns:** <code><a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

**Since:** 1.0.0

--------------------


### addListener(...)

```typescript
addListener(eventName: 'backButton', listenerFunc: () => void) => PluginListenerHandle
```

Listen for the hardware back button event (Android only). Listening for this event will disable the
default back button behaviour, so you might want to call `window.history.back()` manually.
If you want to close the app, call `App.exitApp()`.

| Param              | Type                      |
| ------------------ | ------------------------- |
| **`eventName`**    | <code>"backButton"</code> |
| **`listenerFunc`** | <code>() => void</code>   |

**Returns:** <code><a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

**Since:** 1.0.0

--------------------


### removeAllListeners()

```typescript
removeAllListeners() => void
```

Remove all native listeners for this plugin

**Returns:** <code>void</code>

**Since:** 1.0.0

--------------------


### Interfaces


#### AppInfo

| Prop          | Type                | Description                                                                                         | Since |
| ------------- | ------------------- | --------------------------------------------------------------------------------------------------- | ----- |
| **`name`**    | <code>string</code> | The name of the app.                                                                                | 1.0.0 |
| **`id`**      | <code>string</code> | The identifier of the app. On iOS it's the Bundle Identifier. On Android it's the Application ID    | 1.0.0 |
| **`build`**   | <code>string</code> | The build version. On iOS it's the CFBundleVersion. On Android it's the versionCode.                | 1.0.0 |
| **`version`** | <code>string</code> | The app version. On iOS it's the CFBundleShortVersionString. On Android it's package's versionName. | 1.0.0 |


#### AppState

| Prop           | Type                 | Description                       | Since |
| -------------- | -------------------- | --------------------------------- | ----- |
| **`isActive`** | <code>boolean</code> | Whether the app is active or not. | 1.0.0 |


#### AppLaunchUrl

| Prop      | Type                | Description                   | Since |
| --------- | ------------------- | ----------------------------- | ----- |
| **`url`** | <code>string</code> | The url used to open the app. | 1.0.0 |


#### PluginListenerHandle

| Prop         | Type                    |
| ------------ | ----------------------- |
| **`remove`** | <code>() => void</code> |


#### AppUrlOpen

| Prop                       | Type                 | Description                                                                                                                                                                        | Since |
| -------------------------- | -------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ----- |
| **`url`**                  | <code>string</code>  | The URL the app was opened with.                                                                                                                                                   | 1.0.0 |
| **`iosSourceApplication`** | <code>any</code>     | The source application opening the app (iOS only) https://developer.apple.com/documentation/uikit/uiapplicationopenurloptionskey/1623128-sourceapplication                         | 1.0.0 |
| **`iosOpenInPlace`**       | <code>boolean</code> | Whether the app should open the passed document in-place or must copy it first. https://developer.apple.com/documentation/uikit/uiapplicationopenurloptionskey/1623123-openinplace | 1.0.0 |


#### AppRestoredResult

| Prop             | Type                              | Description                                                                                                                                       | Since |
| ---------------- | --------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------- | ----- |
| **`pluginId`**   | <code>string</code>               | The pluginId this result corresponds to. For example, `Camera`.                                                                                   | 1.0.0 |
| **`methodName`** | <code>string</code>               | The methodName this result corresponds to. For example, `getPhoto`                                                                                | 1.0.0 |
| **`data`**       | <code>any</code>                  | The result data passed from the plugin. This would be the result you'd expect from normally calling the plugin method. For example, `CameraPhoto` | 1.0.0 |
| **`success`**    | <code>boolean</code>              | Boolean indicating if the plugin call succeeded.                                                                                                  | 1.0.0 |
| **`error`**      | <code>{ message: string; }</code> | If the plugin call didn't succeed, it will contain the error message.                                                                             | 1.0.0 |

</div>
<!--DOCGEN_API_END-->