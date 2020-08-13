declare module '@capacitor/core' {
  interface PluginRegistry {
    GeolocationPlugin: GeolocationPluginPlugin;
  }
}

export interface GeolocationPluginPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
