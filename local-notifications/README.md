# @capacitor/local-notifications

The Local Notifications API provides a way to schedule device notifications locally (i.e. without a server sending push notifications).

## Install

```bash
npm install @capacitor/local-notifications
npx cap sync
```

## Android
Android 13 requires a permission check in order to send notifications.  You are required to call `checkPermissions()` and `requestPermissions()` accordingly.

On Android 12 and older it won't show a prompt and will just return as granted.

Starting on Android 12, scheduled notifications won't be exact unless this permission is added to your `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
```

Note that even if the permission is present, users can still disable exact notifications from the app settings.

## Configuration

<docgen-config>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

On Android, the Local Notifications can be configured with the following options:

| Prop            | Type                | Description                                                                                                                                                                                                                                                                                                                  | Since |
| --------------- | ------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ----- |
| **`smallIcon`** | <code>string</code> | Set the default status bar icon for notifications. Icons should be placed in your app's `res/drawable` folder. The value for this option should be the drawable resource ID, which is the filename without an extension. Only available for Android.                                                                         | 1.0.0 |
| **`iconColor`** | <code>string</code> | Set the default color of status bar icons for notifications. Only available for Android.                                                                                                                                                                                                                                     | 1.0.0 |
| **`sound`**     | <code>string</code> | Set the default notification sound for notifications. On Android 26+ it sets the default channel sound and can't be changed unless the app is uninstalled. If the audio file is not found, it will result in the default system sound being played on Android 21-25 and no sound on Android 26+. Only available for Android. | 1.0.0 |

### Examples

In `capacitor.config.json`:

```json
{
  "plugins": {
    "LocalNotifications": {
      "smallIcon": "ic_stat_icon_config_sample",
      "iconColor": "#488AFF",
      "sound": "beep.wav"
    }
  }
}
```

In `capacitor.config.ts`:

```ts
/// <reference types="@capacitor/local-notifications" />

import { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  plugins: {
    LocalNotifications: {
      smallIcon: "ic_stat_icon_config_sample",
      iconColor: "#488AFF",
      sound: "beep.wav",
    },
  },
};

