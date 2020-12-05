/// <reference types="@capacitor/cli" />

import type { PermissionState, PluginListenerHandle } from '@capacitor/core';

declare module '@capacitor/cli' {
  export interface PluginsConfig {
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
       */
      smallIcon?: string;

      /**
       * Set the default color of status bar icons for notifications.
       *
       * Only available for Android.
       *
       * @since 1.0.0
       */
      iconColor?: string;

      /**
       * Set the default notification sound for notifications.
       *
       * On Android 26+ it sets the default channel sound and can't be
       * changed unless the app is uninstalled.
       *
       * Only available for Android.
       *
       * @since 1.0.0
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
  deleteChannel(channel: Channel): Promise<void>;

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
   * Listen for when notifications are displayed.
   *
   * @since 1.0.0
   */
  addListener(
    eventName: 'received',
    listenerFunc: (notification: LocalNotificationSchema) => void,
  ): PluginListenerHandle;

  /**
   * Listen for when an action is performed on a notification.
   *
   * @since 1.0.0
   */
  addListener(
    eventName: 'actionPerformed',
    listenerFunc: (notificationAction: ActionPerformed) => void,
  ): PluginListenerHandle;

  /**
   * Remove all listeners for this plugin.
   *
   * @since 1.0.0
   */
  removeAllListeners(): void;
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
  id: string;
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
  notifications: LocalNotificationDescriptor[];
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
   * Only available for iOS 11+.
   *
   * @since 1.0.0
   */
  iosHiddenPreviewsBodyPlaceholder?: string; // >= iOS 11 only

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
   * Only available for iOS 11+.
   *
   * @since 1.0.0
   */
  iosHiddenPreviewsShowTitle?: boolean;

  /**
   * Sets `hiddenPreviewsShowSubtitle` in the options of the
   * [`UNNotificationCategory`](https://developer.apple.com/documentation/usernotifications/unnotificationcategory).
   *
   * Only available for iOS 11+.
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
   * Name of the audio file to play when this notification is displayed.
   *
   * Include the file extension with the filename.
   *
   * On iOS, the file should be in the app bundle.
   * On Android, the file should be in res/raw folder.
   *
   * Recommended format is `.wav` because is supported by both iOS and Android.
   *
   * Only available for iOS and Android 26+.
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
   * Only available for iOS 12+.
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
   * Only available for Android 26+.
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
   * Schedule a notification on particular interval(s).
   *
   * This is similar to scheduling [cron](https://en.wikipedia.org/wiki/Cron)
   * jobs.
   *
   * Only available for iOS and Android.
   *
   * @since 1.0.0
   */
  on?: {
    year?: number;
    month?: number;
    day?: number;
    hour?: number;
    minute?: number;
  };

  /**
   * Schedule a notification on a particular interval.
   *
   * @since 1.0.0
   */
  every?:
    | 'year'
    | 'month'
    | 'two-weeks'
    | 'week'
    | 'day'
    | 'hour'
    | 'minute'
    | 'second';

  /**
   * Limit the number times a notification is delivered by the interval
   * specified by `every`.
   *
   * @since 1.0.0
   */
  count?: number;
}

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

export interface Channel {
  /**
   * The channel identifier.
   *
   * @since 1.0.0
   */
  id: string;

  /**
   * The channel name.
   *
   * @since 1.0.0
   */
  name: string;

  /**
   * The channel description.
   *
   * @since 1.0.0
   */
  description?: string;

  /**
   * The sound that is played for notifications posted to this channel.
   *
   * @since 1.0.0
   */
  sound?: string;

  /**
   * The level of interruption of notifications posted to this channel.
   *
   * See the `PRIORITY_*` constants of
   * [`NotificationCompat`](https://developer.android.com/reference/androidx/core/app/NotificationCompat)
   * for more information.
   *
   * @since 1.0.0
   */
  importance: 1 | 2 | 3 | 4 | 5;

  /**
   * The visibility level of notifications posted to this channel.
   *
   * See the `VISIBILITY_*` constants of
   * [`NotificationCompat`](https://developer.android.com/reference/androidx/core/app/NotificationCompat)
   * for more information.
   *
   * @since 1.0.0
   */
  visibility?: -1 | 0 | 1;

  /**
   * Whether or not notifications posted to this channel should display
   * notification lights.
   *
   * @since 1.0.0
   */
  lights?: boolean;

  /**
   * The color of notification lights when using the `lights` option.
   *
   * This can be any value that
   * [`Color.parseColor()`](https://developer.android.com/reference/android/graphics/Color#parseColor(java.lang.String))
   * expects.
   *
   * @since 1.0.0
   */
  lightColor?: string;

  /**
   * Whether or not notifications posted to this channel should vibrate the
   * device.
   *
   * @since 1.0.0
   */
  vibration?: boolean;
}
