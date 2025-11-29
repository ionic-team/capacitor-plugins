/// <reference types="@capacitor/cli" />

import type { PermissionState, PluginListenerHandle } from '@capacitor/core';

declare module '@capacitor/cli' {
  export interface PluginsConfig {
    /**
     * On Android, the Local Notifications can be configured with the following options:
     */
    LocalNotifications?: {
      /**
       * Set the default status bar icon for notifications.
       *
       * Icons should be placed in your app's `res/drawable` folder. The value for
       * this option should be the drawable resource ID, which is the filename
       * without an extension.
       *
       * Only available for Android.
       *
       * @since 1.0.0
       * @example "ic_stat_icon_config_sample"
       */
      smallIcon?: string;

      /**
       * Set the default color of status bar icons for notifications.
       *
       * Only available for Android.
       *
       * @since 1.0.0
       * @example "#488AFF"
       */
      iconColor?: string;

      /**
       * Set the default notification sound for notifications.
       *
       * On Android 8+ it sets the default channel sound and can't be
       * changed unless the app is uninstalled.
       *
       * If the audio file is not found, it will result in the default system
       * sound being played on Android 7.x and no sound on Android 8+.
       *
       * Only available for Android.
       *
       * @since 1.0.0
       * @example "beep.wav"
       */
      sound?: string;
    };
  }
}

export interface LocalNotificationsPlugin {
  /**
   * Schedule one or more local notifications.
   *
   * @since 1.0.0
   */
  schedule(options: ScheduleOptions): Promise<ScheduleResult>;

  /**
   * Get a list of pending notifications.
   *
   * @since 1.0.0
   */
  getPending(): Promise<PendingResult>;

  /**
   * Register actions to take when notifications are displayed.
   *
   * Only available for iOS and Android.
   *
   * @since 1.0.0
   */
  registerActionTypes(options: RegisterActionTypesOptions): Promise<void>;

  /**
   * Cancel pending notifications.
   *
   * @since 1.0.0
   */
  cancel(options: CancelOptions): Promise<void>;

  /**
   * Check if notifications are enabled or not.
   *
   * @deprecated Use `checkPermissions()` to check if the user has allowed
   * notifications to be displayed.
   * @since 1.0.0
   */
  areEnabled(): Promise<EnabledResult>;

  /**
   * Get a list of notifications that are visible on the notifications screen.
   *
   * @since 4.0.0
   */
  getDeliveredNotifications(): Promise<DeliveredNotifications>;

  /**
   * Remove the specified notifications from the notifications screen.
   *
   * @since 4.0.0
   */
  removeDeliveredNotifications(delivered: DeliveredNotifications): Promise<void>;

  /**
   * Remove all the notifications from the notifications screen.
   *
   * @since 4.0.0
   */
  removeAllDeliveredNotifications(): Promise<void>;

  /**
   * Create a notification channel.
   *
   * Only available for Android.
   *
   * @since 1.0.0
   */
  createChannel(channel: Channel): Promise<void>;

  /**
   * Delete a notification channel.
   *
   * Only available for Android.
   *
   * @since 1.0.0
   */
  deleteChannel(args: { id: string }): Promise<void>;

  /**
   * Get a list of notification channels.
   *
   * Only available for Android.
   *
   * @since 1.0.0
   */
  listChannels(): Promise<ListChannelsResult>;

  /**
   * Check permission to display local notifications.
   *
   * @since 1.0.0
   */
  checkPermissions(): Promise<PermissionStatus>;

  /**
   * Request permission to display local notifications.
   *
   * @since 1.0.0
   */
  requestPermissions(): Promise<PermissionStatus>;

  /**
   * Direct user to the application settings screen to configure exact alarms.
   *
   * In the event that a user changes the settings from granted to denied, the application
   * will restart and any notification scheduled with an exact alarm will be deleted.
   *
   * On Android < 12, the user will NOT be directed to the application settings screen, instead this function will
   * return `granted`.
   *
   * Only available on Android.
   *
   * @since 6.0.0
   */
  changeExactNotificationSetting(): Promise<SettingsPermissionStatus>;

