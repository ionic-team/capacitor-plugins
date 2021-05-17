import type { PermissionState } from '@capacitor/core';

export type CameraPermissionState = PermissionState | 'limited';

export type CameraPermissionType = 'camera' | 'photos';

export interface PermissionStatus {
  camera: CameraPermissionState;
  photos: CameraPermissionState;
}

export interface CameraPluginPermissions {
  permissions: CameraPermissionType[];
}

export interface CameraPlugin {
  /**
   * Prompt the user to pick a photo from an album, or take a new photo
   * with the camera.
   *
   * @since 1.0.0
   */
  getPhoto(options: ImageOptions): Promise<Photo>;

  /**
   * Check camera and photo album permissions
   *
   * @since 1.0.0
   */
  checkPermissions(): Promise<PermissionStatus>;

  /**
   * Request camera and photo album permissions
   *
   * @since 1.0.0
   */
  requestPermissions(
    permissions?: CameraPluginPermissions,
  ): Promise<PermissionStatus>;
}

export interface ImageOptions {
  /**
   * The quality of image to return as JPEG, from 0-100
   *
   * @since 1.0.0
   */
  quality?: number;
  /**
   * Whether to allow the user to crop or make small edits (platform specific)
   *
   * @since 1.0.0
   */
  allowEditing?: boolean;
  /**
   * How the data should be returned. Currently, only 'Base64', 'DataUrl' or 'Uri' is supported
   *
   * @since 1.0.0
   */
  resultType: CameraResultType;
  /**
   * Whether to save the photo to the gallery.
   * If the photo was picked from the gallery, it will only be saved if edited.
   * @default: false
   *
   * @since 1.0.0
   */
  saveToGallery?: boolean;
  /**
   * The width of the saved image
   *
   * @since 1.0.0
   */
  width?: number;
  /**
   * The height of the saved image
   *
   * @since 1.0.0
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
   * @default: false
   *
   * @since 1.0.0
   */
  preserveAspectRatio?: boolean;
  /**
   * Whether to automatically rotate the image "up" to correct for orientation
   * in portrait mode
   * @default: true
   *
   * @since 1.0.0
   */
  correctOrientation?: boolean;
  /**
   * The source to get the photo from. By default this prompts the user to select
   * either the photo album or take a photo.
   * @default: CameraSource.Prompt
   *
   * @since 1.0.0
   */
  source?: CameraSource;
  /**
   * iOS and Web only: The camera direction.
   * @default: CameraDirection.Rear
   *
   * @since 1.0.0
   */
  direction?: CameraDirection;

  /**
   * iOS only: The presentation style of the Camera.
   * @default: 'fullscreen'
   *
   * @since 1.0.0
   */
  presentationStyle?: 'fullscreen' | 'popover';

  /**
   * Web only: Whether to use the PWA Element experience or file input. The
   * default is to use PWA Elements if installed and fall back to file input.
   * To always use file input, set this to `true`.
   *
   * Learn more about PWA Elements: https://capacitorjs.com/docs/pwa-elements
   *
   * @since 1.0.0
   */
  webUseInput?: boolean;

  /**
   * Text value to use when displaying the prompt.
   * iOS only: The title of the action sheet.
   * @default: 'Photo'
   *
   * @since 1.0.0
   *
   */
  promptLabelHeader?: string;

  /**
   * Text value to use when displaying the prompt.
   * iOS only: The label of the 'cancel' button.
   * @default: 'Cancel'
   *
   * @since 1.0.0
   */
  promptLabelCancel?: string;

  /**
   * Text value to use when displaying the prompt.
   * The label of the button to select a saved image.
   * @default: 'From Photos'
   *
   * @since 1.0.0
   */
  promptLabelPhoto?: string;

  /**
   * Text value to use when displaying the prompt.
   * The label of the button to open the camera.
   * @default: 'Take Picture'
   *
   * @since 1.0.0
   */
  promptLabelPicture?: string;
}

export interface Photo {
  /**
   * The base64 encoded string representation of the image, if using CameraResultType.Base64.
   *
   * @since 1.0.0
   */
  base64String?: string;
  /**
   * The url starting with 'data:image/jpeg;base64,' and the base64 encoded string representation of the image, if using CameraResultType.DataUrl.
   *
   * @since 1.0.0
   */
  dataUrl?: string;
  /**
   * If using CameraResultType.Uri, the path will contain a full,
   * platform-specific file URL that can be read later using the Filsystem API.
   *
   * @since 1.0.0
   */
  path?: string;
  /**
   * webPath returns a path that can be used to set the src attribute of an image for efficient
   * loading and rendering.
   *
   * @since 1.0.0
   */
  webPath?: string;
  /**
   * Exif data, if any, retrieved from the image
   *
   * @since 1.0.0
   */
  exif?: any;
  /**
   * The format of the image, ex: jpeg, png, gif.
   *
   * iOS and Android only support jpeg.
   * Web supports jpeg and png. gif is only supported if using file input.
   *
   * @since 1.0.0
   */
  format: string;
}

export enum CameraSource {
  Prompt = 'PROMPT',
  Camera = 'CAMERA',
  Photos = 'PHOTOS',
}

export enum CameraDirection {
  Rear = 'REAR',
  Front = 'FRONT',
}

export enum CameraResultType {
  Uri = 'uri',
  Base64 = 'base64',
  DataUrl = 'dataUrl',
}

/**
 * @deprecated Use `Photo`.
 * @since 1.0.0
 */
export type CameraPhoto = Photo;

/**
 * @deprecated Use `ImageOptions`.
 * @since 1.0.0
 */
export type CameraOptions = ImageOptions;
