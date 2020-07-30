declare module '@capacitor/core' {
  interface PluginRegistry {
    Haptics: HapticsPlugin;
  }
}

export interface HapticsPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