  /**
   * Check application setting for using exact alarms.
   *
   * Only available on Android.
   *
   * @since 6.0.0
   */
  checkExactNotificationSetting(): Promise<SettingsPermissionStatus>;

  /**
   * Listen for when notifications are displayed.
   *
   * @since 1.0.0
   */
  addListener(
    eventName: 'localNotificationReceived',
    listenerFunc: (notification: LocalNotificationSchema) => void,
  ): Promise<PluginListenerHandle>;

  /**
   * Listen for when an action is performed on a notification.
   *
   * @since 1.0.0
   */
  addListener(
    eventName: 'localNotificationActionPerformed',
    listenerFunc: (notificationAction: ActionPerformed) => void,
  ): Promise<PluginListenerHandle>;

  /**
   * Remove all listeners for this plugin.
   *
   * @since 1.0.0
   */
  removeAllListeners(): Promise<void>;
}

/**
 * The object that describes a local notification.
 *
 * @since 1.0.0
 */
export interface LocalNotificationDescriptor {
  /**
   * The notification identifier.
   *
   * @since 1.0.0
   */
  id: number;
}

export interface ScheduleOptions {
  /**
   * The list of notifications to schedule.
   *
   * @since 1.0.0
   */
  notifications: LocalNotificationSchema[];
}

export interface ScheduleResult {
  /**
   * The list of scheduled notifications.
   *
   * @since 1.0.0
   */
  notifications: LocalNotificationDescriptor[];
}

export interface PendingResult {
  /**
   * The list of pending notifications.
   *
   * @since 1.0.0
   */
  notifications: PendingLocalNotificationSchema[];
}

export interface RegisterActionTypesOptions {
  /**
   * The list of action types to register.
   *
   * @since 1.0.0
   */
  types: ActionType[];
}

export interface CancelOptions {
  /**
   * The list of notifications to cancel.
   *
   * @since 1.0.0
   */
  notifications: LocalNotificationDescriptor[];
}

/**
 * A collection of actions.
 *
 * @since 1.0.0
 */
export interface ActionType {
  /**
   * The ID of the action type.
   *
   * Referenced in notifications by the `actionTypeId` key.
   *
   * @since 1.0.0
   */
  id: string;

  /**
   * The list of actions associated with this action type.
   *
   * @since 1.0.0
   */
  actions?: Action[];

  /**
   * Sets `hiddenPreviewsBodyPlaceholder` of the
   * [`UNNotificationCategory`](https://developer.apple.com/documentation/usernotifications/unnotificationcategory).
   *
   * Only available for iOS.
   *
   * @since 1.0.0
   */
  iosHiddenPreviewsBodyPlaceholder?: string;

  /**
   * Sets `customDismissAction` in the options of the
   * [`UNNotificationCategory`](https://developer.apple.com/documentation/usernotifications/unnotificationcategory).
   *
   * Only available for iOS.
   *
   * @since 1.0.0
   */
  iosCustomDismissAction?: boolean;

  /**
   * Sets `allowInCarPlay` in the options of the
   * [`UNNotificationCategory`](https://developer.apple.com/documentation/usernotifications/unnotificationcategory).
   *
   * Only available for iOS.
   *
   * @since 1.0.0
   */
  iosAllowInCarPlay?: boolean;

  /**
   * Sets `hiddenPreviewsShowTitle` in the options of the
   * [`UNNotificationCategory`](https://developer.apple.com/documentation/usernotifications/unnotificationcategory).
   *
   * Only available for iOS.
   *
   * @since 1.0.0
   */
  iosHiddenPreviewsShowTitle?: boolean;

  /**
   * Sets `hiddenPreviewsShowSubtitle` in the options of the
   * [`UNNotificationCategory`](https://developer.apple.com/documentation/usernotifications/unnotificationcategory).
   *
   * Only available for iOS.
   *
   * @since 1.0.0
   */
  iosHiddenPreviewsShowSubtitle?: boolean;
}

