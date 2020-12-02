import type { PermissionState } from '@capacitor/core';
import { WebPlugin } from '@capacitor/core';

import type {
  LocalNotification,
  LocalNotificationEnabledResult,
  LocalNotificationPendingList,
  LocalNotificationScheduleResult,
  LocalNotificationsPermissionStatus,
  LocalNotificationsPlugin,
  NotificationChannelList,
} from './definitions';

export class LocalNotificationsWeb
  extends WebPlugin
  implements LocalNotificationsPlugin {
  protected pending: LocalNotification[] = [];

  async createChannel(): Promise<void> {
    throw this.unavailable('Feature not available for web.');
  }

  async deleteChannel(): Promise<void> {
    throw this.unavailable('Feature not available for web.');
  }

  async listChannels(): Promise<NotificationChannelList> {
    throw this.unavailable('Feature not available for web.');
  }

  async schedule(options: {
    notifications: LocalNotification[];
  }): Promise<LocalNotificationScheduleResult> {
    for (const notification of options.notifications) {
      this.sendNotification(notification);
    }

    return {
      notifications: options.notifications.map(notification => ({
        id: notification.id.toString(),
      })),
    };
  }

  async getPending(): Promise<LocalNotificationPendingList> {
    return {
      notifications: this.pending.map(notification => ({
        id: notification.id.toString(),
      })),
    };
  }

  async registerActionTypes(): Promise<void> {
    throw this.unavailable('Feature not available for web.');
  }

  async cancel(pending: LocalNotificationPendingList): Promise<void> {
    this.pending = this.pending.filter(
      notification =>
        !pending.notifications.find(n => n.id === notification.id.toString()),
    );
  }

  async areEnabled(): Promise<LocalNotificationEnabledResult> {
    const { display } = await this.checkPermissions();

    return {
      value: display === 'granted',
    };
  }

  async requestPermissions(): Promise<LocalNotificationsPermissionStatus> {
    const display = this.transformNotificationPermission(
      await Notification.requestPermission(),
    );

    return { display };
  }

  async checkPermissions(): Promise<LocalNotificationsPermissionStatus> {
    const display = this.transformNotificationPermission(
      Notification.permission,
    );

    return { display };
  }

  protected transformNotificationPermission(
    permission: NotificationPermission,
  ): PermissionState {
    switch (permission) {
      case 'granted':
        return 'granted';
      case 'denied':
        return 'denied';
      default:
        return 'prompt';
    }
  }

  protected sendPending(): void {
    const toRemove: LocalNotification[] = [];
    const now = new Date().getTime();

    for (const notification of this.pending) {
      if (
        notification.schedule?.at &&
        notification.schedule.at.getTime() <= now
      ) {
        this.buildNotification(notification);
        toRemove.push(notification);
      }
    }

    this.pending = this.pending.filter(
      notification => !toRemove.find(n => n === notification),
    );
  }

  protected sendNotification(notification: LocalNotification): void {
    if (notification.schedule?.at) {
      const diff = notification.schedule.at.getTime() - new Date().getTime();

      this.pending.push(notification);
      setTimeout(() => {
        this.sendPending();
      }, diff);
    }
  }

  protected buildNotification(notification: LocalNotification): Notification {
    return new Notification(notification.title, {
      body: notification.body,
    });
  }
}
