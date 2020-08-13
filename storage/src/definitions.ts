declare module '@capacitor/core' {
  interface PluginRegistry {
    Storage: StoragePlugin;
  }
}

export interface StoragePlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