/**
 * An action that can be taken when a notification is displayed.
 *
 * @since 1.0.0
 */
export interface Action {
  /**
   * The action identifier.
   *
   * Referenced in the `'actionPerformed'` event as `actionId`.
   *
   * @since 1.0.0
   */
  id: string;

  /**
   * The title text to display for this action.
   *
   * @since 1.0.0
   */
  title: string;

  /**
   * Sets `authenticationRequired` in the options of the
   * [`UNNotificationAction`](https://developer.apple.com/documentation/usernotifications/unnotificationaction).
   *
   * Only available for iOS.
   *
   * @since 1.0.0
   */
  requiresAuthentication?: boolean;

  /**
   * Sets `foreground` in the options of the
   * [`UNNotificationAction`](https://developer.apple.com/documentation/usernotifications/unnotificationaction).
   *
   * Only available for iOS.
   *
   * @since 1.0.0
   */
  foreground?: boolean;

  /**
   * Sets `destructive` in the options of the
   * [`UNNotificationAction`](https://developer.apple.com/documentation/usernotifications/unnotificationaction).
   *
   * Only available for iOS.
   *
   * @since 1.0.0
   */
  destructive?: boolean;

  /**
   * Use a `UNTextInputNotificationAction` instead of a `UNNotificationAction`.
   *
   * Only available for iOS.
   *
   * @since 1.0.0
   */
  input?: boolean;

  /**
   * Sets `textInputButtonTitle` on the
   * [`UNTextInputNotificationAction`](https://developer.apple.com/documentation/usernotifications/untextinputnotificationaction).
   *
   * Only available for iOS when `input` is `true`.
   *
   * @since 1.0.0
   */
  inputButtonTitle?: string;

  /**
   * Sets `textInputPlaceholder` on the
   * [`UNTextInputNotificationAction`](https://developer.apple.com/documentation/usernotifications/untextinputnotificationaction).
   *
   * Only available for iOS when `input` is `true`.
   *
   * @since 1.0.0
   */
  inputPlaceholder?: string;
}

/**
 * Represents a notification attachment.
 *
 * @since 1.0.0
 */
export interface Attachment {
  /**
   * The attachment identifier.
   *
   * @since 1.0.0
   */
  id: string;

  /**
   * The URL to the attachment.
   *
   * Use the `res` scheme to reference web assets, e.g.
   * `res:///assets/img/icon.png`. Also accepts `file` URLs.
   *
   * @since 1.0.0
   */
  url: string;

  /**
   * Attachment options.
   *
   * @since 1.0.0
   */
  options?: AttachmentOptions;
}

export interface AttachmentOptions {
  /**
   * Sets the `UNNotificationAttachmentOptionsTypeHintKey` key in the hashable
   * options of
   * [`UNNotificationAttachment`](https://developer.apple.com/documentation/usernotifications/unnotificationattachment).
   *
   * Only available for iOS.
   *
   * @since 1.0.0
   */
  iosUNNotificationAttachmentOptionsTypeHintKey?: string;

  /**
   * Sets the `UNNotificationAttachmentOptionsThumbnailHiddenKey` key in the
   * hashable options of
   * [`UNNotificationAttachment`](https://developer.apple.com/documentation/usernotifications/unnotificationattachment).
   *
   * Only available for iOS.
   *
   * @since 1.0.0
   */
  iosUNNotificationAttachmentOptionsThumbnailHiddenKey?: string;

  /**
   * Sets the `UNNotificationAttachmentOptionsThumbnailClippingRectKey` key in
   * the hashable options of
   * [`UNNotificationAttachment`](https://developer.apple.com/documentation/usernotifications/unnotificationattachment).
   *
   * Only available for iOS.
   *
   * @since 1.0.0
   */
  iosUNNotificationAttachmentOptionsThumbnailClippingRectKey?: string;

