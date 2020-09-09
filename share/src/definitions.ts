declare module '@capacitor/core' {
  interface PluginRegistry {
    Share: SharePlugin;
  }
}

export interface SharePlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
