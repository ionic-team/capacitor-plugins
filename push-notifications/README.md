# @capacitor/push-notifications

The Push Notifications API provides access to native push notifications.

## Install

```bash
npm install @capacitor/push-notifications
npx cap sync
```

## iOS

On iOS you must enable the Push Notifications capability. See [Setting Capabilities](https://capacitorjs.com/docs/v3/ios/configuration#setting-capabilities) for instructions on how to enable the capability.

After enabling the Push Notifications capability, add the following to your app's `AppDelegate.swift`:

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

Android 13 requires a permission check in order to receive push notifications.  You are required to call `checkPermissions()` and `requestPermissions()` accordingly, when targeting SDK 33.

### Variables

This plugin will use the following project variables (defined in your app's `variables.gradle` file):

- `$firebaseMessagingVersion` version of `com.google.firebase:firebase-messaging` (default: `23.1.2`)

---

## Push Notifications icon

On Android, the Push Notifications icon with the appropriate name should be added to the `AndroidManifest.xml` file:

```xml
<meta-data android:name="com.google.firebase.messaging.default_notification_icon" android:resource="@mipmap/push_icon_name" />
```

If no icon is specified Android will use the application icon, but push icon should be white pixels on a transparent backdrop. As the application icon is not usually like that, it will show a white square or circle. So it's recommended to provide the separate icon for Push Notifications.

Android Studio has an icon generator you can use to create your Push Notifications icon.

## Push notifications appearance in foreground

<docgen-config>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

You can configure the way the push notifications are displayed when the app is in foreground.

| Prop                      | Type                              | Description                                                                                                                                                                                                                                                                                                                                                                                          | Since |
| ------------------------- | --------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ----- |
| **`presentationOptions`** | <code>PresentationOption[]</code> | This is an array of strings you can combine. Possible values in the array are: - `badge`: badge count on the app icon is updated (default value) - `sound`: the device will ring/vibrate when the push notification is received - `alert`: the push notification is displayed in a native dialog An empty array can be provided if none of the options are desired. badge is only available for iOS. | 1.0.0 |

### Examples

In `capacitor.config.json`:

```json
{
  "plugins": {
    "PushNotifications": {
      "presentationOptions": ["badge", "sound", "alert"]
    }
  }
}
```

In `capacitor.config.ts`:

```ts
/// <reference types="@capacitor/push-notifications" />

import { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  plugins: {
    PushNotifications: {
      presentationOptions: ["badge", "sound", "alert"],
    },
  },
};

export default config;
```

</docgen-config>

## Silent Push Notifications / Data-only Notifications
#### iOS
This plugin does not support iOS Silent Push (Remote Notifications). We recommend using native code solutions for handling these types of notifications, see [Pushing Background Updates to Your App](https://developer.apple.com/documentation/usernotifications/setting_up_a_remote_notification_server/pushing_background_updates_to_your_app).

#### Android
This plugin does support data-only notifications, but will NOT call `pushNotificationReceived` if the app has been killed. To handle this scenario, you will need to create a service that extends `FirebaseMessagingService`, see [Handling FCM Messages](https://firebase.google.com/docs/cloud-messaging/android/receive). 

## Common Issues
On Android, there are various system and app states that can affect the delivery of push notifications:

* If the device has entered [Doze](https://developer.android.com/training/monitoring-device-state/doze-standby) mode, your application may have restricted capabilities. To increase the chance of your notification being received, consider using [FCM high priority messages](https://firebase.google.com/docs/cloud-messaging/concept-options#setting-the-priority-of-a-message).
* There are differences in behavior between development and production. Try testing your app outside of being launched by Android Studio. Read more [here](https://stackoverflow.com/a/50238790/1351469).

---

## Example

```typescript
import { PushNotifications } from '@capacitor/push-notifications';

const addListeners = async () => {
  await PushNotifications.addListener('registration', token => {
    console.info('Registration token: ', token.value);
  });

  await PushNotifications.addListener('registrationError', err => {
    console.error('Registration error: ', err.error);
  });

  await PushNotifications.addListener('pushNotificationReceived', notification => {
    console.log('Push notification received: ', notification);
  });

  await PushNotifications.addListener('pushNotificationActionPerformed', notification => {
    console.log('Push notification action performed', notification.actionId, notification.inputValue);
  });
}

const registerNotifications = async () => {
  let permStatus = await PushNotifications.checkPermissions();

  if (permStatus.receive === 'prompt') {
    permStatus = await PushNotifications.requestPermissions();
  }

  if (permStatus.receive !== 'granted') {
    throw new Error('User denied permissions!');
  }

  await PushNotifications.register();
}

const getDeliveredNotifications = async () => {
  const notificationList = await PushNotifications.getDeliveredNotifications();
  console.log('delivered notifications', notificationList);
}
```

## API

<docgen-index>

* [`register()`](#register)
* [`unregister()`](#unregister)
* [`getDeliveredNotifications()`](#getdeliverednotifications)
* [`removeDeliveredNotifications(...)`](#removedeliverednotifications)
* [`removeAllDeliveredNotifications()`](#removealldeliverednotifications)
* [`createChannel(...)`](#createchannel)
* [`deleteChannel(...)`](#deletechannel)
* [`listChannels()`](#listchannels)
* [`checkPermissions()`](#checkpermissions)
* [`requestPermissions()`](#requestpermissions)
* [`addListener('registration', ...)`](#addlistenerregistration)
* [`addListener('registrationError', ...)`](#addlistenerregistrationerror)
* [`addListener('pushNotificationReceived', ...)`](#addlistenerpushnotificationreceived)
* [`addListener('pushNotificationActionPerformed', ...)`](#addlistenerpushnotificationactionperformed)
* [`removeAllListeners()`](#removealllisteners)
* [Interfaces](#interfaces)
* [Type Aliases](#type-aliases)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### register()

```typescript
register() => Promise<void>
```

Register the app to receive push notifications.

This method will trigger the `'registration'` event with the push token or
`'registrationError'` if there was a problem. It does not prompt the user for
notification permissions, use `requestPermissions()` first.

**Since:** 1.0.0

--------------------


### unregister()

```typescript
unregister() => Promise<void>
```

Unregister the app from push notifications.

This will delete a firebase token on Android, and unregister APNS on iOS.

**Since:** 5.0.0

--------------------


### getDeliveredNotifications()

```typescript
getDeliveredNotifications() => Promise<DeliveredNotifications>
```

Get a list of notifications that are visible on the notifications screen.

**Returns:** <code>Promise&lt;<a href="#deliverednotifications">DeliveredNotifications</a>&gt;</code>

**Since:** 1.0.0

--------------------


### removeDeliveredNotifications(...)

```typescript
removeDeliveredNotifications(delivered: DeliveredNotifications) => Promise<void>
```

Remove the specified notifications from the notifications screen.

| Param           | Type                                                                      |
| --------------- | ------------------------------------------------------------------------- |
| **`delivered`** | <code><a href="#deliverednotifications">DeliveredNotifications</a></code> |

**Since:** 1.0.0

--------------------


### removeAllDeliveredNotifications()

```typescript
removeAllDeliveredNotifications() => Promise<void>
```

Remove all the notifications from the notifications screen.

**Since:** 1.0.0

--------------------


### createChannel(...)

```typescript
createChannel(channel: Channel) => Promise<void>
```

Create a notification channel.

Only available on Android O or newer (SDK 26+).

| Param         | Type                                        |
| ------------- | ------------------------------------------- |
| **`channel`** | <code><a href="#channel">Channel</a></code> |

**Since:** 1.0.0

--------------------


### deleteChannel(...)

```typescript
deleteChannel(args: { id: string; }) => Promise<void>
```

Delete a notification channel.

Only available on Android O or newer (SDK 26+).

| Param      | Type                         |
| ---------- | ---------------------------- |
| **`args`** | <code>{ id: string; }</code> |

**Since:** 1.0.0

--------------------


### listChannels()

```typescript
listChannels() => Promise<ListChannelsResult>
```

List the available notification channels.

Only available on Android O or newer (SDK 26+).

**Returns:** <code>Promise&lt;<a href="#listchannelsresult">ListChannelsResult</a>&gt;</code>

**Since:** 1.0.0

--------------------


### checkPermissions()

```typescript
checkPermissions() => Promise<PermissionStatus>
```

Check permission to receive push notifications.

On Android 12 and below the status is always granted because you can always
receive push notifications. If you need to check if the user allows
to display notifications, use local-notifications plugin.

**Returns:** <code>Promise&lt;<a href="#permissionstatus">PermissionStatus</a>&gt;</code>

**Since:** 1.0.0

--------------------


### requestPermissions()

```typescript
requestPermissions() => Promise<PermissionStatus>
```

Request permission to receive push notifications.

On Android 12 and below it doesn't prompt for permission because you can always
receive push notifications.

On iOS, the first time you use the function, it will prompt the user
for push notification permission and return granted or denied based
on the user selection. On following calls it will get the current status of
the permission without prompting again.

**Returns:** <code>Promise&lt;<a href="#permissionstatus">PermissionStatus</a>&gt;</code>

**Since:** 1.0.0

--------------------


### addListener('registration', ...)

```typescript
addListener(eventName: 'registration', listenerFunc: (token: Token) => void) => Promise<PluginListenerHandle> & PluginListenerHandle
```

Called when the push notification registration finishes without problems.

Provides the push notification token.

| Param              | Type                                                        |
| ------------------ | ----------------------------------------------------------- |
| **`eventName`**    | <code>'registration'</code>                                 |
| **`listenerFunc`** | <code>(token: <a href="#token">Token</a>) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt; & <a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

**Since:** 1.0.0

--------------------


### addListener('registrationError', ...)

```typescript
addListener(eventName: 'registrationError', listenerFunc: (error: RegistrationError) => void) => Promise<PluginListenerHandle> & PluginListenerHandle
```

Called when the push notification registration finished with problems.

Provides an error with the registration problem.

| Param              | Type                                                                                |
| ------------------ | ----------------------------------------------------------------------------------- |
| **`eventName`**    | <code>'registrationError'</code>                                                    |
| **`listenerFunc`** | <code>(error: <a href="#registrationerror">RegistrationError</a>) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt; & <a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

**Since:** 1.0.0

--------------------


### addListener('pushNotificationReceived', ...)

```typescript
addListener(eventName: 'pushNotificationReceived', listenerFunc: (notification: PushNotificationSchema) => void) => Promise<PluginListenerHandle> & PluginListenerHandle
```

Called when the device receives a push notification.

| Param              | Type                                                                                                 |
| ------------------ | ---------------------------------------------------------------------------------------------------- |
| **`eventName`**    | <code>'pushNotificationReceived'</code>                                                              |
| **`listenerFunc`** | <code>(notification: <a href="#pushnotificationschema">PushNotificationSchema</a>) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt; & <a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

**Since:** 1.0.0

--------------------


### addListener('pushNotificationActionPerformed', ...)

```typescript
addListener(eventName: 'pushNotificationActionPerformed', listenerFunc: (notification: ActionPerformed) => void) => Promise<PluginListenerHandle> & PluginListenerHandle
```

Called when an action is performed on a push notification.

| Param              | Type                                                                                   |
| ------------------ | -------------------------------------------------------------------------------------- |
| **`eventName`**    | <code>'pushNotificationActionPerformed'</code>                                         |
| **`listenerFunc`** | <code>(notification: <a href="#actionperformed">ActionPerformed</a>) =&gt; void</code> |

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


#### DeliveredNotifications

| Prop                | Type                                  | Description                                                         | Since |
| ------------------- | ------------------------------------- | ------------------------------------------------------------------- | ----- |
| **`notifications`** | <code>PushNotificationSchema[]</code> | List of notifications that are visible on the notifications screen. | 1.0.0 |


#### PushNotificationSchema

| Prop               | Type                 | Description                                                                                                          | Since |
| ------------------ | -------------------- | -------------------------------------------------------------------------------------------------------------------- | ----- |
| **`title`**        | <code>string</code>  | The notification title.                                                                                              | 1.0.0 |
| **`subtitle`**     | <code>string</code>  | The notification subtitle.                                                                                           | 1.0.0 |
| **`body`**         | <code>string</code>  | The main text payload for the notification.                                                                          | 1.0.0 |
| **`id`**           | <code>string</code>  | The notification identifier.                                                                                         | 1.0.0 |
| **`tag`**          | <code>string</code>  | The notification tag. Only available on Android (from push notifications).                                           | 4.0.0 |
| **`badge`**        | <code>number</code>  | The number to display for the app icon badge.                                                                        | 1.0.0 |
| **`notification`** | <code>any</code>     | It's not being returned.                                                                                             | 1.0.0 |
| **`data`**         | <code>any</code>     | Any additional data that was included in the push notification payload.                                              | 1.0.0 |
| **`click_action`** | <code>string</code>  | The action to be performed on the user opening the notification. Only available on Android.                          | 1.0.0 |
| **`link`**         | <code>string</code>  | Deep link from the notification. Only available on Android.                                                          | 1.0.0 |
| **`group`**        | <code>string</code>  | Set the group identifier for notification grouping. Only available on Android. Works like `threadIdentifier` on iOS. | 1.0.0 |
| **`groupSummary`** | <code>boolean</code> | Designate this notification as the summary for an associated `group`. Only available on Android.                     | 1.0.0 |


#### Channel

| Prop              | Type                                              | Description                                                                                                                                                                                                                                                | Default          | Since |
| ----------------- | ------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ---------------- | ----- |
| **`id`**          | <code>string</code>                               | The channel identifier.                                                                                                                                                                                                                                    |                  | 1.0.0 |
| **`name`**        | <code>string</code>                               | The human-friendly name of this channel (presented to the user).                                                                                                                                                                                           |                  | 1.0.0 |
| **`description`** | <code>string</code>                               | The description of this channel (presented to the user).                                                                                                                                                                                                   |                  | 1.0.0 |
| **`sound`**       | <code>string</code>                               | The sound that should be played for notifications posted to this channel. Notification channels with an importance of at least `3` should have a sound. The file name of a sound file should be specified relative to the android app `res/raw` directory. |                  | 1.0.0 |
| **`importance`**  | <code><a href="#importance">Importance</a></code> | The level of interruption for notifications posted to this channel.                                                                                                                                                                                        | <code>`3`</code> | 1.0.0 |
| **`visibility`**  | <code><a href="#visibility">Visibility</a></code> | The visibility of notifications posted to this channel. This setting is for whether notifications posted to this channel appear on the lockscreen or not, and if so, whether they appear in a redacted form.                                               |                  | 1.0.0 |
| **`lights`**      | <code>boolean</code>                              | Whether notifications posted to this channel should display notification lights, on devices that support it.                                                                                                                                               |                  | 1.0.0 |
| **`lightColor`**  | <code>string</code>                               | The light color for notifications posted to this channel. Only supported if lights are enabled on this channel and the device supports it. Supported color formats are `#RRGGBB` and `#RRGGBBAA`.                                                          |                  | 1.0.0 |
| **`vibration`**   | <code>boolean</code>                              | Whether notifications posted to this channel should vibrate.                                                                                                                                                                                               |                  | 1.0.0 |


#### ListChannelsResult

| Prop           | Type                   | Description                                   | Since |
| -------------- | ---------------------- | --------------------------------------------- | ----- |
| **`channels`** | <code>Channel[]</code> | List of all the Channels created by your app. | 1.0.0 |


#### PermissionStatus

| Prop          | Type                                                        | Description                                  | Since |
| ------------- | ----------------------------------------------------------- | -------------------------------------------- | ----- |
| **`receive`** | <code><a href="#permissionstate">PermissionState</a></code> | Permission state of receiving notifications. | 1.0.0 |


#### PluginListenerHandle

| Prop         | Type                                      |
| ------------ | ----------------------------------------- |
| **`remove`** | <code>() =&gt; Promise&lt;void&gt;</code> |


#### Token

| Prop        | Type                | Description                                                              | Since |
| ----------- | ------------------- | ------------------------------------------------------------------------ | ----- |
| **`value`** | <code>string</code> | On iOS it contains the APNS token. On Android it contains the FCM token. | 1.0.0 |


#### RegistrationError

| Prop        | Type                | Description                                        | Since |
| ----------- | ------------------- | -------------------------------------------------- | ----- |
| **`error`** | <code>string</code> | Error message describing the registration failure. | 4.0.0 |


#### ActionPerformed

| Prop               | Type                                                                      | Description                                                     | Since |
| ------------------ | ------------------------------------------------------------------------- | --------------------------------------------------------------- | ----- |
| **`actionId`**     | <code>string</code>                                                       | The action performed on the notification.                       | 1.0.0 |
| **`inputValue`**   | <code>string</code>                                                       | Text entered on the notification action. Only available on iOS. | 1.0.0 |
| **`notification`** | <code><a href="#pushnotificationschema">PushNotificationSchema</a></code> | The notification in which the action was performed.             | 1.0.0 |


### Type Aliases


#### Importance

The importance level. For more details, see the [Android Developer Docs](https://developer.android.com/reference/android/app/NotificationManager#IMPORTANCE_DEFAULT)

<code>1 | 2 | 3 | 4 | 5</code>


#### Visibility

The notification visibility. For more details, see the [Android Developer Docs](https://developer.android.com/reference/androidx/core/app/NotificationCompat#VISIBILITY_PRIVATE)

<code>-1 | 0 | 1</code>


#### PermissionState

<code>'prompt' | 'prompt-with-rationale' | 'granted' | 'denied'</code>

</docgen-api>
