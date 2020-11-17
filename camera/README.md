# @capacitor/camera

The Camera API provides the ability to take a photo with the camera or choose an existing one from the photo album.

## Install

```bash
npm install @capacitor/camera
npx cap sync
```

## API

<docgen-index>

* [`getPhoto(...)`](#getphoto)
* [`checkPermissions()`](#checkpermissions)
* [`requestPermissions(...)`](#requestpermissions)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### getPhoto(...)

```typescript
getPhoto(options: CameraOptions) => Promise<CameraPhoto>
```

Prompt the user to pick a photo from an album, or take a new photo
with the camera.

| Param         | Type                                                    |
| ------------- | ------------------------------------------------------- |
| **`options`** | <code><a href="#cameraoptions">CameraOptions</a></code> |

**Returns:** <code>Promise&lt;<a href="#cameraphoto">CameraPhoto</a>&gt;</code>

**Since:** 1.0.0

--------------------


### checkPermissions()

```typescript
checkPermissions() => Promise<CameraPermissionStatus>
```

Check camera and photo album permissions

**Returns:** <code>Promise&lt;<a href="#camerapermissionstatus">CameraPermissionStatus</a>&gt;</code>

**Since:** 1.0.0

--------------------


### requestPermissions(...)

```typescript
requestPermissions(types: CameraPluginPermissions | null) => Promise<CameraPermissionStatus>
```

Request camera and photo album permissions

| Param       | Type                                                                                |
| ----------- | ----------------------------------------------------------------------------------- |
| **`types`** | <code><a href="#camerapluginpermissions">CameraPluginPermissions</a> \| null</code> |

**Returns:** <code>Promise&lt;<a href="#camerapermissionstatus">CameraPermissionStatus</a>&gt;</code>

**Since:** 1.0.0

--------------------


### Interfaces


#### CameraPhoto

| Prop               | Type                | Description                                                                                                                                           |
| ------------------ | ------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------- |
| **`base64String`** | <code>string</code> | The base64 encoded string representation of the image, if using CameraResultType.Base64.                                                              |
| **`dataUrl`**      | <code>string</code> | The url starting with 'data:image/jpeg;base64,' and the base64 encoded string representation of the image, if using CameraResultType.DataUrl.         |
| **`path`**         | <code>string</code> | If using CameraResultType.Uri, the path will contain a full, platform-specific file URL that can be read later using the Filsystem API.               |
| **`webPath`**      | <code>string</code> | webPath returns a path that can be used to set the src attribute of an image for efficient loading and rendering.                                     |
| **`exif`**         | <code>any</code>    | Exif data, if any, retrieved from the image                                                                                                           |
| **`format`**       | <code>string</code> | The format of the image, ex: jpeg, png, gif. iOS and Android only support jpeg. Web supports jpeg and png. gif is only supported if using file input. |


#### CameraOptions

| Prop                      | Type                                          | Description                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| ------------------------- | --------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **`quality`**             | <code>number</code>                           | The quality of image to return as JPEG, from 0-100                                                                                                                                                                                                                                                                                                                                                                                                                  |
| **`allowEditing`**        | <code>boolean</code>                          | Whether to allow the user to crop or make small edits (platform specific)                                                                                                                                                                                                                                                                                                                                                                                           |
| **`resultType`**          | <code>"uri" \| "base64" \| "dataUrl"</code>   | How the data should be returned. Currently, only 'Base64', 'DataUrl' or 'Uri' is supported                                                                                                                                                                                                                                                                                                                                                                          |
| **`saveToGallery`**       | <code>boolean</code>                          | Whether to save the photo to the gallery. If the photo was picked from the gallery, it will only be saved if edited. Default: false                                                                                                                                                                                                                                                                                                                                 |
| **`width`**               | <code>number</code>                           | The width of the saved image                                                                                                                                                                                                                                                                                                                                                                                                                                        |
| **`height`**              | <code>number</code>                           | The height of the saved image                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| **`preserveAspectRatio`** | <code>boolean</code>                          | Whether to preserve the aspect ratio of the image. If this flag is true, the width and height will be used as max values and the aspect ratio will be preserved. This is only relevant when both a width and height are passed. When only width or height is provided the aspect ratio is always preserved (and this option is a no-op). A future major version will change this behavior to be default, and may also remove this option altogether. Default: false |
| **`correctOrientation`**  | <code>boolean</code>                          | Whether to automatically rotate the image "up" to correct for orientation in portrait mode Default: true                                                                                                                                                                                                                                                                                                                                                            |
| **`source`**              | <code>"camera" \| "prompt" \| "photos"</code> | The source to get the photo from. By default this prompts the user to select either the photo album or take a photo. Default: CameraSource.Prompt                                                                                                                                                                                                                                                                                                                   |
| **`direction`**           | <code>"rear" \| "front"</code>                | iOS and Web only: The camera direction. Default: CameraDirection.Rear                                                                                                                                                                                                                                                                                                                                                                                               |
| **`presentationStyle`**   | <code>"fullscreen" \| "popover"</code>        | iOS only: The presentation style of the Camera. Defaults to fullscreen.                                                                                                                                                                                                                                                                                                                                                                                             |
| **`webUseInput`**         | <code>boolean</code>                          | Web only: Whether to use the PWA Element experience or file input. The default is to use PWA Elements if installed and fall back to file input. To always use file input, set this to `true`. Learn more about PWA Elements: https://capacitorjs.com/docs/pwa-elements                                                                                                                                                                                              |
| **`promptLabelHeader`**   | <code>string</code>                           | If use CameraSource.Prompt only, can change Prompt label. default: promptLabelHeader : 'Photo' // iOS only promptLabelCancel : 'Cancel' // iOS only promptLabelPhoto : 'From Photos' promptLabelPicture : 'Take Picture'                                                                                                                                                                                                                                            |
| **`promptLabelCancel`**   | <code>string</code>                           |                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
| **`promptLabelPhoto`**    | <code>string</code>                           |                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
| **`promptLabelPicture`**  | <code>string</code>                           |                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |


#### CameraPermissionStatus

| Prop              | Type             |
| ----------------- | ---------------- |
| **`camera`**      | <code>any</code> |
| **`writePhotos`** | <code>any</code> |
| **`readPhotos`**  | <code>any</code> |


#### CameraPluginPermissions

| Prop        | Type                                |
| ----------- | ----------------------------------- |
| **`types`** | <code>CameraPermissionType[]</code> |

</docgen-api>
