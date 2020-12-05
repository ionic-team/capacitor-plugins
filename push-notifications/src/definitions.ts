export interface PushNotificationsPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