  /**
   * Sets the `UNNotificationAttachmentOptionsThumbnailTimeKey` key in the
   * hashable options of
   * [`UNNotificationAttachment`](https://developer.apple.com/documentation/usernotifications/unnotificationattachment).
   *
   * Only available for iOS.
   *
   * @since 1.0.0
   */
  iosUNNotificationAttachmentOptionsThumbnailTimeKey?: string;
}

export interface PendingLocalNotificationSchema {
  /**
   * The title of the notification.
   *
   * @since 1.0.0
   */
  title: string;

  /**
   * The body of the notification, shown below the title.
   *
   * @since 1.0.0
   */
  body: string;

  /**
   * The notification identifier.
   *
   * @since 1.0.0
   */
  id: number;

  /**
   * Schedule this notification for a later time.
   *
   * @since 1.0.0
   */
  schedule?: Schedule;

  /**
   * Set extra data to store within this notification.
   *
   * @since 1.0.0
   */
  extra?: any;
}

export interface LocalNotificationSchema {
  /**
   * The title of the notification.
   *
   * @since 1.0.0
   */
  title: string;

  /**
   * The body of the notification, shown below the title.
   *
   * @since 1.0.0
   */
  body: string;

  /**
   * Sets a multiline text block for display in a big text notification style.
   *
   * @since 1.0.0
   */
  largeBody?: string;

  /**
   * Used to set the summary text detail in inbox and big text notification styles.
   *
   * Only available for Android.
   *
   * @since 1.0.0
   */
  summaryText?: string;
  /**
   * The notification identifier.
   * On Android it's a 32-bit int.
   * So the value should be between -2147483648 and 2147483647 inclusive.
   *
   * @since 1.0.0
   */
  id: number;

  /**
   * Schedule this notification for a later time.
   *
   * @since 1.0.0
   */
  schedule?: Schedule;

  /**
   * Name of the audio file to play when this notification is displayed.
   *
   * Include the file extension with the filename.
   *
   * On iOS, the file should be in the app bundle.
   * On Android, the file should be in res/raw folder.
   *
   * Recommended format is `.wav` because is supported by both iOS and Android.
   *
   * Only available for iOS and Android 7.x.
   * For Android 8+ use channelId of a channel configured with the desired sound.
   *
   * If the sound file is not found, (i.e. empty string or wrong name)
   * the default system notification sound will be used.
   * If not provided, it will produce the default sound on Android and no sound on iOS.
   *
   * @since 1.0.0
   */
  sound?: string;

  /**
   * Set a custom status bar icon.
   *
   * If set, this overrides the `smallIcon` option from Capacitor
   * configuration.
   *
   * Icons should be placed in your app's `res/drawable` folder. The value for
   * this option should be the drawable resource ID, which is the filename
   * without an extension.
   *
   * Only available for Android.
   *
   * @since 1.0.0
   */
  smallIcon?: string;

  /**
   * Set a large icon for notifications.
   *
   * Icons should be placed in your app's `res/drawable` folder. The value for
   * this option should be the drawable resource ID, which is the filename
   * without an extension.
   *
   * Only available for Android.
   *
   * @since 1.0.0
   */
  largeIcon?: string;

  /**
   * Set the color of the notification icon.
   *
   * Only available for Android.
   *
   * @since 1.0.0
   */
  iconColor?: string;

  /**
   * Set attachments for this notification.
   *
   * @since 1.0.0
   */
  attachments?: Attachment[];

  /**
   * Associate an action type with this notification.
   *
   * @since 1.0.0
   */
  actionTypeId?: string;

  /**
   * Set extra data to store within this notification.
   *
   * @since 1.0.0
   */
  extra?: any;

  /**
   * Used to group multiple notifications.
   *
   * Sets `threadIdentifier` on the
   * [`UNMutableNotificationContent`](https://developer.apple.com/documentation/usernotifications/unmutablenotificationcontent).
   *
   * Only available for iOS.
   *
   * @since 1.0.0
   */
  threadIdentifier?: string;

