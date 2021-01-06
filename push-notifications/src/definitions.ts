import type { PermissionState, PluginListenerHandle } from '@capacitor/core';

export interface PushNotificationsPlugin {
  /**
   * Register the app to receive push notifications.
   *
   * Will trigger registration event with the push token or registrationError if there was some problem.
   *
   * Doesn't prompt the user for notification permissions, use requestPermission() first.
   *
   * @since 1.0.0
   */
  register(): Promise<void>;

  /**
   * Returns the notifications that are visible on the notifications screen.
   *
   * @since 1.0.0
   */
  getDeliveredNotifications(): Promise<PushNotificationDeliveredList>;

  /**
   * Removes the specified notifications from the notifications screen.
   *
   * @param delivered list of delivered notifications.
   * @since 1.0.0
   */
  removeDeliveredNotifications(
    delivered: PushNotificationDeliveredList,
  ): Promise<void>;

  /**
   * Removes all the notifications from the notifications screen.
   *
   * @since 1.0.0
   */
  removeAllDeliveredNotifications(): Promise<void>;

  /**
   * On Android O or newer (SDK 26+) creates a notification channel.
   *
   * @param channel to create.
   * @since 1.0.0
   */
  createChannel(channel: Channel): Promise<void>;

  /**
   * On Android O or newer (SDK 26+) deletes a notification channel.
   *
   * @param channel to delete.
   * @since 1.0.0
   */
  deleteChannel(channel: Channel): Promise<void>;

  /**
   * On Android O or newer (SDK 26+) list the available notification channels.
   *
   * @since 1.0.0
   */
  listChannels(): Promise<ListChannelsResult>;

  /**
   * Check permission to receive push notifications.
   *
   * @since 1.0.0
   */
  checkPermissions(): Promise<PermissionStatus>;

  /**
   * Request permission to receive push notifications.
   *
   * @since 1.0.0
   */
  requestPermissions(): Promise<PermissionStatus>;

  /**
   * Event called when the push notification registration finished without problems.
   * Provides the push notification token.
   *
   * @param eventName registration.
   * @param listenerFunc callback with the push token.
   * @since 1.0.0
   */
  addListener(
    eventName: 'registration',
    listenerFunc: (token: PushNotificationToken) => void,
  ): PluginListenerHandle;

  /**
   * Event called when the push notification registration finished with problems.
   * Provides an error with the registration problem.
   *
   * @param eventName registrationError.
   * @param listenerFunc callback with the registration error.
   * @since 1.0.0
   */
  addListener(
    eventName: 'registrationError',
    listenerFunc: (error: any) => void,
  ): PluginListenerHandle;

  /**
   * Event called when the device receives a push notification.
   *
   * @param eventName pushNotificationReceived.
   * @param listenerFunc callback with the received notification.
   * @since 1.0.0
   */
  addListener(
    eventName: 'pushNotificationReceived',
    listenerFunc: (notification: PushNotificationSchema) => void,
  ): PluginListenerHandle;

  /**
   * Event called when an action is performed on a pusn notification.
   *
   * @param eventName pushNotificationActionPerformed.
   * @param listenerFunc callback with the notification action.
   * @since 1.0.0
   */
  addListener(
    eventName: 'pushNotificationActionPerformed',
    listenerFunc: (notification: PushNotificationActionPerformed) => void,
  ): PluginListenerHandle;

  /**
   * Remove all native listeners for this plugin.
   *
   * @since 1.0.0
   */
  removeAllListeners(): void;
}

export interface PushNotificationSchema {
  /**
   * The notification title
   * @since 1.0.0
   */
  title?: string;
  /**
   * The notification sub title
   * @since 1.0.0
   */
  subtitle?: string;
  /**
   * The main text payload for the notification
   * @since 1.0.0
   */
  body?: string;
  /**
   * The notification identifier
   * @since 1.0.0
   */
  id: string;
  /**
   * The number to display for the app icon badge
   * @since 1.0.0
   */
  badge?: number;
  /**
   * @since 1.0.0
   */
  notification?: any;
  /**
   * The notification identifier
   * @since 1.0.0
   */
  data: any;
  /**
   * @since 1.0.0
   */
  click_action?: string;
  /**
   * @since 1.0.0
   */
  link?: string;

  /**
   * Android only: set the group identifier for notification grouping, like
   * threadIdentifier on iOS.
   *
   * @since 1.0.0
   */
  group?: string;

  /**
   * Android only: designate this notification as the summary for a group
   * (should be used with the `group` property).
   *
   * @since 1.0.0
   */
  groupSummary?: boolean;
}

export interface PushNotificationActionPerformed {
  /**
   * @since 1.0.0
   */
  actionId: string;
  /**
   * @since 1.0.0
   */
  inputValue?: string;
  /**
   * @since 1.0.0
   */
  notification: PushNotificationSchema;
}

export interface PushNotificationToken {
  /**
   * @since 1.0.0
   */
  value: string;
}

export interface PushNotificationDeliveredList {
  /**
   * @since 1.0.0
   */
  notifications: PushNotificationSchema[];
}

export interface Channel {
  /**
   * Android only: The channel identifier
   * @since 1.0.0
   */
  id: string;
  /**
   * Android only: Sets the user visible name of this channel.
   *
   * @since 1.0.0
   */
  name: string;
  /**
   * Android only: Sets the user visible description of this channel.
   *
   * @since 1.0.0
   */
  description?: string;
  /**
   * Android only: Sets the sound that should be played for notifications posted to this channel.
   * Notification channels with an importance of at least 3 should have a sound.
   *
   * Should specifify the file name of a sound file relative to the android app res/raw directory.
   * @since 1.0.0
   * @example "jingle.wav"
   */
  sound?: string;
  /**
   * Android only: Sets the level of interruption of this notification channel.
   *
   * @since 1.0.0
   */
  importance: 1 | 2 | 3 | 4 | 5;
  /**
   * Android only: Sets whether notifications posted to this channel appear on the
   * lockscreen or not, and if so, whether they appear in a redacted form.
   *
   * @since 1.0.0
   */
  visibility?: -1 | 0 | 1;
  /**
   * Android only: Sets whether notifications posted to this channel should display notification lights, on devices that support that feature.
   *
   * @since 1.0.0
   */
  lights?: boolean;
  /**
   * Android only: Sets the notification light color for notifications posted to this channel, if lights are enabled on this channel and the device supports that feature.
   *
   * Supported color formats:
   *
   * #RRGGBB
   * #AARRGGBB
   *
   * The following names are also accepted: red, blue, green, black, white, gray, cyan, magenta, yellow, lightgray, darkgray, grey, lightgrey, darkgrey, aqua, fuchsia, lime, maroon, navy, olive, purple, silver, and teal
   *
   * @since 1.0.0
   */
  lightColor?: string;
  /**
   * Android only: Sets whether notification posted to this channel should vibrate.
   *
   * @since 1.0.0
   */
  vibration?: boolean;
}

export interface ListChannelsResult {
  /**
   * @since 1.0.0
   */
  channels: Channel[];
}

export interface PermissionStatus {
  /**
   * @since 1.0.0
   */
  receive: PermissionState;
}