export default config;
```

</docgen-config>

## Doze

If the device has entered [Doze](https://developer.android.com/training/monitoring-device-state/doze-standby) mode, your application may have restricted capabilities. If you need your notification to fire even during Doze, schedule your notification by using `allowWhileIdle: true`. Make use of `allowWhileIdle` judiciously, as these notifications [can only fire once per 9 minutes, per app.](https://developer.android.com/training/monitoring-device-state/doze-standby#assessing_your_app)

## API

<docgen-index>

* [`schedule(...)`](#schedule)
* [`getPending()`](#getpending)
* [`registerActionTypes(...)`](#registeractiontypes)
* [`cancel(...)`](#cancel)
* [`areEnabled()`](#areenabled)
* [`getDeliveredNotifications()`](#getdeliverednotifications)
* [`removeDeliveredNotifications(...)`](#removedeliverednotifications)
* [`removeAllDeliveredNotifications()`](#removealldeliverednotifications)
* [`createChannel(...)`](#createchannel)
* [`deleteChannel(...)`](#deletechannel)
* [`listChannels()`](#listchannels)
* [`checkPermissions()`](#checkpermissions)
* [`requestPermissions()`](#requestpermissions)
* [`addListener('localNotificationReceived', ...)`](#addlistenerlocalnotificationreceived)
* [`addListener('localNotificationActionPerformed', ...)`](#addlistenerlocalnotificationactionperformed)
* [`removeAllListeners()`](#removealllisteners)
* [Interfaces](#interfaces)
* [Type Aliases](#type-aliases)
* [Enums](#enums)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### schedule(...)

```typescript
schedule(options: ScheduleOptions) => Promise<ScheduleResult>
```

<a href="#schedule">Schedule</a> one or more local notifications.

| Param         | Type                                                        |
| ------------- | ----------------------------------------------------------- |
| **`options`** | <code><a href="#scheduleoptions">ScheduleOptions</a></code> |

**Returns:** <code>Promise&lt;<a href="#scheduleresult">ScheduleResult</a>&gt;</code>

**Since:** 1.0.0

--------------------


### getPending()

```typescript
getPending() => Promise<PendingResult>
```

Get a list of pending notifications.

**Returns:** <code>Promise&lt;<a href="#pendingresult">PendingResult</a>&gt;</code>

**Since:** 1.0.0

--------------------


### registerActionTypes(...)

```typescript
registerActionTypes(options: RegisterActionTypesOptions) => Promise<void>
```

Register actions to take when notifications are displayed.

Only available for iOS and Android.

| Param         | Type                                                                              |
| ------------- | --------------------------------------------------------------------------------- |
| **`options`** | <code><a href="#registeractiontypesoptions">RegisterActionTypesOptions</a></code> |

**Since:** 1.0.0

--------------------


### cancel(...)

```typescript
cancel(options: CancelOptions) => Promise<void>
```

Cancel pending notifications.

| Param         | Type                                                    |
| ------------- | ------------------------------------------------------- |
| **`options`** | <code><a href="#canceloptions">CancelOptions</a></code> |

**Since:** 1.0.0

--------------------


### areEnabled()

```typescript
areEnabled() => Promise<EnabledResult>
```

Check if notifications are enabled or not.

**Returns:** <code>Promise&lt;<a href="#enabledresult">EnabledResult</a>&gt;</code>

**Since:** 1.0.0

--------------------


### getDeliveredNotifications()

```typescript
getDeliveredNotifications() => Promise<DeliveredNotifications>
```

Get a list of notifications that are visible on the notifications screen.

**Returns:** <code>Promise&lt;<a href="#deliverednotifications">DeliveredNotifications</a>&gt;</code>

**Since:** 4.0.0

--------------------


### removeDeliveredNotifications(...)

```typescript
removeDeliveredNotifications(delivered: DeliveredNotifications) => Promise<void>
```

Remove the specified notifications from the notifications screen.

| Param           | Type                                                                      |
| --------------- | ------------------------------------------------------------------------- |
| **`delivered`** | <code><a href="#deliverednotifications">DeliveredNotifications</a></code> |

**Since:** 4.0.0

--------------------


### removeAllDeliveredNotifications()

```typescript
removeAllDeliveredNotifications() => Promise<void>
```

Remove all the notifications from the notifications screen.

**Since:** 4.0.0

--------------------


### createChannel(...)

```typescript
createChannel(channel: Channel) => Promise<void>
```

Create a notification channel.

Only available for Android.

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

Only available for Android.

| Param      | Type                         |
| ---------- | ---------------------------- |
| **`args`** | <code>{ id: string; }</code> |

**Since:** 1.0.0

--------------------


### listChannels()

```typescript
listChannels() => Promise<ListChannelsResult>
```

Get a list of notification channels.

Only available for Android.

**Returns:** <code>Promise&lt;<a href="#listchannelsresult">ListChannelsResult</a>&gt;</code>

**Since:** 1.0.0

--------------------


### checkPermissions()

```typescript
checkPermissions() => Promise<PermissionStatus>
```

Check permission to display local notifications.

**Returns:** <code>Promise&lt;<a href="#permissionstatus">PermissionStatus</a>&gt;</code>

**Since:** 1.0.0

--------------------


### requestPermissions()

```typescript
requestPermissions() => Promise<PermissionStatus>
```

Request permission to display local notifications.

**Returns:** <code>Promise&lt;<a href="#permissionstatus">PermissionStatus</a>&gt;</code>

**Since:** 1.0.0

--------------------


### addListener('localNotificationReceived', ...)

```typescript
addListener(eventName: 'localNotificationReceived', listenerFunc: (notification: LocalNotificationSchema) => void) => Promise<PluginListenerHandle> & PluginListenerHandle
```

Listen for when notifications are displayed.

| Param              | Type                                                                                                   |
| ------------------ | ------------------------------------------------------------------------------------------------------ |
| **`eventName`**    | <code>'localNotificationReceived'</code>                                                               |
| **`listenerFunc`** | <code>(notification: <a href="#localnotificationschema">LocalNotificationSchema</a>) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt; & <a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

**Since:** 1.0.0

--------------------


### addListener('localNotificationActionPerformed', ...)

```typescript
addListener(eventName: 'localNotificationActionPerformed', listenerFunc: (notificationAction: ActionPerformed) => void) => Promise<PluginListenerHandle> & PluginListenerHandle
```

Listen for when an action is performed on a notification.

| Param              | Type                                                                                         |
| ------------------ | -------------------------------------------------------------------------------------------- |
| **`eventName`**    | <code>'localNotificationActionPerformed'</code>                                              |
| **`listenerFunc`** | <code>(notificationAction: <a href="#actionperformed">ActionPerformed</a>) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt; & <a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

**Since:** 1.0.0

--------------------


### removeAllListeners()

```typescript
removeAllListeners() => Promise<void>
```

Remove all listeners for this plugin.

**Since:** 1.0.0

--------------------


### Interfaces


#### ScheduleResult

| Prop                | Type                                       | Description                          | Since |
| ------------------- | ------------------------------------------ | ------------------------------------ | ----- |
| **`notifications`** | <code>LocalNotificationDescriptor[]</code> | The list of scheduled notifications. | 1.0.0 |


#### LocalNotificationDescriptor

The object that describes a local notification.

| Prop     | Type                | Description                  | Since |
| -------- | ------------------- | ---------------------------- | ----- |
| **`id`** | <code>number</code> | The notification identifier. | 1.0.0 |


#### ScheduleOptions

| Prop                | Type                                   | Description                            | Since |
| ------------------- | -------------------------------------- | -------------------------------------- | ----- |
| **`notifications`** | <code>LocalNotificationSchema[]</code> | The list of notifications to schedule. | 1.0.0 |


#### LocalNotificationSchema

| Prop                   | Type                                          | Description                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     | Since |
| ---------------------- | --------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ----- |
| **`title`**            | <code>string</code>                           | The title of the notification.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  | 1.0.0 |
| **`body`**             | <code>string</code>                           | The body of the notification, shown below the title.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            | 1.0.0 |
| **`largeBody`**        | <code>string</code>                           | Sets a multiline text block for display in a big text notification style.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       | 1.0.0 |
| **`summaryText`**      | <code>string</code>                           | Used to set the summary text detail in inbox and big text notification styles. Only available for Android.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      | 1.0.0 |
| **`id`**               | <code>number</code>                           | The notification identifier. On Android it's a 32-bit int. So the value should be between -2147483648 and 2147483647 inclusive.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 | 1.0.0 |
| **`schedule`**         | <code><a href="#schedule">Schedule</a></code> | <a href="#schedule">Schedule</a> this notification for a later time.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            | 1.0.0 |
| **`sound`**            | <code>string</code>                           | Name of the audio file to play when this notification is displayed. Include the file extension with the filename. On iOS, the file should be in the app bundle. On Android, the file should be in res/raw folder. Recommended format is `.wav` because is supported by both iOS and Android. Only available for iOS and Android &lt; 26. For Android 26+ use channelId of a channel configured with the desired sound. If the sound file is not found, (i.e. empty string or wrong name) the default system notification sound will be used. If not provided, it will produce the default sound on Android and no sound on iOS. | 1.0.0 |
| **`smallIcon`**        | <code>string</code>                           | Set a custom status bar icon. If set, this overrides the `smallIcon` option from Capacitor configuration. Icons should be placed in your app's `res/drawable` folder. The value for this option should be the drawable resource ID, which is the filename without an extension. Only available for Android.                                                                                                                                                                                                                                                                                                                     | 1.0.0 |
| **`largeIcon`**        | <code>string</code>                           | Set a large icon for notifications. Icons should be placed in your app's `res/drawable` folder. The value for this option should be the drawable resource ID, which is the filename without an extension. Only available for Android.                                                                                                                                                                                                                                                                                                                                                                                           | 1.0.0 |
| **`iconColor`**        | <code>string</code>                           | Set the color of the notification icon. Only available for Android.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             | 1.0.0 |
| **`attachments`**      | <code>Attachment[]</code>                     | Set attachments for this notification.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          | 1.0.0 |
| **`actionTypeId`**     | <code>string</code>                           | Associate an action type with this notification.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                | 1.0.0 |
| **`extra`**            | <code>any</code>                              | Set extra data to store within this notification.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               | 1.0.0 |
| **`threadIdentifier`** | <code>string</code>                           | Used to group multiple notifications. Sets `threadIdentifier` on the [`UNMutableNotificationContent`](https://developer.apple.com/documentation/usernotifications/unmutablenotificationcontent). Only available for iOS.                                                                                                                                                                                                                                                                                                                                                                                                        | 1.0.0 |
| **`summaryArgument`**  | <code>string</code>                           | The string this notification adds to the category's summary format string. Sets `summaryArgument` on the [`UNMutableNotificationContent`](https://developer.apple.com/documentation/usernotifications/unmutablenotificationcontent). Only available for iOS.                                                                                                                                                                                                                                                                                                                                                                    | 1.0.0 |
| **`group`**            | <code>string</code>                           | Used to group multiple notifications. Calls `setGroup()` on [`NotificationCompat.Builder`](https://developer.android.com/reference/androidx/core/app/NotificationCompat.Builder) with the provided value. Only available for Android.                                                                                                                                                                                                                                                                                                                                                                                           | 1.0.0 |
| **`groupSummary`**     | <code>boolean</code>                          | If true, this notification becomes the summary for a group of notifications. Calls `setGroupSummary()` on [`NotificationCompat.Builder`](https://developer.android.com/reference/androidx/core/app/NotificationCompat.Builder) with the provided value. Only available for Android when using `group`.                                                                                                                                                                                                                                                                                                                          | 1.0.0 |
| **`channelId`**        | <code>string</code>                           | Specifies the channel the notification should be delivered on. If channel with the given name does not exist then the notification will not fire. If not provided, it will use the default channel. Calls `setChannelId()` on [`NotificationCompat.Builder`](https://developer.android.com/reference/androidx/core/app/NotificationCompat.Builder) with the provided value. Only available for Android 26+.                                                                                                                                                                                                                     | 1.0.0 |
| **`ongoing`**          | <code>boolean</code>                          | If true, the notification can't be swiped away. Calls `setOngoing()` on [`NotificationCompat.Builder`](https://developer.android.com/reference/androidx/core/app/NotificationCompat.Builder) with the provided value. Only available for Android.                                                                                                                                                                                                                                                                                                                                                                               | 1.0.0 |
| **`autoCancel`**       | <code>boolean</code>                          | If true, the notification is canceled when the user clicks on it. Calls `setAutoCancel()` on [`NotificationCompat.Builder`](https://developer.android.com/reference/androidx/core/app/NotificationCompat.Builder) with the provided value. Only available for Android.                                                                                                                                                                                                                                                                                                                                                          | 1.0.0 |
| **`inboxList`**        | <code>string[]</code>                         | Sets a list of strings for display in an inbox style notification. Up to 5 strings are allowed. Only available for Android.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     | 1.0.0 |


#### Schedule

Represents a schedule for a notification.

Use either `at`, `on`, or `every` to schedule notifications.

| Prop                 | Type                                                    | Description                                                                                                                                                                                                                                                                                                                             | Since |
| -------------------- | ------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ----- |
| **`at`**             | <code><a href="#date">Date</a></code>                   | <a href="#schedule">Schedule</a> a notification at a specific date and time.                                                                                                                                                                                                                                                            | 1.0.0 |
| **`repeats`**        | <code>boolean</code>                                    | Repeat delivery of this notification at the date and time specified by `at`. Only available for iOS and Android.                                                                                                                                                                                                                        | 1.0.0 |
| **`allowWhileIdle`** | <code>boolean</code>                                    | Allow this notification to fire while in [Doze](https://developer.android.com/training/monitoring-device-state/doze-standby) Only available for Android 23+. Note that these notifications can only fire [once per 9 minutes, per app](https://developer.android.com/training/monitoring-device-state/doze-standby#assessing_your_app). | 1.0.0 |
| **`on`**             | <code><a href="#scheduleon">ScheduleOn</a></code>       | <a href="#schedule">Schedule</a> a notification on particular interval(s). This is similar to scheduling [cron](https://en.wikipedia.org/wiki/Cron) jobs. Only available for iOS and Android.                                                                                                                                           | 1.0.0 |
| **`every`**          | <code><a href="#scheduleevery">ScheduleEvery</a></code> | <a href="#schedule">Schedule</a> a notification on a particular interval.                                                                                                                                                                                                                                                               | 1.0.0 |
| **`count`**          | <code>number</code>                                     | Limit the number times a notification is delivered by the interval specified by `every`.                                                                                                                                                                                                                                                | 1.0.0 |


#### Date

Enables basic storage and retrieval of dates and times.

| Method                 | Signature                                                                                                    | Description                                                                                                                             |
| ---------------------- | ------------------------------------------------------------------------------------------------------------ | --------------------------------------------------------------------------------------------------------------------------------------- |
| **toString**           | () =&gt; string                                                                                              | Returns a string representation of a date. The format of the string depends on the locale.                                              |
| **toDateString**       | () =&gt; string                                                                                              | Returns a date as a string value.                                                                                                       |
| **toTimeString**       | () =&gt; string                                                                                              | Returns a time as a string value.                                                                                                       |
| **toLocaleString**     | () =&gt; string                                                                                              | Returns a value as a string value appropriate to the host environment's current locale.                                                 |
| **toLocaleDateString** | () =&gt; string                                                                                              | Returns a date as a string value appropriate to the host environment's current locale.                                                  |
| **toLocaleTimeString** | () =&gt; string                                                                                              | Returns a time as a string value appropriate to the host environment's current locale.                                                  |
| **valueOf**            | () =&gt; number                                                                                              | Returns the stored time value in milliseconds since midnight, January 1, 1970 UTC.                                                      |
| **getTime**            | () =&gt; number                                                                                              | Gets the time value in milliseconds.                                                                                                    |
| **getFullYear**        | () =&gt; number                                                                                              | Gets the year, using local time.                                                                                                        |
| **getUTCFullYear**     | () =&gt; number                                                                                              | Gets the year using Universal Coordinated Time (UTC).                                                                                   |
| **getMonth**           | () =&gt; number                                                                                              | Gets the month, using local time.                                                                                                       |
| **getUTCMonth**        | () =&gt; number                                                                                              | Gets the month of a <a href="#date">Date</a> object using Universal Coordinated Time (UTC).                                             |
| **getDate**            | () =&gt; number                                                                                              | Gets the day-of-the-month, using local time.                                                                                            |
| **getUTCDate**         | () =&gt; number                                                                                              | Gets the day-of-the-month, using Universal Coordinated Time (UTC).                                                                      |
| **getDay**             | () =&gt; number                                                                                              | Gets the day of the week, using local time.                                                                                             |
| **getUTCDay**          | () =&gt; number                                                                                              | Gets the day of the week using Universal Coordinated Time (UTC).                                                                        |
| **getHours**           | () =&gt; number                                                                                              | Gets the hours in a date, using local time.                                                                                             |
| **getUTCHours**        | () =&gt; number                                                                                              | Gets the hours value in a <a href="#date">Date</a> object using Universal Coordinated Time (UTC).                                       |
| **getMinutes**         | () =&gt; number                                                                                              | Gets the minutes of a <a href="#date">Date</a> object, using local time.                                                                |
| **getUTCMinutes**      | () =&gt; number                                                                                              | Gets the minutes of a <a href="#date">Date</a> object using Universal Coordinated Time (UTC).                                           |
| **getSeconds**         | () =&gt; number                                                                                              | Gets the seconds of a <a href="#date">Date</a> object, using local time.                                                                |
| **getUTCSeconds**      | () =&gt; number                                                                                              | Gets the seconds of a <a href="#date">Date</a> object using Universal Coordinated Time (UTC).                                           |
| **getMilliseconds**    | () =&gt; number                                                                                              | Gets the milliseconds of a <a href="#date">Date</a>, using local time.                                                                  |
| **getUTCMilliseconds** | () =&gt; number                                                                                              | Gets the milliseconds of a <a href="#date">Date</a> object using Universal Coordinated Time (UTC).                                      |
| **getTimezoneOffset**  | () =&gt; number                                                                                              | Gets the difference in minutes between the time on the local computer and Universal Coordinated Time (UTC).                             |
| **setTime**            | (time: number) =&gt; number                                                                                  | Sets the date and time value in the <a href="#date">Date</a> object.                                                                    |
| **setMilliseconds**    | (ms: number) =&gt; number                                                                                    | Sets the milliseconds value in the <a href="#date">Date</a> object using local time.                                                    |
| **setUTCMilliseconds** | (ms: number) =&gt; number                                                                                    | Sets the milliseconds value in the <a href="#date">Date</a> object using Universal Coordinated Time (UTC).                              |
| **setSeconds**         | (sec: number, ms?: number \| undefined) =&gt; number                                                         | Sets the seconds value in the <a href="#date">Date</a> object using local time.                                                         |
| **setUTCSeconds**      | (sec: number, ms?: number \| undefined) =&gt; number                                                         | Sets the seconds value in the <a href="#date">Date</a> object using Universal Coordinated Time (UTC).                                   |
| **setMinutes**         | (min: number, sec?: number \| undefined, ms?: number \| undefined) =&gt; number                              | Sets the minutes value in the <a href="#date">Date</a> object using local time.                                                         |
| **setUTCMinutes**      | (min: number, sec?: number \| undefined, ms?: number \| undefined) =&gt; number                              | Sets the minutes value in the <a href="#date">Date</a> object using Universal Coordinated Time (UTC).                                   |
| **setHours**           | (hours: number, min?: number \| undefined, sec?: number \| undefined, ms?: number \| undefined) =&gt; number | Sets the hour value in the <a href="#date">Date</a> object using local time.                                                            |
| **setUTCHours**        | (hours: number, min?: number \| undefined, sec?: number \| undefined, ms?: number \| undefined) =&gt; number | Sets the hours value in the <a href="#date">Date</a> object using Universal Coordinated Time (UTC).                                     |
| **setDate**            | (date: number) =&gt; number                                                                                  | Sets the numeric day-of-the-month value of the <a href="#date">Date</a> object using local time.                                        |
| **setUTCDate**         | (date: number) =&gt; number                                                                                  | Sets the numeric day of the month in the <a href="#date">Date</a> object using Universal Coordinated Time (UTC).                        |
| **setMonth**           | (month: number, date?: number \| undefined) =&gt; number                                                     | Sets the month value in the <a href="#date">Date</a> object using local time.                                                           |
| **setUTCMonth**        | (month: number, date?: number \| undefined) =&gt; number                                                     | Sets the month value in the <a href="#date">Date</a> object using Universal Coordinated Time (UTC).                                     |
| **setFullYear**        | (year: number, month?: number \| undefined, date?: number \| undefined) =&gt; number                         | Sets the year of the <a href="#date">Date</a> object using local time.                                                                  |
| **setUTCFullYear**     | (year: number, month?: number \| undefined, date?: number \| undefined) =&gt; number                         | Sets the year value in the <a href="#date">Date</a> object using Universal Coordinated Time (UTC).                                      |
| **toUTCString**        | () =&gt; string                                                                                              | Returns a date converted to a string using Universal Coordinated Time (UTC).                                                            |
| **toISOString**        | () =&gt; string                                                                                              | Returns a date as a string value in ISO format.                                                                                         |
| **toJSON**             | (key?: any) =&gt; string                                                                                     | Used by the JSON.stringify method to enable the transformation of an object's data for JavaScript Object Notation (JSON) serialization. |


#### ScheduleOn

| Prop          | Type                                        |
| ------------- | ------------------------------------------- |
| **`year`**    | <code>number</code>                         |
| **`month`**   | <code>number</code>                         |
| **`day`**     | <code>number</code>                         |
| **`weekday`** | <code><a href="#weekday">Weekday</a></code> |
| **`hour`**    | <code>number</code>                         |
| **`minute`**  | <code>number</code>                         |
| **`second`**  | <code>number</code>                         |


#### Attachment

Represents a notification attachment.

| Prop          | Type                                                            | Description                                                                                                                           | Since |
| ------------- | --------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------- | ----- |
| **`id`**      | <code>string</code>                                             | The attachment identifier.                                                                                                            | 1.0.0 |
| **`url`**     | <code>string</code>                                             | The URL to the attachment. Use the `res` scheme to reference web assets, e.g. `res:///assets/img/icon.png`. Also accepts `file` URLs. | 1.0.0 |
| **`options`** | <code><a href="#attachmentoptions">AttachmentOptions</a></code> | <a href="#attachment">Attachment</a> options.                                                                                         | 1.0.0 |


