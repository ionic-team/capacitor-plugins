declare module '@capacitor/core' {
  interface PluginRegistry {
    Clipboard: ClipboardPlugin;
  }
}

export interface ClipboardPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
