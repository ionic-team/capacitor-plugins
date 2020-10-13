# @capacitor/app

The App API handles high level App state and events.For example, this API emits events when the app enters and leaves the foreground, handles deeplinks, opens other apps, and manages persisted plugin state.

<!--DOCGEN_INDEX_START-->
* [exitApp()](#exitapp)
* [getInfo()](#getinfo)
* [getState()](#getstate)
* [getLaunchUrl()](#getlaunchurl)
* [addListener()](#addlistener)
* [addListener()](#addlistener)
* [addListener()](#addlistener)
* [addListener()](#addlistener)
* [removeAllListeners()](#removealllisteners)
* [Interfaces](#interfaces)
<!--DOCGEN_INDEX_END-->

<!--DOCGEN_API_START-->
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->
## API

### exitApp

```typescript
exitApp() => never
```

Force exit the app. This should only be used in conjunction with the `backButton` handler for Android to
exit the app when navigation is complete.

Ionic handles this itself so you shouldn't need to call this if using Ionic

**Returns:** never

--------------------


### getInfo

```typescript
getInfo() => Promise<AppInfo>
```

Return information about the underlying device/os/platform.

**Returns:** Promise&lt;[AppInfo](#appinfo)&gt;

**Since:** 1.0.0

--------------------


### getState

```typescript
getState() => Promise<AppState>
```

Gets the current app state

**Returns:** Promise&lt;[AppState](#appstate)&gt;

--------------------


### getLaunchUrl

```typescript
getLaunchUrl() => Promise<AppLaunchUrl>
```

Get the URL the app was launched with, if any

**Returns:** Promise&lt;[AppLaunchUrl](#applaunchurl)&gt;

--------------------


### addListener

```typescript
addListener(eventName: 'appStateChange', listenerFunc: (state: AppState) => void) => PluginListenerHandle
```

Listen for changes in the App's active state (whether the app is in the foreground or background)

| Param            | Type                      |
| ---------------- | ------------------------- |
| **eventName**    | "appStateChange"          |
| **listenerFunc** | (state: AppState) => void |

**Returns:** [PluginListenerHandle](#pluginlistenerhandle)

--------------------


### addListener

```typescript
addListener(eventName: 'appUrlOpen', listenerFunc: (data: AppUrlOpen) => void) => PluginListenerHandle
```

Listen for url open events for the app. This handles both custom URL scheme links as well
as URLs your app handles (Universal Links on iOS and App Links on Android)

| Param            | Type                       |
| ---------------- | -------------------------- |
| **eventName**    | "appUrlOpen"               |
| **listenerFunc** | (data: AppUrlOpen) => void |

**Returns:** [PluginListenerHandle](#pluginlistenerhandle)

--------------------


### addListener

```typescript
addListener(eventName: 'appRestoredResult', listenerFunc: (data: AppRestoredResult) => void) => PluginListenerHandle
```

If the app was launched with previously persisted plugin call data, such as on Android
when an activity returns to an app that was closed, this call will return any data
the app was launched with, converted into the form of a result from a plugin call.

| Param            | Type                              |
| ---------------- | --------------------------------- |
| **eventName**    | "appRestoredResult"               |
| **listenerFunc** | (data: AppRestoredResult) => void |

**Returns:** [PluginListenerHandle](#pluginlistenerhandle)

--------------------


### addListener

```typescript
addListener(eventName: 'backButton', listenerFunc: () => void) => PluginListenerHandle
```

Listen for the hardware back button event (Android only). Listening for this event will disable the
default back button behaviour, so you might want to call `window.history.back()` manually.
If you want to close the app, call `App.exitApp()`.

| Param            | Type         |
| ---------------- | ------------ |
| **eventName**    | "backButton" |
| **listenerFunc** | () => void   |

**Returns:** [PluginListenerHandle](#pluginlistenerhandle)

--------------------


### removeAllListeners

```typescript
removeAllListeners() => void
```

Remove all native listeners for this plugin

**Returns:** void

--------------------


### Interfaces


#### AppInfo

| Prop        | Type   |
| ----------- | ------ |
| **name**    | string |
| **id**      | string |
| **build**   | string |
| **version** | string |


#### AppState

| Prop         | Type    |
| ------------ | ------- |
| **isActive** | boolean |


#### AppLaunchUrl

| Prop    | Type   |
| ------- | ------ |
| **url** | string |


#### PluginListenerHandle

| Prop       | Type       |
| ---------- | ---------- |
| **remove** | () => void |


#### AppUrlOpen

| Prop                     | Type    | Description                                                                                                                                                                        |
| ------------------------ | ------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **url**                  | string  | The URL the app was opened with                                                                                                                                                    |
| **iosSourceApplication** | any     | The source application opening the app (iOS only) https://developer.apple.com/documentation/uikit/uiapplicationopenurloptionskey/1623128-sourceapplication                         |
| **iosOpenInPlace**       | boolean | Whether the app should open the passed document in-place or must copy it first. https://developer.apple.com/documentation/uikit/uiapplicationopenurloptionskey/1623123-openinplace |


#### AppRestoredResult

| Prop           | Type                 | Description                                                                                                                                       |
| -------------- | -------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------- |
| **pluginId**   | string               | The pluginId this result corresponds to. For example, `Camera`.                                                                                   |
| **methodName** | string               | The methodName this result corresponds to. For example, `getPhoto`                                                                                |
| **data**       | any                  | The result data passed from the plugin. This would be the result you'd expect from normally calling the plugin method. For example, `CameraPhoto` |
| **success**    | boolean              | Boolean indicating if the plugin call succeeded                                                                                                   |
| **error**      | { message: string; } | If the plugin call didn't succeed, it will contain the error message                                                                              |


<!--DOCGEN_API_END-->