declare module '@capacitor/core' {
  interface PluginRegistry {
    Haptics: HapticsPlugin;
  }
}

export interface HapticsPlugin {
  /**
   * Trigger a haptics "impact" feedback
   */
  impact(options: HapticsImpactOptions): void;
  /**
   * Trigger a haptics "notification" feedback
   */
  notification(options: HapticsNotificationOptions): void;
  /**
   * Vibrate the device
   */
  vibrate(options?: VibrateOptions): void;
  /**
   * Trigger a selection started haptic hint
   */
  selectionStart(): void;
  /**
   * Trigger a selection changed haptic hint. If a selection was
   * started already, this will cause the device to provide haptic
   * feedback
   */
  selectionChanged(): void;
  /**
   * If selectionStart() was called, selectionEnd() ends the selection.
   * For example, call this when a user has lifted their finger from a control
   */
  selectionEnd(): void;
}

export interface HapticsImpactOptions {
  style: HapticsImpactStyle;
}

export enum HapticsImpactStyle {
  Heavy = 'HEAVY',
  Medium = 'MEDIUM',
  Light = 'LIGHT',
}

export interface HapticsNotificationOptions {
  type: HapticsNotificationType;
}

export enum HapticsNotificationType {
  SUCCESS = 'SUCCESS',
  WARNING = 'WARNING',
  ERROR = 'ERROR',
}

export interface VibrateOptions {
  // Android and Web only
  duration?: number;
}
