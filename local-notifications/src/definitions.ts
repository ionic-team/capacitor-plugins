declare module '@capacitor/core' {
  interface PluginRegistry {
    LocalNotifications: LocalNotificationsPlugin;
  }
}

export interface LocalNotificationsPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
