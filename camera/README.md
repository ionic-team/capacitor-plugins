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
* [Type Aliases](#type-aliases)

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
requestPermissions(permissions?: CameraPluginPermissions | undefined) => Promise<CameraPermissionStatus>
```

Request camera and photo album permissions

| Param             | Type                                                                        |
| ----------------- | --------------------------------------------------------------------------- |
| **`permissions`** | <code><a href="#camerapluginpermissions">CameraPluginPermissions</a></code> |

**Returns:** <code>Promise&lt;<a href="#camerapermissionstatus">CameraPermissionStatus</a>&gt;</code>

**Since:** 1.0.0

--------------------


### Interfaces


#### CameraPhoto

| Prop               | Type                | Description                                                                                                                                                                   | Since |
| ------------------ | ------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ----- |
| **`base64String`** | <code>string</code> | The base64 encoded string representation of the image, if using <a href="#cameraresulttype">CameraResultType</a>.Base64.                                                      | 1.0.0 |
| **`dataUrl`**      | <code>string</code> | The url starting with 'data:image/jpeg;base64,' and the base64 encoded string representation of the image, if using <a href="#cameraresulttype">CameraResultType</a>.DataUrl. | 1.0.0 |
| **`path`**         | <code>string</code> | If using <a href="#cameraresulttype">CameraResultType</a>.Uri, the path will contain a full, platform-specific file URL that can be read later using the Filsystem API.       | 1.0.0 |
| **`webPath`**      | <code>string</code> | webPath returns a path that can be used to set the src attribute of an image for efficient loading and rendering.                                                             | 1.0.0 |
| **`exif`**         | <code>any</code>    | Exif data, if any, retrieved from the image                                                                                                                                   | 1.0.0 |
| **`format`**       | <code>string</code> | The format of the image, ex: jpeg, png, gif. iOS and Android only support jpeg. Web supports jpeg and png. gif is only supported if using file input.                         | 1.0.0 |


#### CameraOptions

| Prop                      | Type                                                          | Description                                                                                                                                                                                                                                                                                                                                                                                                                                          | Default                             | Since |
| ------------------------- | ------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ----------------------------------- | ----- |
| **`quality`**             | <code>number</code>                                           | The quality of image to return as JPEG, from 0-100                                                                                                                                                                                                                                                                                                                                                                                                   |                                     | 1.0.0 |
| **`allowEditing`**        | <code>boolean</code>                                          | Whether to allow the user to crop or make small edits (platform specific)                                                                                                                                                                                                                                                                                                                                                                            |                                     | 1.0.0 |
| **`resultType`**          | <code><a href="#cameraresulttype">CameraResultType</a></code> | How the data should be returned. Currently, only 'Base64', 'DataUrl' or 'Uri' is supported                                                                                                                                                                                                                                                                                                                                                           |                                     | 1.0.0 |
| **`saveToGallery`**       | <code>boolean</code>                                          | Whether to save the photo to the gallery. If the photo was picked from the gallery, it will only be saved if edited.                                                                                                                                                                                                                                                                                                                                 | <code>: false</code>                | 1.0.0 |
| **`width`**               | <code>number</code>                                           | The width of the saved image                                                                                                                                                                                                                                                                                                                                                                                                                         |                                     | 1.0.0 |
| **`height`**              | <code>number</code>                                           | The height of the saved image                                                                                                                                                                                                                                                                                                                                                                                                                        |                                     | 1.0.0 |
| **`preserveAspectRatio`** | <code>boolean</code>                                          | Whether to preserve the aspect ratio of the image. If this flag is true, the width and height will be used as max values and the aspect ratio will be preserved. This is only relevant when both a width and height are passed. When only width or height is provided the aspect ratio is always preserved (and this option is a no-op). A future major version will change this behavior to be default, and may also remove this option altogether. | <code>: false</code>                | 1.0.0 |
| **`correctOrientation`**  | <code>boolean</code>                                          | Whether to automatically rotate the image "up" to correct for orientation in portrait mode                                                                                                                                                                                                                                                                                                                                                           | <code>: true</code>                 | 1.0.0 |
| **`source`**              | <code><a href="#camerasource">CameraSource</a></code>         | The source to get the photo from. By default this prompts the user to select either the photo album or take a photo.                                                                                                                                                                                                                                                                                                                                 | <code>: CameraSource.prompt</code>  | 1.0.0 |
| **`direction`**           | <code><a href="#cameradirection">CameraDirection</a></code>   | iOS and Web only: The camera direction.                                                                                                                                                                                                                                                                                                                                                                                                              | <code>: CameraDirection.rear</code> | 1.0.0 |
| **`presentationStyle`**   | <code>'fullscreen' \| 'popover'</code>                        | iOS only: The presentation style of the Camera.                                                                                                                                                                                                                                                                                                                                                                                                      | <code>: 'fullscreen'</code>         | 1.0.0 |
| **`webUseInput`**         | <code>boolean</code>                                          | Web only: Whether to use the PWA Element experience or file input. The default is to use PWA Elements if installed and fall back to file input. To always use file input, set this to `true`. Learn more about PWA Elements: https://capacitorjs.com/docs/pwa-elements                                                                                                                                                                               |                                     | 1.0.0 |
| **`promptLabelHeader`**   | <code>string</code>                                           | Text value to use when displaying the prompt. iOS only: The title of the action sheet.                                                                                                                                                                                                                                                                                                                                                               | <code>: 'Photo'</code>              | 1.0.0 |
| **`promptLabelCancel`**   | <code>string</code>                                           | Text value to use when displaying the prompt. iOS only: The label of the 'cancel' button.                                                                                                                                                                                                                                                                                                                                                            | <code>: 'Cancel'</code>             | 1.0.0 |
| **`promptLabelPhoto`**    | <code>string</code>                                           | Text value to use when displaying the prompt. The label of the button to select a saved image.                                                                                                                                                                                                                                                                                                                                                       | <code>: 'From Photos'</code>        | 1.0.0 |
| **`promptLabelPicture`**  | <code>string</code>                                           | Text value to use when displaying the prompt. The label of the button to open the camera.                                                                                                                                                                                                                                                                                                                                                            | <code>: 'Take Picture'</code>       | 1.0.0 |


#### CameraPermissionStatus

| Prop         | Type                                                                    |
| ------------ | ----------------------------------------------------------------------- |
| **`camera`** | <code><a href="#camerapermissionstate">CameraPermissionState</a></code> |
| **`photos`** | <code><a href="#camerapermissionstate">CameraPermissionState</a></code> |


#### CameraPluginPermissions

| Prop              | Type                                |
| ----------------- | ----------------------------------- |
| **`permissions`** | <code>CameraPermissionType[]</code> |


### Type Aliases


#### CameraResultType

<code>'uri' | 'base64' | 'dataUrl'</code>


#### CameraSource

<code>'prompt' | 'camera' | 'photos'</code>


#### CameraDirection

<code>'rear' | 'front'</code>


#### CameraPermissionState

<code><a href="#permissionstate">PermissionState</a> | 'limited'</code>


#### PermissionState

<code>'prompt' | 'prompt-with-rationale' | 'granted' | 'denied'</code>


#### CameraPermissionType

<code>'camera' | 'photos'</code>

</docgen-api>
