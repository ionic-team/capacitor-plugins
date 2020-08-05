declare module '@capacitor/core' {
  interface PluginRegistry {
    TextZoom: TextZoomPlugin;
  }
}

export interface TextZoomPlugin {
  get(): Promise<{ value: number }>;
  getPreferred(): Promise<{ value: number }>;
  set(options: { value: number }): Promise<void>;
}
