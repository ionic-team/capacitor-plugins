declare module '@capacitor/core' {
  interface PluginRegistry {
    Filesystem: FilesystemPlugin;
  }
}

export interface FilesystemPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
