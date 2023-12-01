import type { PluginListenerHandle } from '@capacitor/core';

export interface MotionPlugin {
  /**
   * Add a listener for accelerometer data
   *
   * @since 1.0.0
   */
  addListener(
    eventName: 'accel',
    listenerFunc: AccelListener,
  ): Promise<PluginListenerHandle>;

  /**
   * Add a listener for device orientation change (compass heading, etc.)
   *
   * @since 1.0.0
   */
  addListener(
    eventName: 'orientation',
    listenerFunc: OrientationListener,
  ): Promise<PluginListenerHandle>;

  /**
   * Remove all the listeners that are attached to this plugin.
   *
   * @since 1.0.0
   */
  removeAllListeners(): Promise<void>;
}

export type AccelListener = (event: AccelListenerEvent) => void;
export type OrientationListener = (event: OrientationListenerEvent) => void;
export type OrientationListenerEvent = RotationRate;

export interface RotationRate {
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

export interface Acceleration {
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

export interface AccelListenerEvent {
  /**
   * An object giving the acceleration of the device on the three axis X, Y and Z. Acceleration is expressed in m/s
   *
   * @since 1.0.0
   */
  acceleration: Acceleration;

  /**
   * An object giving the acceleration of the device on the three axis X, Y and Z with the effect of gravity. Acceleration is expressed in m/s
   *
   * @since 1.0.0
   */
  accelerationIncludingGravity: Acceleration;

  /**
   * An object giving the rate of change of the device's orientation on the three orientation axis alpha, beta and gamma. Rotation rate is expressed in degrees per seconds.
   *
   * @since 1.0.0
   */
  rotationRate: RotationRate;

  /**
   * A number representing the interval of time, in milliseconds, at which data is obtained from the device.
   *
   * @since 1.0.0
   */
  interval: number;
}

/**
 * @deprecated Use `AccelListener`.
 * @since 1.0.0
 */
export type MotionWatchAccelCallback = AccelListener;

/**
 * @deprecated Use `AccelListenerEvent`.
 * @since 1.0.0
 */
export type MotionEventResult = AccelListenerEvent;

/**
 * @deprecated Use `OrientationListener`.
 * @since 1.0.0
 */
export type MotionWatchOrientationCallback = OrientationListener;

/**
 * @deprecated Use `OrientationListenerEvent`.
 * @since 1.0.0
 */
export type MotionOrientationEventResult = OrientationListenerEvent;
