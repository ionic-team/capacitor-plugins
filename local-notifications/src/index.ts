import { registerPlugin } from '@capacitor/core';

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
  LocalNotificationsPermissionStatus,
  NotificationChannel,
  NotificationChannelList,
} from './definitions';

const LocalNotifications = registerPlugin<LocalNotificationsPlugin>(
  'LocalNotifications',
  {
    web: () => import('./web').then(m => new m.LocalNotificationsWeb()),
  },
);

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
  LocalNotificationsPermissionStatus,
  NotificationChannel,
  NotificationChannelList,
};