  /**
   * The string this notification adds to the category's summary format string.
   *
   * Sets `summaryArgument` on the
   * [`UNMutableNotificationContent`](https://developer.apple.com/documentation/usernotifications/unmutablenotificationcontent).
   *
   * Only available for iOS.
   *
   * @since 1.0.0
   */
  summaryArgument?: string;

  /**
   * Used to group multiple notifications.
   *
   * Calls `setGroup()` on
   * [`NotificationCompat.Builder`](https://developer.android.com/reference/androidx/core/app/NotificationCompat.Builder)
   * with the provided value.
   *
   * Only available for Android.
   *
   * @since 1.0.0
   */
  group?: string;

  /**
   * If true, this notification becomes the summary for a group of
   * notifications.
   *
   * Calls `setGroupSummary()` on
   * [`NotificationCompat.Builder`](https://developer.android.com/reference/androidx/core/app/NotificationCompat.Builder)
   * with the provided value.
   *
   * Only available for Android when using `group`.
   *
   * @since 1.0.0
   */
  groupSummary?: boolean;

  /**
   * Specifies the channel the notification should be delivered on.
   *
   * If channel with the given name does not exist then the notification will
   * not fire. If not provided, it will use the default channel.
   *
   * Calls `setChannelId()` on
   * [`NotificationCompat.Builder`](https://developer.android.com/reference/androidx/core/app/NotificationCompat.Builder)
   * with the provided value.
   *
   * Only available for Android 8+.
   *
   * @since 1.0.0
   */
  channelId?: string;

  /**
   * If true, the notification can't be swiped away.
   *
   * Calls `setOngoing()` on
   * [`NotificationCompat.Builder`](https://developer.android.com/reference/androidx/core/app/NotificationCompat.Builder)
   * with the provided value.
   *
   * Only available for Android.
   *
   * @since 1.0.0
   */
  ongoing?: boolean;

  /**
   * If true, the notification is canceled when the user clicks on it.
   *
   * Calls `setAutoCancel()` on
   * [`NotificationCompat.Builder`](https://developer.android.com/reference/androidx/core/app/NotificationCompat.Builder)
   * with the provided value.
   *
   * Only available for Android.
   *
   * @since 1.0.0
   */
  autoCancel?: boolean;

  /**
   * Sets a list of strings for display in an inbox style notification.
   *
   * Up to 5 strings are allowed.
   *
   * Only available for Android.
   *
   * @since 1.0.0
   */
  inboxList?: string[];

  /**
   * If true, notification will not appear while app is in the foreground.
   *
   * Only available for iOS.
   *
   * @since 5.0.0
   */
  silent?: boolean;
}

/**
 * Represents a schedule for a notification.
 *
 * Use either `at`, `on`, or `every` to schedule notifications.
 *
 * @since 1.0.0
 */
export interface Schedule {
  /**
   * Schedule a notification at a specific date and time.
   *
   * @since 1.0.0
   */
  at?: Date;

  /**
   * Repeat delivery of this notification at the date and time specified by
   * `at`.
   *
   * Only available for iOS and Android.
   *
   * @since 1.0.0
   */
  repeats?: boolean;

  /**
   * Allow this notification to fire while in [Doze](https://developer.android.com/training/monitoring-device-state/doze-standby)
   *
   * Note that these notifications can only fire [once per 9 minutes, per app](https://developer.android.com/training/monitoring-device-state/doze-standby#assessing_your_app).
   *
   * @since 1.0.0
   */
  allowWhileIdle?: boolean;

  /**
   * Schedule a notification on particular interval(s).
   *
   * This is similar to scheduling [cron](https://en.wikipedia.org/wiki/Cron)
   * jobs.
   *
   * Only available for iOS and Android.
   *
   * @since 1.0.0
   */
  on?: ScheduleOn;

  /**
   * Schedule a notification on a particular interval.
   *
   * @since 1.0.0
   */
  every?: ScheduleEvery;

