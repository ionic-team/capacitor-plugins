import type { PermissionState } from '@capacitor/core';
import { WebPlugin } from '@capacitor/core';

import type {
  EnabledResult,
  ListChannelsResult,
  LocalNotificationSchema,
  LocalNotificationsPlugin,
  PermissionStatus,
  ScheduleOptions,
  ScheduleResult,
} from './definitions';

export class LocalNotificationsWeb
  extends WebPlugin
  implements LocalNotificationsPlugin {
  protected pending: LocalNotificationSchema[] = [];

  async createChannel(): Promise<void> {
    throw this.unimplemented('Not implemented on web.');
  }

  async deleteChannel(): Promise<void> {
    throw this.unimplemented('Not implemented on web.');
  }

  async listChannels(): Promise<ListChannelsResult> {
    throw this.unimplemented('Not implemented on web.');
  }

  async schedule(options: ScheduleOptions): Promise<ScheduleResult> {
    for (const notification of options.notifications) {
      this.sendNotification(notification);
    }

    return {
      notifications: options.notifications.map(notification => ({
        id: notification.id.toString(),
      })),
    };
  }

  async getPending(): Promise<ScheduleResult> {
    return {
      notifications: this.pending.map(notification => ({
        id: notification.id.toString(),
      })),
    };
  }

  async registerActionTypes(): Promise<void> {
    throw this.unimplemented('Not implemented on web.');
  }

  async cancel(pending: ScheduleResult): Promise<void> {
    this.pending = this.pending.filter(
      notification =>
        !pending.notifications.find(n => n.id === notification.id.toString()),
    );
  }

  async areEnabled(): Promise<EnabledResult> {
    const { display } = await this.checkPermissions();

    return {
      value: display === 'granted',
    };
  }

  async requestPermissions(): Promise<PermissionStatus> {
    const display = this.transformNotificationPermission(
      await Notification.requestPermission(),
    );

    return { display };
  }

  async checkPermissions(): Promise<PermissionStatus> {
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
    const toRemove: LocalNotificationSchema[] = [];
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

  protected sendNotification(notification: LocalNotificationSchema): void {
    if (notification.schedule?.at) {
      const diff = notification.schedule.at.getTime() - new Date().getTime();

      this.pending.push(notification);
      setTimeout(() => {
        this.sendPending();
      }, diff);
    }
  }

  protected buildNotification(
    notification: LocalNotificationSchema,
  ): Notification {
    return new Notification(notification.title, {
      body: notification.body,
    });
  }
}
