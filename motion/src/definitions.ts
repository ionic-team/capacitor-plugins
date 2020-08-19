declare module '@capacitor/core' {
  interface PluginRegistry {
    Motion: MotionPlugin;
  }
}

export interface MotionPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
