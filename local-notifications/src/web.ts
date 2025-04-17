import { WebPlugin } from '@capacitor/core';
import type { PermissionState } from '@capacitor/core';

import type {
  DeliveredNotifications,
  EnabledResult,
  ListChannelsResult,
  LocalNotificationSchema,
  LocalNotificationsPlugin,
  PendingResult,
  PermissionStatus,
  ScheduleOptions,
  ScheduleResult,
  SettingsPermissionStatus,
} from './definitions';

export class LocalNotificationsWeb
  extends WebPlugin
  implements LocalNotificationsPlugin
{
  protected pending: LocalNotificationSchema[] = [];
  protected deliveredNotifications: Notification[] = [];

  async getDeliveredNotifications(): Promise<DeliveredNotifications> {
    const deliveredSchemas = [];
    for (const notification of this.deliveredNotifications) {
      const deliveredSchema: LocalNotificationSchema = {
        title: notification.title,
        id: parseInt(notification.tag),
        body: notification.body,
      };
      deliveredSchemas.push(deliveredSchema);
    }
    return {
      notifications: deliveredSchemas,
    };
  }
  async removeDeliveredNotifications(
    delivered: DeliveredNotifications,
  ): Promise<void> {
    for (const toRemove of delivered.notifications) {
      const found = this.deliveredNotifications.find(
        n => n.tag === String(toRemove.id),
      );
      found?.close();
      this.deliveredNotifications = this.deliveredNotifications.filter(
        () => !found,
      );
    }
  }
  async removeAllDeliveredNotifications(): Promise<void> {
    for (const notification of this.deliveredNotifications) {
      notification.close();
    }
    this.deliveredNotifications = [];
  }
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
    if (!this.hasNotificationSupport()) {
      throw this.unavailable('Notifications not supported in this browser.');
    }

    for (const notification of options.notifications) {
      this.sendNotification(notification);
    }

    return {
      notifications: options.notifications.map(notification => ({
        id: notification.id,
      })),
    };
  }

  async getPending(): Promise<PendingResult> {
    return {
      notifications: this.pending,
    };
  }

  async registerActionTypes(): Promise<void> {
    throw this.unimplemented('Not implemented on web.');
  }

  async cancel(pending: ScheduleResult): Promise<void> {
    this.pending = this.pending.filter(
      notification =>
        !pending.notifications.find(n => n.id === notification.id),
    );
  }

  async areEnabled(): Promise<EnabledResult> {
    const { display } = await this.checkPermissions();

    return {
      value: display === 'granted',
    };
  }

  async changeExactNotificationSetting(): Promise<SettingsPermissionStatus> {
    throw this.unimplemented('Not implemented on web.');
  }

  async checkExactNotificationSetting(): Promise<SettingsPermissionStatus> {
    throw this.unimplemented('Not implemented on web.');
  }

  async requestPermissions(): Promise<PermissionStatus> {
    if (!this.hasNotificationSupport()) {
      throw this.unavailable('Notifications not supported in this browser.');
    }

    const display = this.transformNotificationPermission(
      await Notification.requestPermission(),
    );

    return { display };
  }

  async checkPermissions(): Promise<PermissionStatus> {
    if (!this.hasNotificationSupport()) {
      throw this.unavailable('Notifications not supported in this browser.');
    }

    const display = this.transformNotificationPermission(
      Notification.permission,
    );

    return { display };
  }

  protected hasNotificationSupport = (): boolean => {
    if (!('Notification' in window) || !Notification.requestPermission) {
      return false;
    }

    if (Notification.permission !== 'granted') {
      // don't test for `new Notification` if permission has already been granted
      // otherwise this sends a real notification on supported browsers
      try {
        new Notification('');
      } catch (e) {
        if (e.name == 'TypeError') {
          return false;
        }
      }
    }

    return true;
  };

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
      return;
    }
    this.buildNotification(notification);
  }

  protected buildNotification(
    notification: LocalNotificationSchema,
  ): Notification {
    const localNotification = new Notification(notification.title, {
      body: notification.body,
      tag: String(notification.id),
    });
    localNotification.addEventListener(
      'click',
      this.onClick.bind(this, notification),
      false,
    );
    localNotification.addEventListener(
      'show',
      this.onShow.bind(this, notification),
      false,
    );
    localNotification.addEventListener(
      'close',
      () => {
        this.deliveredNotifications = this.deliveredNotifications.filter(
          () => !this,
        );
      },
      false,
    );
    this.deliveredNotifications.push(localNotification);
    return localNotification;
  }

  protected onClick(notification: LocalNotificationSchema): void {
    const data = {
      actionId: 'tap',
      notification,
    };
    this.notifyListeners('localNotificationActionPerformed', data);
  }

  protected onShow(notification: LocalNotificationSchema): void {
    this.notifyListeners('localNotificationReceived', notification);
  }
}
