declare module '@capacitor/core' {
  interface PluginRegistry {
    TextZoom: TextZoomPlugin;
  }
}

export interface TextZoomPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
