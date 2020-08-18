declare module '@capacitor/core' {
  interface PluginRegistry {
    Browser: BrowserPlugin;
  }
}

export interface BrowserPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
