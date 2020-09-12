import type { PluginListenerHandle } from '@capacitor/core';

declare module '@capacitor/core' {
  interface PluginRegistry {
    Motion: MotionPlugin;
  }
}

export interface MotionPlugin {
  /**
   * Add a listener for accelerometer data
   *
   * @since 1.0.0
   */
  addListener(
    eventName: 'accel',
    listenerFunc: (event: MotionEventResult) => void,
  ): PluginListenerHandle;

  /**
   * Add a listener for device orientation change (compass heading, etc.)
   *
   * @since 1.0.0
   */
  addListener(
    eventName: 'orientation',
    listenerFunc: (event: MotionOrientationEventResult) => void,
  ): PluginListenerHandle;

  /**
   * Remove all the listeners that are attached to this plugin.
   *
   * @since 1.0.0
   */
  removeAllListeners(): void;
}

export interface DeviceMotionEventRotationRate {
  /**
   * The amount of rotation around the Z axis, in degrees per second.
   *
   * @since 1.0.0
   */
  alpha: number;

  /**
   * The amount of rotation around the X axis, in degrees per second.
   *
   * @since 1.0.0
   */
  beta: number;

  /**
   * The amount of rotation around the Y axis, in degrees per second.
   *
   * @since 1.0.0
   */
  gamma: number;
}

export type MotionOrientationEventResult = DeviceMotionEventRotationRate;

export interface DeviceMotionEventAcceleration {
  /**
   * The amount of acceleration along the X axis.
   *
   * @since 1.0.0
   */
  x: number;

  /**
   * The amount of acceleration along the Y axis.
   *
   * @since 1.0.0
   */
  y: number;

  /**
   * The amount of acceleration along the Z axis.
   *
   * @since 1.0.0
   */
  z: number;
}

export interface MotionEventResult {
  /**
   * An object giving the acceleration of the device on the three axis X, Y and Z. Acceleration is expressed in m/s
   *
   * @since 1.0.0
   */
  acceleration: DeviceMotionEventAcceleration;

  /**
   * An object giving the acceleration of the device on the three axis X, Y and Z with the effect of gravity. Acceleration is expressed in m/s
   *
   * @since 1.0.0
   */
  accelerationIncludingGravity: DeviceMotionEventAcceleration;

  /**
   * An object giving the rate of change of the device's orientation on the three orientation axis alpha, beta and gamma. Rotation rate is expressed in degrees per seconds.
   *
   * @since 1.0.0
   */
  rotationRate: DeviceMotionEventRotationRate;

  /**
   * A number representing the interval of time, in milliseconds, at which data is obtained from the device.
   *
   * @since 1.0.0
   */
  interval: number;
}
