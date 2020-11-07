import type { PluginListenerHandle } from '@capacitor/core';

declare module '@capacitor/core' {
  interface PluginRegistry {
    LocalNotifications: LocalNotificationsPlugin;
  }
}

export interface LocalNotificationsPlugin {
  schedule(options: {
    notifications: LocalNotification[];
  }): Promise<LocalNotificationScheduleResult>;
  getPending(): Promise<LocalNotificationPendingList>;
  registerActionTypes(options: {
    types: LocalNotificationActionType[];
  }): Promise<void>;
  cancel(pending: LocalNotificationPendingList): Promise<void>;
  areEnabled(): Promise<LocalNotificationEnabledResult>;
  createChannel(channel: NotificationChannel): Promise<void>;
  deleteChannel(channel: NotificationChannel): Promise<void>;
  listChannels(): Promise<NotificationChannelList>;
  // TODO
  // requestPermission(): Promise<NotificationPermissionResponse>;
  addListener(
    eventName: 'localNotificationReceived',
    listenerFunc: (notification: LocalNotification) => void,
  ): PluginListenerHandle;
  addListener(
    eventName: 'localNotificationActionPerformed',
    listenerFunc: (
      notificationAction: LocalNotificationActionPerformed,
    ) => void,
  ): PluginListenerHandle;

  /**
   * Remove all native listeners for this plugin
   */
  removeAllListeners(): void;
}

export interface LocalNotificationRequest {
  id: string;
}

export interface LocalNotificationPendingList {
  notifications: LocalNotificationRequest[];
}

export type LocalNotificationScheduleResult = LocalNotificationPendingList;

export interface LocalNotificationActionType {
  id: string;
  actions?: LocalNotificationAction[];
  iosHiddenPreviewsBodyPlaceholder?: string; // >= iOS 11 only
  iosCustomDismissAction?: boolean;
  iosAllowInCarPlay?: boolean;
  iosHiddenPreviewsShowTitle?: boolean; // >= iOS 11 only
  iosHiddenPreviewsShowSubtitle?: boolean; // >= iOS 11 only
}

export interface LocalNotificationAction {
  id: string;
  title: string;
  requiresAuthentication?: boolean;
  foreground?: boolean;
  destructive?: boolean;
  input?: boolean;
  inputButtonTitle?: string;
  inputPlaceholder?: string;
}

export interface LocalNotificationAttachment {
  id: string;
  url: string;
  options?: LocalNotificationAttachmentOptions;
}

export interface LocalNotificationAttachmentOptions {
  iosUNNotificationAttachmentOptionsTypeHintKey?: string;
  iosUNNotificationAttachmentOptionsThumbnailHiddenKey?: string;
  iosUNNotificationAttachmentOptionsThumbnailClippingRectKey?: string;
  iosUNNotificationAttachmentOptionsThumbnailTimeKey?: string;
}

export interface LocalNotification {
  title: string;
  body: string;
  id: number;
  schedule?: LocalNotificationSchedule;
  /**
   * Name of the audio file with extension.
   * On iOS the file should be in the app bundle.
   * On Android the file should be on res/raw folder.
   * Doesn't work on Android version 26+ (Android O and newer), for
   * Recommended format is .wav because is supported by both platforms.
   */
  sound?: string;
  /**
   * Android-only: set a custom statusbar icon.
   * If set, it overrides default icon from capacitor.config.json
   */
  smallIcon?: string;
  /**
   * Android only: set the color of the notification icon
   */
  iconColor?: string;
  attachments?: LocalNotificationAttachment[];
  actionTypeId?: string;
  extra?: any;
  /**
   * iOS only: set the thread identifier for notification grouping
   */
  threadIdentifier?: string;
  /**
   * iOS 12+ only: set the summary argument for notification grouping
   */
  summaryArgument?: string;
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
  /**
   * Android only: set the notification channel on which local notification
   * will generate. If channel with the given name does not exist then the
   * notification will not fire. If not provided, it will use the default channel.
   */
  channelId?: string;
  /**
   * Android only: set the notification ongoing.
   * If set to true the notification can't be swiped away.
   */
  ongoing?: boolean;
  /**
   * Android only: set the notification to be removed automatically when the user clicks on it
   */
  autoCancel?: boolean;
}

export interface LocalNotificationSchedule {
  at?: Date;
  repeats?: boolean;
  every?:
    | 'year'
    | 'month'
    | 'two-weeks'
    | 'week'
    | 'day'
    | 'hour'
    | 'minute'
    | 'second';
  count?: number;
  on?: {
    year?: number;
    month?: number;
    day?: number;
    hour?: number;
    minute?: number;
  };
}

export interface LocalNotificationActionPerformed {
  actionId: string;
  inputValue?: string;
  notification: LocalNotification;
}

export interface LocalNotificationEnabledResult {
  /**
   * Whether the device has Local Notifications enabled or not
   */
  value: boolean;
}

export interface NotificationChannel {
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

export interface NotificationChannelList {
  channels: NotificationChannel[];
}
