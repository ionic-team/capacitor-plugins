# @capacitor/local-notifications

The Notifications API provides access to native local notifications.

## Install

```bash
npm install @capacitor/local-notifications
npx cap sync
```

## API

<docgen-index>

* [`schedule(...)`](#schedule)
* [`getPending()`](#getpending)
* [`registerActionTypes(...)`](#registeractiontypes)
* [`cancel(...)`](#cancel)
* [`areEnabled()`](#areenabled)
* [`createChannel(...)`](#createchannel)
* [`deleteChannel(...)`](#deletechannel)
* [`listChannels()`](#listchannels)
* [`checkPermissions()`](#checkpermissions)
* [`requestPermissions()`](#requestpermissions)
* [`addListener(...)`](#addlistener)
* [`addListener(...)`](#addlistener)
* [`removeAllListeners()`](#removealllisteners)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### schedule(...)

```typescript
schedule(options: { notifications: LocalNotification[]; }) => Promise<LocalNotificationScheduleResult>
```

| Param         | Type                                                 |
| ------------- | ---------------------------------------------------- |
| **`options`** | <code>{ notifications: LocalNotification[]; }</code> |

**Returns:** <code>Promise&lt;<a href="#localnotificationpendinglist">LocalNotificationPendingList</a>&gt;</code>

--------------------


### getPending()

```typescript
getPending() => Promise<LocalNotificationPendingList>
```

**Returns:** <code>Promise&lt;<a href="#localnotificationpendinglist">LocalNotificationPendingList</a>&gt;</code>

--------------------


### registerActionTypes(...)

```typescript
registerActionTypes(options: { types: LocalNotificationActionType[]; }) => Promise<void>
```

| Param         | Type                                                   |
| ------------- | ------------------------------------------------------ |
| **`options`** | <code>{ types: LocalNotificationActionType[]; }</code> |

--------------------


### cancel(...)

```typescript
cancel(pending: LocalNotificationPendingList) => Promise<void>
```

| Param         | Type                                                                                  |
| ------------- | ------------------------------------------------------------------------------------- |
| **`pending`** | <code><a href="#localnotificationpendinglist">LocalNotificationPendingList</a></code> |

--------------------


### areEnabled()

```typescript
areEnabled() => Promise<LocalNotificationEnabledResult>
```

**Returns:** <code>Promise&lt;<a href="#localnotificationenabledresult">LocalNotificationEnabledResult</a>&gt;</code>

--------------------


### createChannel(...)

```typescript
createChannel(channel: NotificationChannel) => Promise<void>
```

| Param         | Type                                                                |
| ------------- | ------------------------------------------------------------------- |
| **`channel`** | <code><a href="#notificationchannel">NotificationChannel</a></code> |

--------------------


### deleteChannel(...)

```typescript
deleteChannel(channel: NotificationChannel) => Promise<void>
```

| Param         | Type                                                                |
| ------------- | ------------------------------------------------------------------- |
| **`channel`** | <code><a href="#notificationchannel">NotificationChannel</a></code> |

--------------------


### listChannels()

```typescript
listChannels() => Promise<NotificationChannelList>
```

**Returns:** <code>Promise&lt;<a href="#notificationchannellist">NotificationChannelList</a>&gt;</code>

--------------------


### checkPermissions()

```typescript
checkPermissions() => Promise<LocalNotificationsPermissionStatus>
```

Check notification permissions

**Returns:** <code>Promise&lt;<a href="#localnotificationspermissionstatus">LocalNotificationsPermissionStatus</a>&gt;</code>

**Since:** 1.0.0

--------------------


### requestPermissions()

```typescript
requestPermissions() => Promise<LocalNotificationsPermissionStatus>
```

Request location permissions

**Returns:** <code>Promise&lt;<a href="#localnotificationspermissionstatus">LocalNotificationsPermissionStatus</a>&gt;</code>

**Since:** 1.0.0

--------------------


### addListener(...)

```typescript
addListener(eventName: 'localNotificationReceived', listenerFunc: (notification: LocalNotification) => void) => PluginListenerHandle
```

| Param              | Type                                                                                       |
| ------------------ | ------------------------------------------------------------------------------------------ |
| **`eventName`**    | <code>"localNotificationReceived"</code>                                                   |
| **`listenerFunc`** | <code>(notification: <a href="#localnotification">LocalNotification</a>) =&gt; void</code> |

**Returns:** <code><a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

--------------------


### addListener(...)

```typescript
addListener(eventName: 'localNotificationActionPerformed', listenerFunc: (notificationAction: LocalNotificationActionPerformed) => void) => PluginListenerHandle
```

| Param              | Type                                                                                                                           |
| ------------------ | ------------------------------------------------------------------------------------------------------------------------------ |
| **`eventName`**    | <code>"localNotificationActionPerformed"</code>                                                                                |
| **`listenerFunc`** | <code>(notificationAction: <a href="#localnotificationactionperformed">LocalNotificationActionPerformed</a>) =&gt; void</code> |

**Returns:** <code><a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

--------------------


### removeAllListeners()

```typescript
removeAllListeners() => void
```

Remove all native listeners for this plugin

--------------------


### Interfaces


#### LocalNotificationPendingList

| Prop                | Type                                    |
| ------------------- | --------------------------------------- |
| **`notifications`** | <code>LocalNotificationRequest[]</code> |


#### LocalNotificationRequest

| Prop     | Type                |
| -------- | ------------------- |
| **`id`** | <code>string</code> |


#### LocalNotification

| Prop                   | Type                                                                            | Description                                                                                                                                                                                                                                                            |
| ---------------------- | ------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **`title`**            | <code>string</code>                                                             |                                                                                                                                                                                                                                                                        |
| **`body`**             | <code>string</code>                                                             |                                                                                                                                                                                                                                                                        |
| **`id`**               | <code>number</code>                                                             |                                                                                                                                                                                                                                                                        |
| **`schedule`**         | <code><a href="#localnotificationschedule">LocalNotificationSchedule</a></code> |                                                                                                                                                                                                                                                                        |
| **`sound`**            | <code>string</code>                                                             | Name of the audio file with extension. On iOS the file should be in the app bundle. On Android the file should be on res/raw folder. Doesn't work on Android version 26+ (Android O and newer), for Recommended format is .wav because is supported by both platforms. |
| **`smallIcon`**        | <code>string</code>                                                             | Android-only: set a custom statusbar icon. If set, it overrides default icon from capacitor.config.json                                                                                                                                                                |
| **`iconColor`**        | <code>string</code>                                                             | Android only: set the color of the notification icon                                                                                                                                                                                                                   |
| **`attachments`**      | <code>LocalNotificationAttachment[]</code>                                      |                                                                                                                                                                                                                                                                        |
| **`actionTypeId`**     | <code>string</code>                                                             |                                                                                                                                                                                                                                                                        |
| **`extra`**            | <code>any</code>                                                                |                                                                                                                                                                                                                                                                        |
| **`threadIdentifier`** | <code>string</code>                                                             | iOS only: set the thread identifier for notification grouping                                                                                                                                                                                                          |
| **`summaryArgument`**  | <code>string</code>                                                             | iOS 12+ only: set the summary argument for notification grouping                                                                                                                                                                                                       |
| **`group`**            | <code>string</code>                                                             | Android only: set the group identifier for notification grouping, like threadIdentifier on iOS.                                                                                                                                                                        |
| **`groupSummary`**     | <code>boolean</code>                                                            | Android only: designate this notification as the summary for a group (should be used with the `group` property).                                                                                                                                                       |
| **`channelId`**        | <code>string</code>                                                             | Android only: set the notification channel on which local notification will generate. If channel with the given name does not exist then the notification will not fire. If not provided, it will use the default channel.                                             |
| **`ongoing`**          | <code>boolean</code>                                                            | Android only: set the notification ongoing. If set to true the notification can't be swiped away.                                                                                                                                                                      |
| **`autoCancel`**       | <code>boolean</code>                                                            | Android only: set the notification to be removed automatically when the user clicks on it                                                                                                                                                                              |


#### LocalNotificationSchedule

| Prop          | Type                                                                                               |
| ------------- | -------------------------------------------------------------------------------------------------- |
| **`at`**      | <code><a href="#date">Date</a></code>                                                              |
| **`repeats`** | <code>boolean</code>                                                                               |
| **`every`**   | <code>"year" \| "month" \| "two-weeks" \| "week" \| "day" \| "hour" \| "minute" \| "second"</code> |
| **`count`**   | <code>number</code>                                                                                |
| **`on`**      | <code>{ year?: number; month?: number; day?: number; hour?: number; minute?: number; }</code>      |


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


#### LocalNotificationAttachment

| Prop          | Type                                                                                              |
| ------------- | ------------------------------------------------------------------------------------------------- |
| **`id`**      | <code>string</code>                                                                               |
| **`url`**     | <code>string</code>                                                                               |
| **`options`** | <code><a href="#localnotificationattachmentoptions">LocalNotificationAttachmentOptions</a></code> |


#### LocalNotificationAttachmentOptions

| Prop                                                             | Type                |
| ---------------------------------------------------------------- | ------------------- |
| **`iosUNNotificationAttachmentOptionsTypeHintKey`**              | <code>string</code> |
| **`iosUNNotificationAttachmentOptionsThumbnailHiddenKey`**       | <code>string</code> |
| **`iosUNNotificationAttachmentOptionsThumbnailClippingRectKey`** | <code>string</code> |
| **`iosUNNotificationAttachmentOptionsThumbnailTimeKey`**         | <code>string</code> |


#### LocalNotificationActionType

| Prop                                   | Type                                   |
| -------------------------------------- | -------------------------------------- |
| **`id`**                               | <code>string</code>                    |
| **`actions`**                          | <code>LocalNotificationAction[]</code> |
| **`iosHiddenPreviewsBodyPlaceholder`** | <code>string</code>                    |
| **`iosCustomDismissAction`**           | <code>boolean</code>                   |
| **`iosAllowInCarPlay`**                | <code>boolean</code>                   |
| **`iosHiddenPreviewsShowTitle`**       | <code>boolean</code>                   |
| **`iosHiddenPreviewsShowSubtitle`**    | <code>boolean</code>                   |


#### LocalNotificationAction

| Prop                         | Type                 |
| ---------------------------- | -------------------- |
| **`id`**                     | <code>string</code>  |
| **`title`**                  | <code>string</code>  |
| **`requiresAuthentication`** | <code>boolean</code> |
| **`foreground`**             | <code>boolean</code> |
| **`destructive`**            | <code>boolean</code> |
| **`input`**                  | <code>boolean</code> |
| **`inputButtonTitle`**       | <code>string</code>  |
| **`inputPlaceholder`**       | <code>string</code>  |


#### LocalNotificationEnabledResult

| Prop        | Type                 | Description                                               |
| ----------- | -------------------- | --------------------------------------------------------- |
| **`value`** | <code>boolean</code> | Whether the device has Local Notifications enabled or not |


#### NotificationChannel

| Prop              | Type                               |
| ----------------- | ---------------------------------- |
| **`id`**          | <code>string</code>                |
| **`name`**        | <code>string</code>                |
| **`description`** | <code>string</code>                |
| **`sound`**       | <code>string</code>                |
| **`importance`**  | <code>1 \| 2 \| 5 \| 4 \| 3</code> |
| **`visibility`**  | <code>0 \| 1 \| -1</code>          |
| **`lights`**      | <code>boolean</code>               |
| **`lightColor`**  | <code>string</code>                |
| **`vibration`**   | <code>boolean</code>               |


#### NotificationChannelList

| Prop           | Type                               |
| -------------- | ---------------------------------- |
| **`channels`** | <code>NotificationChannel[]</code> |


#### LocalNotificationsPermissionStatus

| Prop          | Type                                                                      |
| ------------- | ------------------------------------------------------------------------- |
| **`display`** | <code>"prompt" \| "prompt-with-rationale" \| "granted" \| "denied"</code> |


#### PluginListenerHandle

| Prop         | Type                       |
| ------------ | -------------------------- |
| **`remove`** | <code>() =&gt; void</code> |


#### LocalNotificationActionPerformed

| Prop               | Type                                                            |
| ------------------ | --------------------------------------------------------------- |
| **`actionId`**     | <code>string</code>                                             |
| **`inputValue`**   | <code>string</code>                                             |
| **`notification`** | <code><a href="#localnotification">LocalNotification</a></code> |

</docgen-api>
