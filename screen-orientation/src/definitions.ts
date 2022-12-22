export interface ScreenOrientationPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