  /**
   * Limit the number times a notification is delivered by the interval
   * specified by `every`.
   *
   * @since 1.0.0
   */
  count?: number;
}

export interface ScheduleOn {
  year?: number;
  month?: number;
  day?: number;
  weekday?: Weekday;
  hour?: number;
  minute?: number;
  second?: number;
}

export type ScheduleEvery = 'year' | 'month' | 'two-weeks' | 'week' | 'day' | 'hour' | 'minute' | 'second';

export interface ListChannelsResult {
  /**
   * The list of notification channels.
   *
   * @since 1.0.0
   */
  channels: Channel[];
}

export interface PermissionStatus {
  /**
   * Permission state of displaying notifications.
   *
   * @since 1.0.0
   */
  display: PermissionState;
}

export interface SettingsPermissionStatus {
  /**
   * Permission state of using exact alarms.
   *
   * @since 6.0.0
   */
  exact_alarm: PermissionState;
}

export interface ActionPerformed {
  /**
   * The identifier of the performed action.
   *
   * @since 1.0.0
   */
  actionId: string;

  /**
   * The value entered by the user on the notification.
   *
   * Only available on iOS for notifications with `input` set to `true`.
   *
   * @since 1.0.0
   */
  inputValue?: string;

  /**
   * The original notification schema.
   *
   * @since 1.0.0
   */
  notification: LocalNotificationSchema;
}

/**
 * @deprecated
 */
export interface EnabledResult {
  /**
   * Whether or not the device has local notifications enabled.
   *
   * @since 1.0.0
   */
  value: boolean;
}

export interface DeliveredNotificationSchema {
  /**
   * The notification identifier.
   *
   * @since 4.0.0
   */
  id: number;

  /**
   * The notification tag.
   *
   * Only available on Android.
   *
   * @since 4.0.0
   */
  tag?: string;
  /**
   * The title of the notification.
   *
   * @since 4.0.0
   */
  title: string;

  /**
   * The body of the notification, shown below the title.
   *
   * @since 4.0.0
   */
  body: string;

  /**
   * The configured group of the notification.
   *
   *
   * Only available for Android.
   *
   * @since 4.0.0
   */
  group?: string;

  /**
   * If this notification is the summary for a group of notifications.
   *
   * Only available for Android.
   *
   * @since 4.0.0
   */
  groupSummary?: boolean;

  /**
   * Any additional data that was included in the
   * notification payload.
   *
   * Only available for Android.
   *
   * @since 4.0.0
   */
  data?: any;

  /**
   * Extra data to store within this notification.
   *
   * Only available for iOS.
   *
   * @since 4.0.0
   */
  extra?: any;

  /**
   * The attachments for this notification.
   *
   * Only available for iOS.
   *
   * @since 1.0.0
   */
  attachments?: Attachment[];

  /**
   * Action type ssociated with this notification.
   *
   * Only available for iOS.
   *
   * @since 4.0.0
   */
  actionTypeId?: string;

  /**
   * Schedule used to fire this notification.
   *
   * Only available for iOS.
   *
   * @since 4.0.0
   */
  schedule?: Schedule;

  /**
   * Sound that was used when the notification was displayed.
   *
   * Only available for iOS.
   *
   * @since 4.0.0
   */
  sound?: string;
}

export interface DeliveredNotifications {
  /**
   * List of notifications that are visible on the
   * notifications screen.
   *
   * @since 1.0.0
   */
  notifications: DeliveredNotificationSchema[];
}

export interface Channel {
  /**
   * The channel identifier.
   *
   * @since 1.0.0
   */
  id: string;

  /**
   * The human-friendly name of this channel (presented to the user).
   *
   * @since 1.0.0
   */
  name: string;

  /**
   * The description of this channel (presented to the user).
   *
   * @since 1.0.0
   */
  description?: string;

