import type { PluginImplementations } from '@capacitor/core';
import { Plugins, registerPlugin } from '@capacitor/core';

import type {
  LocalNotification,
  LocalNotificationAction,
  LocalNotificationActionPerformed,
  LocalNotificationActionType,
  LocalNotificationAttachment,
  LocalNotificationAttachmentOptions,
  LocalNotificationEnabledResult,
  LocalNotificationPendingList,
  LocalNotificationRequest,
  LocalNotificationSchedule,
  LocalNotificationScheduleResult,
  LocalNotificationsPlugin,
  NotificationChannel,
  NotificationChannelList,
} from './definitions';
import { LocalNotificationsWeb } from './web';

const implementations: PluginImplementations<LocalNotificationsPlugin> = {
  android: Plugins.LocalNotifications,
  ios: Plugins.LocalNotifications,
  web: new LocalNotificationsWeb(),
};

const LocalNotifications = registerPlugin(
  'LocalNotifications',
  implementations,
).getImplementation();

export {
  LocalNotification,
  LocalNotificationAction,
  LocalNotificationActionPerformed,
  LocalNotificationActionType,
  LocalNotificationAttachment,
  LocalNotificationAttachmentOptions,
  LocalNotificationEnabledResult,
  LocalNotificationPendingList,
  LocalNotificationRequest,
  LocalNotificationSchedule,
  LocalNotificationScheduleResult,
  LocalNotifications,
  NotificationChannel,
  NotificationChannelList,
};
