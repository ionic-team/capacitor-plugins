import {} from '@capacitor/core';

declare module '@capacitor/core' {
  interface PluginRegistry {
    Camera: CameraPlugin;
  }
}

export type CameraPermissionState = PermissionState;

export type CameraPermissionType = 'camera' | 'writePhotos' | 'readPhotos';

export interface CameraPermissionStatus {
  camera: CameraPermissionState;
  writePhotos: CameraPermissionState;
  readPhotos: CameraPermissionState;
}

export interface CameraPluginPermissions {
  types: CameraPermissionType[];
}

export interface CameraPlugin {
  /**
   * Prompt the user to pick a photo from an album, or take a new photo
   * with the camera.
   *
   * @since 1.0.0
   */
  getPhoto(options: CameraOptions): Promise<CameraPhoto>;

  /**
   * Check camera and photo album permissions
   *
   * @since 1.0.0
   */
  checkPermissions(): Promise<CameraPermissionStatus>;

  /**
   * Request camera and photo album permissions
   *
   * @since 1.0.0
   */
  requestPermissions(
    types: CameraPluginPermissions | null,
  ): Promise<CameraPermissionStatus>;
}

export interface CameraOptions {
  /**
   * The quality of image to return as JPEG, from 0-100
   */
  quality?: number;
  /**
   * Whether to allow the user to crop or make small edits (platform specific)
   */
  allowEditing?: boolean;
  /**
   * How the data should be returned. Currently, only 'Base64', 'DataUrl' or 'Uri' is supported
   */
  resultType: CameraResultType;
  /**
   * Whether to save the photo to the gallery.
   * If the photo was picked from the gallery, it will only be saved if edited.
   * Default: false
   */
  saveToGallery?: boolean;
  /**
   * The width of the saved image
   */
  width?: number;
  /**
   * The height of the saved image
   */
  height?: number;
  /**
   * Whether to preserve the aspect ratio of the image.
   * If this flag is true, the width and height will be used as max values
   * and the aspect ratio will be preserved. This is only relevant when
   * both a width and height are passed. When only width or height is provided
   * the aspect ratio is always preserved (and this option is a no-op).
   *
   * A future major version will change this behavior to be default,
   * and may also remove this option altogether.
   * Default: false
   */
  preserveAspectRatio?: boolean;
  /**
   * Whether to automatically rotate the image "up" to correct for orientation
   * in portrait mode
   * Default: true
   */
  correctOrientation?: boolean;
  /**
   * The source to get the photo from. By default this prompts the user to select
   * either the photo album or take a photo.
   * Default: CameraSource.Prompt
   */
  source?: CameraSource;
  /**
   * iOS and Web only: The camera direction.
   * Default: CameraDirection.Rear
   */
  direction?: CameraDirection;

  /**
   * iOS only: The presentation style of the Camera. Defaults to fullscreen.
   */
  presentationStyle?: 'fullscreen' | 'popover';

  /**
   * Web only: Whether to use the PWA Element experience or file input. The
   * default is to use PWA Elements if installed and fall back to file input.
   * To always use file input, set this to `true`.
   *
   * Learn more about PWA Elements: https://capacitorjs.com/docs/pwa-elements
   */
  webUseInput?: boolean;

  /**
   * If use CameraSource.Prompt only, can change Prompt label.
   * default:
   *   promptLabelHeader  : 'Photo'       // iOS only
   *   promptLabelCancel  : 'Cancel'      // iOS only
   *   promptLabelPhoto   : 'From Photos'
   *   promptLabelPicture : 'Take Picture'
   */
  promptLabelHeader?: string;
  promptLabelCancel?: string;
  promptLabelPhoto?: string;
  promptLabelPicture?: string;
}

export interface CameraPhoto {
  /**
   * The base64 encoded string representation of the image, if using CameraResultType.Base64.
   */
  base64String?: string;
  /**
   * The url starting with 'data:image/jpeg;base64,' and the base64 encoded string representation of the image, if using CameraResultType.DataUrl.
   */
  dataUrl?: string;
  /**
   * If using CameraResultType.Uri, the path will contain a full,
   * platform-specific file URL that can be read later using the Filsystem API.
   */
  path?: string;
  /**
   * webPath returns a path that can be used to set the src attribute of an image for efficient
   * loading and rendering.
   */
  webPath?: string;
  /**
   * Exif data, if any, retrieved from the image
   */
  exif?: any;
  /**
   * The format of the image, ex: jpeg, png, gif.
   *
   * iOS and Android only support jpeg.
   * Web supports jpeg and png. gif is only supported if using file input.
   */
  format: string;
}

export type CameraSource = 'prompt' | 'camera' | 'photos';

export type CameraDirection = 'rear' | 'front';

export type CameraResultType = 'uri' | 'base64' | 'dataUrl';
