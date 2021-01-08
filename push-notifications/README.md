# @capacitor/push-notifications

The Push Notifications API provides access to native push notifications.

## Install

```bash
npm install @capacitor/push-notifications
npx cap sync
```

## iOS

On iOS you must enable the Push Notifications capability. See [Setting Capabilities](https://capacitorjs.com/docs/v3/ios/configuration#setting-capabilities) for instructions on how to enable the capability.

The Push Notification API uses [Firebase Cloud Messaging](https://firebase.google.com/docs/cloud-messaging) SDK for handling notifications.  See [Set up a Firebase Cloud Messaging client app on iOS](https://firebase.google.com/docs/cloud-messaging/ios/client) and follow the instructions for creating a Firebase project and registering your application.  Do not add the Firebase SDK to your app - the Push Notifications provides that for you.  All that is required is your Firebase project `GoogleService-Info.plist` file added to your Xcode project.

After setting up your Firebase project, add the following to your application AppDelegate.swift

```swift
func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
  NotificationCenter.default.post(name: .capacitorDidRegisterForRemoteNotifications, object: deviceToken)
}

func application(_ application: UIApplication, didFailToRegisterForRemoteNotificationsWithError error: Error) {
  NotificationCenter.default.post(name: .capacitorDidFailToRegisterForRemoteNotifications, object: error)
}
```

## Android

The Push Notification API uses [Firebase Cloud Messaging](https://firebase.google.com/docs/cloud-messaging) SDK for handling notifications.  See [Set up a Firebase Cloud Messaging client app on Android](https://firebase.google.com/docs/cloud-messaging/android/client) and follow the instructions for creating a Firebase project and registering your application.  There is no need to add the Firebase SDK to your app or edit your app manifest - the Push Notifications provides that for you.  All that is required is your Firebase project's `google-services.json` file added to the module (app-level) directory of your app.

---

## Push Notifications icon

On Android, the Push Notifications icon with the appropriate name should be added to the `AndroidManifest.xml` file:

```xml
<meta-data android:name="com.google.firebase.messaging.default_notification_icon" android:resource="@mipmap/push_icon_name" />
```

If no icon is specified Android will use the application icon, but push icon should be white pixels on a transparent backdrop. As the application icon is not usually like that, it will show a white square or circle. So it's recommended to provide the separate icon for Push Notifications.

Android Studio has an icon generator you can use to create your Push Notifications icon.

## Push notifications appearance in foreground

On iOS you can configure the way the push notifications are displayed when the app is in foreground by providing the `presentationOptions` in your `capacitor.config.json` as an Array of Strings you can combine.

Possible values are:
* `badge`: badge count on the app icon is updated (default value)
* `sound`: the device will ring/vibrate when the push notification is received
* `alert`: the push notification is displayed in a native dialog

An empty Array can be provided if none of the previous options are desired. `pushNotificationReceived` event will still be fired with the push notification information.

```json
"plugins": {
  "PushNotifications": {
    "presentationOptions": ["badge", "sound", "alert"]
  }
}
```

---

## API

<docgen-index>

* [`register()`](#register)
* [`getDeliveredNotifications()`](#getdeliverednotifications)
* [`removeDeliveredNotifications(...)`](#removedeliverednotifications)
* [`removeAllDeliveredNotifications()`](#removealldeliverednotifications)
* [`createChannel(...)`](#createchannel)
* [`deleteChannel(...)`](#deletechannel)
* [`listChannels()`](#listchannels)
* [`checkPermissions()`](#checkpermissions)
* [`requestPermissions()`](#requestpermissions)
* [`addListener(...)`](#addlistener)
* [`addListener(...)`](#addlistener)
* [`addListener(...)`](#addlistener)
* [`addListener(...)`](#addlistener)
* [`removeAllListeners()`](#removealllisteners)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### register()

```typescript
register() => Promise<void>
```

Register the app to receive push notifications.

Will trigger registration event with the push token or registrationError if there was some problem.

Doesn't prompt the user for notification permissions, use requestPermission() first.

**Since:** 1.0.0

--------------------


### getDeliveredNotifications()

```typescript
getDeliveredNotifications() => Promise<PushNotificationDeliveredList>
```

Returns the notifications that are visible on the notifications screen.

**Returns:** <code>Promise&lt;<a href="#pushnotificationdeliveredlist">PushNotificationDeliveredList</a>&gt;</code>

**Since:** 1.0.0

--------------------


### removeDeliveredNotifications(...)

```typescript
removeDeliveredNotifications(delivered: PushNotificationDeliveredList) => Promise<void>
```

Removes the specified notifications from the notifications screen.

| Param           | Type                                                                                    | Description                      |
| --------------- | --------------------------------------------------------------------------------------- | -------------------------------- |
| **`delivered`** | <code><a href="#pushnotificationdeliveredlist">PushNotificationDeliveredList</a></code> | list of delivered notifications. |

**Since:** 1.0.0

--------------------


### removeAllDeliveredNotifications()

```typescript
removeAllDeliveredNotifications() => Promise<void>
```

Removes all the notifications from the notifications screen.

**Since:** 1.0.0

--------------------


### createChannel(...)

```typescript
createChannel(channel: Channel) => Promise<void>
```

On Android O or newer (SDK 26+) creates a notification channel.

| Param         | Type                                        | Description |
| ------------- | ------------------------------------------- | ----------- |
| **`channel`** | <code><a href="#channel">Channel</a></code> | to create.  |

**Since:** 1.0.0

--------------------


### deleteChannel(...)

```typescript
deleteChannel(channel: Channel) => Promise<void>
```

On Android O or newer (SDK 26+) deletes a notification channel.

| Param         | Type                                        | Description |
| ------------- | ------------------------------------------- | ----------- |
| **`channel`** | <code><a href="#channel">Channel</a></code> | to delete.  |

**Since:** 1.0.0

--------------------


### listChannels()

```typescript
listChannels() => Promise<ListChannelsResult>
```

On Android O or newer (SDK 26+) list the available notification channels.

**Returns:** <code>Promise&lt;<a href="#listchannelsresult">ListChannelsResult</a>&gt;</code>

**Since:** 1.0.0

--------------------


### checkPermissions()

```typescript
checkPermissions() => Promise<PermissionStatus>
```

Check permission to receive push notifications.

**Returns:** <code>Promise&lt;<a href="#permissionstatus">PermissionStatus</a>&gt;</code>

**Since:** 1.0.0

--------------------


### requestPermissions()

```typescript
requestPermissions() => Promise<PermissionStatus>
```

Request permission to receive push notifications.

**Returns:** <code>Promise&lt;<a href="#permissionstatus">PermissionStatus</a>&gt;</code>

**Since:** 1.0.0

--------------------


### addListener(...)

```typescript
addListener(eventName: 'registration', listenerFunc: (token: PushNotificationToken) => void) => PluginListenerHandle
```

Event called when the push notification registration finished without problems.
Provides the push notification token.

| Param              | Type                                                                                        | Description                   |
| ------------------ | ------------------------------------------------------------------------------------------- | ----------------------------- |
| **`eventName`**    | <code>"registration"</code>                                                                 | registration.                 |
| **`listenerFunc`** | <code>(token: <a href="#pushnotificationtoken">PushNotificationToken</a>) =&gt; void</code> | callback with the push token. |

**Returns:** <code><a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

**Since:** 1.0.0

--------------------


### addListener(...)

```typescript
addListener(eventName: 'registrationError', listenerFunc: (error: any) => void) => PluginListenerHandle
```

Event called when the push notification registration finished with problems.
Provides an error with the registration problem.

| Param              | Type                                 | Description                           |
| ------------------ | ------------------------------------ | ------------------------------------- |
| **`eventName`**    | <code>"registrationError"</code>     | registrationError.                    |
| **`listenerFunc`** | <code>(error: any) =&gt; void</code> | callback with the registration error. |

**Returns:** <code><a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

**Since:** 1.0.0

--------------------


### addListener(...)

```typescript
addListener(eventName: 'pushNotificationReceived', listenerFunc: (notification: PushNotificationSchema) => void) => PluginListenerHandle
```

Event called when the device receives a push notification.

| Param              | Type                                                                                                 | Description                              |
| ------------------ | ---------------------------------------------------------------------------------------------------- | ---------------------------------------- |
| **`eventName`**    | <code>"pushNotificationReceived"</code>                                                              | pushNotificationReceived.                |
| **`listenerFunc`** | <code>(notification: <a href="#pushnotificationschema">PushNotificationSchema</a>) =&gt; void</code> | callback with the received notification. |

**Returns:** <code><a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

**Since:** 1.0.0

--------------------


### addListener(...)

```typescript
addListener(eventName: 'pushNotificationActionPerformed', listenerFunc: (notification: PushNotificationActionPerformed) => void) => PluginListenerHandle
```

Event called when an action is performed on a pusn notification.

| Param              | Type                                                                                                                   | Description                            |
| ------------------ | ---------------------------------------------------------------------------------------------------------------------- | -------------------------------------- |
| **`eventName`**    | <code>"pushNotificationActionPerformed"</code>                                                                         | pushNotificationActionPerformed.       |
| **`listenerFunc`** | <code>(notification: <a href="#pushnotificationactionperformed">PushNotificationActionPerformed</a>) =&gt; void</code> | callback with the notification action. |

**Returns:** <code><a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

**Since:** 1.0.0

--------------------


### removeAllListeners()

```typescript
removeAllListeners() => void
```

Remove all native listeners for this plugin.

**Since:** 1.0.0

--------------------


### Interfaces


#### PushNotificationDeliveredList

| Prop                | Type                                  | Since |
| ------------------- | ------------------------------------- | ----- |
| **`notifications`** | <code>PushNotificationSchema[]</code> | 1.0.0 |


#### PushNotificationSchema

| Prop               | Type                 | Description                                                                                                      | Since |
| ------------------ | -------------------- | ---------------------------------------------------------------------------------------------------------------- | ----- |
| **`title`**        | <code>string</code>  | The notification title                                                                                           | 1.0.0 |
| **`subtitle`**     | <code>string</code>  | The notification sub title                                                                                       | 1.0.0 |
| **`body`**         | <code>string</code>  | The main text payload for the notification                                                                       | 1.0.0 |
| **`id`**           | <code>string</code>  | The notification identifier                                                                                      | 1.0.0 |
| **`badge`**        | <code>number</code>  | The number to display for the app icon badge                                                                     | 1.0.0 |
| **`notification`** | <code>any</code>     |                                                                                                                  | 1.0.0 |
| **`data`**         | <code>any</code>     | The notification identifier                                                                                      | 1.0.0 |
| **`click_action`** | <code>string</code>  |                                                                                                                  | 1.0.0 |
| **`link`**         | <code>string</code>  |                                                                                                                  | 1.0.0 |
| **`group`**        | <code>string</code>  | Android only: set the group identifier for notification grouping, like threadIdentifier on iOS.                  | 1.0.0 |
| **`groupSummary`** | <code>boolean</code> | Android only: designate this notification as the summary for a group (should be used with the `group` property). | 1.0.0 |


#### Channel

| Prop              | Type                               | Description                                                                                                                                                                                                                                                                                                                                                                                                                             | Since |
| ----------------- | ---------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ----- |
| **`id`**          | <code>string</code>                | Android only: The channel identifier                                                                                                                                                                                                                                                                                                                                                                                                    | 1.0.0 |
| **`name`**        | <code>string</code>                | Android only: Sets the user visible name of this channel.                                                                                                                                                                                                                                                                                                                                                                               | 1.0.0 |
| **`description`** | <code>string</code>                | Android only: Sets the user visible description of this channel.                                                                                                                                                                                                                                                                                                                                                                        | 1.0.0 |
| **`sound`**       | <code>string</code>                | Android only: Sets the sound that should be played for notifications posted to this channel. Notification channels with an importance of at least 3 should have a sound. Should specifify the file name of a sound file relative to the android app res/raw directory.                                                                                                                                                                  | 1.0.0 |
| **`importance`**  | <code>1 \| 2 \| 5 \| 4 \| 3</code> | Android only: Sets the level of interruption of this notification channel.                                                                                                                                                                                                                                                                                                                                                              | 1.0.0 |
| **`visibility`**  | <code>0 \| 1 \| -1</code>          | Android only: Sets whether notifications posted to this channel appear on the lockscreen or not, and if so, whether they appear in a redacted form.                                                                                                                                                                                                                                                                                     | 1.0.0 |
| **`lights`**      | <code>boolean</code>               | Android only: Sets whether notifications posted to this channel should display notification lights, on devices that support that feature.                                                                                                                                                                                                                                                                                               | 1.0.0 |
| **`lightColor`**  | <code>string</code>                | Android only: Sets the notification light color for notifications posted to this channel, if lights are enabled on this channel and the device supports that feature. Supported color formats: #RRGGBB #RRGGBBAA The following names are also accepted: red, blue, green, black, white, gray, cyan, magenta, yellow, lightgray, darkgray, grey, lightgrey, darkgrey, aqua, fuchsia, lime, maroon, navy, olive, purple, silver, and teal | 1.0.0 |
| **`vibration`**   | <code>boolean</code>               | Android only: Sets whether notification posted to this channel should vibrate.                                                                                                                                                                                                                                                                                                                                                          | 1.0.0 |


#### ListChannelsResult

| Prop           | Type                   | Since |
| -------------- | ---------------------- | ----- |
| **`channels`** | <code>Channel[]</code> | 1.0.0 |


#### PermissionStatus

| Prop          | Type                                                                      | Since |
| ------------- | ------------------------------------------------------------------------- | ----- |
| **`receive`** | <code>"prompt" \| "prompt-with-rationale" \| "granted" \| "denied"</code> | 1.0.0 |


#### PluginListenerHandle

| Prop         | Type                       |
| ------------ | -------------------------- |
| **`remove`** | <code>() =&gt; void</code> |


#### PushNotificationToken

| Prop        | Type                | Since |
| ----------- | ------------------- | ----- |
| **`value`** | <code>string</code> | 1.0.0 |


#### PushNotificationActionPerformed

| Prop               | Type                                                                      | Since |
| ------------------ | ------------------------------------------------------------------------- | ----- |
| **`actionId`**     | <code>string</code>                                                       | 1.0.0 |
| **`inputValue`**   | <code>string</code>                                                       | 1.0.0 |
| **`notification`** | <code><a href="#pushnotificationschema">PushNotificationSchema</a></code> | 1.0.0 |

</docgen-api>