#### AttachmentOptions

| Prop                                                             | Type                | Description                                                                                                                                                                                                                                   | Since |
| ---------------------------------------------------------------- | ------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ----- |
| **`iosUNNotificationAttachmentOptionsTypeHintKey`**              | <code>string</code> | Sets the `UNNotificationAttachmentOptionsTypeHintKey` key in the hashable options of [`UNNotificationAttachment`](https://developer.apple.com/documentation/usernotifications/unnotificationattachment). Only available for iOS.              | 1.0.0 |
| **`iosUNNotificationAttachmentOptionsThumbnailHiddenKey`**       | <code>string</code> | Sets the `UNNotificationAttachmentOptionsThumbnailHiddenKey` key in the hashable options of [`UNNotificationAttachment`](https://developer.apple.com/documentation/usernotifications/unnotificationattachment). Only available for iOS.       | 1.0.0 |
| **`iosUNNotificationAttachmentOptionsThumbnailClippingRectKey`** | <code>string</code> | Sets the `UNNotificationAttachmentOptionsThumbnailClippingRectKey` key in the hashable options of [`UNNotificationAttachment`](https://developer.apple.com/documentation/usernotifications/unnotificationattachment). Only available for iOS. | 1.0.0 |
| **`iosUNNotificationAttachmentOptionsThumbnailTimeKey`**         | <code>string</code> | Sets the `UNNotificationAttachmentOptionsThumbnailTimeKey` key in the hashable options of [`UNNotificationAttachment`](https://developer.apple.com/documentation/usernotifications/unnotificationattachment). Only available for iOS.         | 1.0.0 |


#### PendingResult

| Prop                | Type                                          | Description                        | Since |
| ------------------- | --------------------------------------------- | ---------------------------------- | ----- |
| **`notifications`** | <code>PendingLocalNotificationSchema[]</code> | The list of pending notifications. | 1.0.0 |


#### PendingLocalNotificationSchema

| Prop           | Type                                          | Description                                                          | Since |
| -------------- | --------------------------------------------- | -------------------------------------------------------------------- | ----- |
| **`title`**    | <code>string</code>                           | The title of the notification.                                       | 1.0.0 |
| **`body`**     | <code>string</code>                           | The body of the notification, shown below the title.                 | 1.0.0 |
| **`id`**       | <code>number</code>                           | The notification identifier.                                         | 1.0.0 |
| **`schedule`** | <code><a href="#schedule">Schedule</a></code> | <a href="#schedule">Schedule</a> this notification for a later time. | 1.0.0 |
| **`extra`**    | <code>any</code>                              | Set extra data to store within this notification.                    | 1.0.0 |


#### RegisterActionTypesOptions

| Prop        | Type                      | Description                           | Since |
| ----------- | ------------------------- | ------------------------------------- | ----- |
| **`types`** | <code>ActionType[]</code> | The list of action types to register. | 1.0.0 |


#### ActionType

A collection of actions.

| Prop                                   | Type                  | Description                                                                                                                                                                                     | Since |
| -------------------------------------- | --------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ----- |
| **`id`**                               | <code>string</code>   | The ID of the action type. Referenced in notifications by the `actionTypeId` key.                                                                                                               | 1.0.0 |
| **`actions`**                          | <code>Action[]</code> | The list of actions associated with this action type.                                                                                                                                           | 1.0.0 |
| **`iosHiddenPreviewsBodyPlaceholder`** | <code>string</code>   | Sets `hiddenPreviewsBodyPlaceholder` of the [`UNNotificationCategory`](https://developer.apple.com/documentation/usernotifications/unnotificationcategory). Only available for iOS.             | 1.0.0 |
| **`iosCustomDismissAction`**           | <code>boolean</code>  | Sets `customDismissAction` in the options of the [`UNNotificationCategory`](https://developer.apple.com/documentation/usernotifications/unnotificationcategory). Only available for iOS.        | 1.0.0 |
| **`iosAllowInCarPlay`**                | <code>boolean</code>  | Sets `allowInCarPlay` in the options of the [`UNNotificationCategory`](https://developer.apple.com/documentation/usernotifications/unnotificationcategory). Only available for iOS.             | 1.0.0 |
| **`iosHiddenPreviewsShowTitle`**       | <code>boolean</code>  | Sets `hiddenPreviewsShowTitle` in the options of the [`UNNotificationCategory`](https://developer.apple.com/documentation/usernotifications/unnotificationcategory). Only available for iOS.    | 1.0.0 |
| **`iosHiddenPreviewsShowSubtitle`**    | <code>boolean</code>  | Sets `hiddenPreviewsShowSubtitle` in the options of the [`UNNotificationCategory`](https://developer.apple.com/documentation/usernotifications/unnotificationcategory). Only available for iOS. | 1.0.0 |


#### Action

An action that can be taken when a notification is displayed.

| Prop                         | Type                 | Description                                                                                                                                                                                                     | Since |
| ---------------------------- | -------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ----- |
| **`id`**                     | <code>string</code>  | The action identifier. Referenced in the `'actionPerformed'` event as `actionId`.                                                                                                                               | 1.0.0 |
| **`title`**                  | <code>string</code>  | The title text to display for this action.                                                                                                                                                                      | 1.0.0 |
| **`requiresAuthentication`** | <code>boolean</code> | Sets `authenticationRequired` in the options of the [`UNNotificationAction`](https://developer.apple.com/documentation/usernotifications/unnotificationaction). Only available for iOS.                         | 1.0.0 |
| **`foreground`**             | <code>boolean</code> | Sets `foreground` in the options of the [`UNNotificationAction`](https://developer.apple.com/documentation/usernotifications/unnotificationaction). Only available for iOS.                                     | 1.0.0 |
| **`destructive`**            | <code>boolean</code> | Sets `destructive` in the options of the [`UNNotificationAction`](https://developer.apple.com/documentation/usernotifications/unnotificationaction). Only available for iOS.                                    | 1.0.0 |
| **`input`**                  | <code>boolean</code> | Use a `UNTextInputNotificationAction` instead of a `UNNotificationAction`. Only available for iOS.                                                                                                              | 1.0.0 |
| **`inputButtonTitle`**       | <code>string</code>  | Sets `textInputButtonTitle` on the [`UNTextInputNotificationAction`](https://developer.apple.com/documentation/usernotifications/untextinputnotificationaction). Only available for iOS when `input` is `true`. | 1.0.0 |
| **`inputPlaceholder`**       | <code>string</code>  | Sets `textInputPlaceholder` on the [`UNTextInputNotificationAction`](https://developer.apple.com/documentation/usernotifications/untextinputnotificationaction). Only available for iOS when `input` is `true`. | 1.0.0 |


#### CancelOptions

| Prop                | Type                                       | Description                          | Since |
| ------------------- | ------------------------------------------ | ------------------------------------ | ----- |
| **`notifications`** | <code>LocalNotificationDescriptor[]</code> | The list of notifications to cancel. | 1.0.0 |


#### EnabledResult

| Prop        | Type                 | Description                                                | Since |
| ----------- | -------------------- | ---------------------------------------------------------- | ----- |
| **`value`** | <code>boolean</code> | Whether or not the device has local notifications enabled. | 1.0.0 |


#### DeliveredNotifications

| Prop                | Type                                       | Description                                                         | Since |
| ------------------- | ------------------------------------------ | ------------------------------------------------------------------- | ----- |
| **`notifications`** | <code>DeliveredNotificationSchema[]</code> | List of notifications that are visible on the notifications screen. | 1.0.0 |


#### DeliveredNotificationSchema

| Prop               | Type                                          | Description                                                                                    | Since |
| ------------------ | --------------------------------------------- | ---------------------------------------------------------------------------------------------- | ----- |
| **`id`**           | <code>number</code>                           | The notification identifier.                                                                   | 4.0.0 |
| **`tag`**          | <code>string</code>                           | The notification tag. Only available on Android.                                               | 4.0.0 |
| **`title`**        | <code>string</code>                           | The title of the notification.                                                                 | 4.0.0 |
| **`body`**         | <code>string</code>                           | The body of the notification, shown below the title.                                           | 4.0.0 |
| **`group`**        | <code>string</code>                           | The configured group of the notification. Only available for Android.                          | 4.0.0 |
| **`groupSummary`** | <code>boolean</code>                          | If this notification is the summary for a group of notifications. Only available for Android.  | 4.0.0 |
| **`data`**         | <code>any</code>                              | Any additional data that was included in the notification payload. Only available for Android. | 4.0.0 |
| **`extra`**        | <code>any</code>                              | Extra data to store within this notification. Only available for iOS.                          | 4.0.0 |
| **`attachments`**  | <code>Attachment[]</code>                     | The attachments for this notification. Only available for iOS.                                 | 1.0.0 |
| **`actionTypeId`** | <code>string</code>                           | <a href="#action">Action</a> type ssociated with this notification. Only available for iOS.    | 4.0.0 |
| **`schedule`**     | <code><a href="#schedule">Schedule</a></code> | <a href="#schedule">Schedule</a> used to fire this notification. Only available for iOS.       | 4.0.0 |
| **`sound`**        | <code>string</code>                           | Sound that was used when the notification was displayed. Only available for iOS.               | 4.0.0 |


#### Channel

| Prop              | Type                                              | Description                                                                                                                                                                                                                                                                                                                                    | Default          | Since |
| ----------------- | ------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ---------------- | ----- |
| **`id`**          | <code>string</code>                               | The channel identifier.                                                                                                                                                                                                                                                                                                                        |                  | 1.0.0 |
| **`name`**        | <code>string</code>                               | The human-friendly name of this channel (presented to the user).                                                                                                                                                                                                                                                                               |                  | 1.0.0 |
| **`description`** | <code>string</code>                               | The description of this channel (presented to the user).                                                                                                                                                                                                                                                                                       |                  | 1.0.0 |
| **`sound`**       | <code>string</code>                               | The sound that should be played for notifications posted to this channel. Notification channels with an importance of at least `3` should have a sound. The file name of a sound file should be specified relative to the android app `res/raw` directory. If the sound is not provided, or the sound file is not found no sound will be used. |                  | 1.0.0 |
| **`importance`**  | <code><a href="#importance">Importance</a></code> | The level of interruption for notifications posted to this channel.                                                                                                                                                                                                                                                                            | <code>`3`</code> | 1.0.0 |
| **`visibility`**  | <code><a href="#visibility">Visibility</a></code> | The visibility of notifications posted to this channel. This setting is for whether notifications posted to this channel appear on the lockscreen or not, and if so, whether they appear in a redacted form.                                                                                                                                   |                  | 1.0.0 |
| **`lights`**      | <code>boolean</code>                              | Whether notifications posted to this channel should display notification lights, on devices that support it.                                                                                                                                                                                                                                   |                  | 1.0.0 |
| **`lightColor`**  | <code>string</code>                               | The light color for notifications posted to this channel. Only supported if lights are enabled on this channel and the device supports it. Supported color formats are `#RRGGBB` and `#RRGGBBAA`.                                                                                                                                              |                  | 1.0.0 |
| **`vibration`**   | <code>boolean</code>                              | Whether notifications posted to this channel should vibrate.                                                                                                                                                                                                                                                                                   |                  | 1.0.0 |


#### ListChannelsResult

| Prop           | Type                   | Description                        | Since |
| -------------- | ---------------------- | ---------------------------------- | ----- |
| **`channels`** | <code>Channel[]</code> | The list of notification channels. | 1.0.0 |


#### PermissionStatus

| Prop          | Type                                                        | Description                                   | Since |
| ------------- | ----------------------------------------------------------- | --------------------------------------------- | ----- |
| **`display`** | <code><a href="#permissionstate">PermissionState</a></code> | Permission state of displaying notifications. | 1.0.0 |


#### PluginListenerHandle

| Prop         | Type                                      |
| ------------ | ----------------------------------------- |
| **`remove`** | <code>() =&gt; Promise&lt;void&gt;</code> |


#### ActionPerformed

| Prop               | Type                                                                        | Description                                                                                                            | Since |
| ------------------ | --------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------- | ----- |
| **`actionId`**     | <code>string</code>                                                         | The identifier of the performed action.                                                                                | 1.0.0 |
| **`inputValue`**   | <code>string</code>                                                         | The value entered by the user on the notification. Only available on iOS for notifications with `input` set to `true`. | 1.0.0 |
| **`notification`** | <code><a href="#localnotificationschema">LocalNotificationSchema</a></code> | The original notification schema.                                                                                      | 1.0.0 |


### Type Aliases


#### ScheduleEvery

<code>'year' | 'month' | 'two-weeks' | 'week' | 'day' | 'hour' | 'minute' | 'second'</code>


#### Importance

The importance level. For more details, see the [Android Developer Docs](https://developer.android.com/reference/android/app/NotificationManager#IMPORTANCE_DEFAULT)

<code>1 | 2 | 3 | 4 | 5</code>


#### Visibility

The notification visibility. For more details, see the [Android Developer Docs](https://developer.android.com/reference/androidx/core/app/NotificationCompat#VISIBILITY_PRIVATE)

<code>-1 | 0 | 1</code>


#### PermissionState

<code>'prompt' | 'prompt-with-rationale' | 'granted' | 'denied'</code>


### Enums


#### Weekday

| Members         | Value          |
| --------------- | -------------- |
| **`Sunday`**    | <code>1</code> |
| **`Monday`**    | <code>2</code> |
| **`Tuesday`**   | <code>3</code> |
| **`Wednesday`** | <code>4</code> |
| **`Thursday`**  | <code>5</code> |
| **`Friday`**    | <code>6</code> |
| **`Saturday`**  | <code>7</code> |

</docgen-api>
