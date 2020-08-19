import { PluginListenerHandle } from '@capacitor/core';

declare module '@capacitor/core' {
  interface PluginRegistry {
    Motion: MotionPlugin;
  }
}

export interface MotionPlugin {
  /**
   * Listen for accelerometer data
   */
  addListener(
    eventName: 'accel',
    listenerFunc: (event: MotionEventResult) => void,
  ): PluginListenerHandle;
  /**
   * Listen for device orientation change (compass heading, etc.)
   */
  addListener(
    eventName: 'orientation',
    listenerFunc: (event: MotionOrientationEventResult) => void,
  ): PluginListenerHandle;

  /**
   * Remove all native listeners for this plugin
   */
  removeAllListeners(): void;
}

export type MotionWatchOrientationCallback = (
  accel: MotionOrientationEventResult,
) => void;
export type MotionWatchAccelCallback = (accel: MotionEventResult) => void;

export interface MotionOrientationEventResult {
  alpha: number;
  beta: number;
  gamma: number;
}

export interface MotionEventResult {
  acceleration: {
    x: number;
    y: number;
    z: number;
  };
  accelerationIncludingGravity: {
    x: number;
    y: number;
    z: number;
  };
  rotationRate: {
    alpha: number;
    beta: number;
    gamma: number;
  };
  interval: number;
}
