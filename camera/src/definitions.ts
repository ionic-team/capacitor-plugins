declare module '@capacitor/core' {
  interface PluginRegistry {
    Camera: CameraPlugin;
  }
}

export interface CameraPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
