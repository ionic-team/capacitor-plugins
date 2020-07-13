declare module '@capacitor/core' {
  interface PluginRegistry {
    ScreenReader: ScreenReaderPlugin;
  }
}

export interface ScreenReaderPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
