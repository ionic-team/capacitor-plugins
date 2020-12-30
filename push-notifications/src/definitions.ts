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
  title?: string;
  subtitle?: string;
  body?: string;
  id: string;
  badge?: number;
  notification?: any;
  data: any;
  click_action?: string;
  link?: string;

  /**
   * Android only: set the group identifier for notification grouping, like
   * threadIdentifier on iOS.
   */
  group?: string;

  /**
   * Android only: designate this notification as the summary for a group
   * (should be used with the `group` property).
   */
  groupSummary?: boolean;
}

export interface PushNotificationActionPerformed {
  actionId: string;
  inputValue?: string;
  notification: PushNotificationSchema;
}

export interface PushNotificationToken {
  value: string;
}

export interface PushNotificationDeliveredList {
  notifications: PushNotificationSchema[];
}

export interface Channel {
  id: string;
  name: string;
  description?: string;
  sound?: string;
  importance: 1 | 2 | 3 | 4 | 5;
  visibility?: -1 | 0 | 1;
  lights?: boolean;
  lightColor?: string;
  vibration?: boolean;
}

export interface ListChannelsResult {
  channels: Channel[];
}

export interface PermissionStatus {
  receive: PermissionState;
}
