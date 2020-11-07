import type { PermissionState } from '@capacitor/core';
import { WebPlugin } from '@capacitor/core';

import type {
  LocalNotification,
  LocalNotificationActionType,
  LocalNotificationEnabledResult,
  LocalNotificationPendingList,
  LocalNotificationScheduleResult,
  LocalNotificationsPermissionStatus,
  LocalNotificationsPlugin,
  NotificationChannel,
  NotificationChannelList,
} from './definitions';

export class LocalNotificationsWeb
  extends WebPlugin
  implements LocalNotificationsPlugin {
  private pending: LocalNotification[] = [];

  constructor() {
    super({ name: 'LocalNotifications' });
  }

  createChannel(channel: NotificationChannel): Promise<void> {
    throw new Error('Feature not available in the browser. ' + channel.id);
  }

  deleteChannel(channel: NotificationChannel): Promise<void> {
    throw new Error('Feature not available in the browser. ' + channel.id);
  }

  listChannels(): Promise<NotificationChannelList> {
    throw new Error('Feature not available in the browser');
  }

  sendPending(): void {
    const toRemove: LocalNotification[] = [];
    const now = +new Date();
    this.pending.forEach(localNotification => {
      if (localNotification.schedule?.at) {
        if (+localNotification.schedule.at <= now) {
          this.buildNotification(localNotification);
          toRemove.push(localNotification);
        }
      }
    });
    console.log('Sent pending, removing', toRemove);

    this.pending = this.pending.filter(
      localNotification => !toRemove.find(ln => ln === localNotification),
    );
  }

  // TODO
  sendNotification(
    localNotification: LocalNotification,
  ): any /* Notification */ {
    const l = localNotification;

    if (localNotification.schedule?.at) {
      const diff = +localNotification.schedule.at - +new Date();
      this.pending.push(l);
      setTimeout(() => {
        this.sendPending();
      }, diff);
      return;
    }

    this.buildNotification(localNotification);
  }

  buildNotification(localNotification: LocalNotification): Notification {
    const l = localNotification;
    return new Notification(l.title, {
      body: l.body,
    });
  }

  schedule(options: {
    notifications: LocalNotification[];
  }): Promise<LocalNotificationScheduleResult> {
    const notifications: Notification[] = [];
    options.notifications.forEach(notification => {
      notifications.push(this.sendNotification(notification));
    });

    return Promise.resolve({
      notifications: options.notifications.map(notification => {
        return { id: '' + notification.id };
      }),
    });
  }

  getPending(): Promise<LocalNotificationPendingList> {
    return Promise.resolve({
      notifications: this.pending.map(localNotification => {
        return {
          id: '' + localNotification.id,
        };
      }),
    });
  }

  registerActionTypes(_options: {
    types: LocalNotificationActionType[];
  }): Promise<void> {
    throw new Error('Method not implemented.');
  }

  cancel(pending: LocalNotificationPendingList): Promise<void> {
    console.log('Cancel these', pending);
    this.pending = this.pending.filter(
      localNotification =>
        !pending.notifications.find(ln => ln.id === '' + localNotification.id),
    );
    return Promise.resolve();
  }

  areEnabled(): Promise<LocalNotificationEnabledResult> {
    return Promise.resolve({
      value: Notification.permission === 'granted',
    });
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
}