  /**
   * The sound that should be played for notifications posted to this channel.
   *
   * Notification channels with an importance of at least `3` should have a
   * sound.
   *
   * The file name of a sound file should be specified relative to the android
   * app `res/raw` directory.
   *
   * If the sound is not provided, or the sound file is not found no sound will be used.
   *
   * @since 1.0.0
   * @example "jingle.wav"
   */
  sound?: string;

  /**
   * The level of interruption for notifications posted to this channel.
   *
   * @default `3`
   * @since 1.0.0
   */
  importance?: Importance;

  /**
   * The visibility of notifications posted to this channel.
   *
   * This setting is for whether notifications posted to this channel appear on
   * the lockscreen or not, and if so, whether they appear in a redacted form.
   *
   * @since 1.0.0
   */
  visibility?: Visibility;

  /**
   * Whether notifications posted to this channel should display notification
   * lights, on devices that support it.
   *
   * @since 1.0.0
   */
  lights?: boolean;

  /**
   * The light color for notifications posted to this channel.
   *
   * Only supported if lights are enabled on this channel and the device
   * supports it.
   *
   * Supported color formats are `#RRGGBB` and `#RRGGBBAA`.
   *
   * @since 1.0.0
   */
  lightColor?: string;

  /**
   * Whether notifications posted to this channel should vibrate.
   *
   * @since 1.0.0
   */
  vibration?: boolean;
}

/**
 * Day of the week. Used for scheduling notifications on a particular weekday.
 */
export enum Weekday {
  Sunday = 1,
  Monday = 2,
  Tuesday = 3,
  Wednesday = 4,
  Thursday = 5,
  Friday = 6,
  Saturday = 7,
}

/**
 * The importance level. For more details, see the [Android Developer Docs](https://developer.android.com/reference/android/app/NotificationManager#IMPORTANCE_DEFAULT)
 * @since 1.0.0
 */
export type Importance = 1 | 2 | 3 | 4 | 5;

/**
 * The notification visibility. For more details, see the [Android Developer Docs](https://developer.android.com/reference/androidx/core/app/NotificationCompat#VISIBILITY_PRIVATE)
 * @since 1.0.0
 */
export type Visibility = -1 | 0 | 1;

/**
 * @deprecated Use 'Channel`.
 * @since 1.0.0
 */
export type NotificationChannel = Channel;

/**
 * @deprecated Use `LocalNotificationDescriptor`.
 * @since 1.0.0
 */
export type LocalNotificationRequest = LocalNotificationDescriptor;

/**
 * @deprecated Use `ScheduleResult`.
 * @since 1.0.0
 */
export type LocalNotificationScheduleResult = ScheduleResult;

/**
 * @deprecated Use `PendingResult`.
 * @since 1.0.0
 */
export type LocalNotificationPendingList = PendingResult;

/**
 * @deprecated Use `ActionType`.
 * @since 1.0.0
 */
export type LocalNotificationActionType = ActionType;

/**
 * @deprecated Use `Action`.
 * @since 1.0.0
 */
export type LocalNotificationAction = Action;

/**
 * @deprecated Use `EnabledResult`.
 * @since 1.0.0
 */
export type LocalNotificationEnabledResult = EnabledResult;

/**
 * @deprecated Use `ListChannelsResult`.
 * @since 1.0.0
 */
export type NotificationChannelList = ListChannelsResult;

/**
 * @deprecated Use `Attachment`.
 * @since 1.0.0
 */
export type LocalNotificationAttachment = Attachment;

/**
 * @deprecated Use `AttachmentOptions`.
 * @since 1.0.0
 */
export type LocalNotificationAttachmentOptions = AttachmentOptions;

/**
 * @deprecated Use `LocalNotificationSchema`.
 * @since 1.0.0
 */
export type LocalNotification = LocalNotificationSchema;

/**
 * @deprecated Use `Schedule`.
 * @since 1.0.0
 */
export type LocalNotificationSchedule = Schedule;

/**
 * @deprecated Use `ActionPerformed`.
 * @since 1.0.0
 */
export type LocalNotificationActionPerformed = ActionPerformed;
