declare module '@capacitor/core' {
  interface PluginRegistry {
    Notifications: NotificationsPlugin;
  }
}

export interface NotificationsPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
