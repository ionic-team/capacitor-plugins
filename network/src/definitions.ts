declare module '@capacitor/core' {
  interface PluginRegistry {
    Network: NetworkPlugin;
  }
}

export interface NetworkPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
