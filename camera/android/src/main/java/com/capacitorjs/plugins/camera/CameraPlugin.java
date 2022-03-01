package com.capacitorjs.plugins.camera;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Base64;
import androidx.activity.result.ActivityResult;
import androidx.core.content.FileProvider;
import com.getcapacitor.FileUtils;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Logger;
import com.getcapacitor.PermissionState;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.ActivityCallback;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.json.JSONException;

/**
 * The Camera plugin makes it easy to take a photo or have the user select a photo
 * from their albums.
 *
 * On Android, this plugin sends an intent that opens the stock Camera app.
 *
 * Adapted from https://developer.android.com/training/camera/photobasics.html
 */
@CapacitorPlugin(
    name = "Camera",
    permissions = {
        @Permission(strings = { Manifest.permission.CAMERA }, alias = CameraPlugin.CAMERA),
        @Permission(
            strings = { Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE },
            alias = CameraPlugin.PHOTOS
        )
    }
)
public class CameraPlugin extends Plugin {

    // Permission alias constants
    static final String CAMERA = "camera";
    static final String PHOTOS = "photos";

    // Message constants
    private static final String INVALID_RESULT_TYPE_ERROR = "Invalid resultType option";
    private static final String PERMISSION_DENIED_ERROR_CAMERA = "User denied access to camera";
    private static final String PERMISSION_DENIED_ERROR_PHOTOS = "User denied access to photos";
    private static final String NO_CAMERA_ERROR = "Device doesn't have a camera available";
    private static final String NO_CAMERA_ACTIVITY_ERROR = "Unable to resolve camera activity";
    private static final String NO_PHOTO_ACTIVITY_ERROR = "Unable to resolve photo activity";
    private static final String IMAGE_FILE_SAVE_ERROR = "Unable to create photo on disk";
    private static final String IMAGE_PROCESS_NO_FILE_ERROR = "Unable to process image, file not found on disk";
    private static final String UNABLE_TO_PROCESS_IMAGE = "Unable to process image";
    private static final String IMAGE_EDIT_ERROR = "Unable to edit image";
    private static final String IMAGE_GALLERY_SAVE_ERROR = "Unable to save the image in the gallery";

    private String imageFileSavePath;
    private String imageEditedFileSavePath;
    private Uri imageFileUri;
    private Uri imagePickedContentUri;
    private boolean isEdited = false;
    private boolean isFirstRequest = true;
    private boolean isSaved = false;

    private CameraSettings settings = new CameraSettings();

    @PluginMethod
    public void getPhoto(PluginCall call) {
        isEdited = false;
        settings = getSettings(call);
        doShow(call);
    }

    @PluginMethod
    public void pickImages(PluginCall call) {
        settings = getSettings(call);
        openPhotos(call, true, false);
    }

    private void doShow(PluginCall call) {
        switch (settings.getSource()) {
            case CAMERA:
                showCamera(call);
                break;
            case PHOTOS:
                showPhotos(call);
                break;
            default:
                showPrompt(call);
                break;
        }
    }

    private void showPrompt(final PluginCall call) {
        // We have all necessary permissions, open the camera
        List<String> options = new ArrayList<>();
        options.add(call.getString("promptLabelPhoto", "From Photos"));
        options.add(call.getString("promptLabelPicture", "Take Picture"));

        final CameraBottomSheetDialogFragment fragment = new CameraBottomSheetDialogFragment();
        fragment.setTitle(call.getString("promptLabelHeader", "Photo"));
        fragment.setOptions(
            options,
            index -> {
                if (index == 0) {
                    settings.setSource(CameraSource.PHOTOS);
                    openPhotos(call);
                } else if (index == 1) {
                    settings.setSource(CameraSource.CAMERA);
                    openCamera(call);
                }
            },
            () -> call.reject("User cancelled photos app")
        );
        fragment.show(getActivity().getSupportFragmentManager(), "capacitorModalsActionSheet");
    }

    private void showCamera(final PluginCall call) {
        if (!getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            call.reject(NO_CAMERA_ERROR);
            return;
        }
        openCamera(call);
    }

    private void showPhotos(final PluginCall call) {
        openPhotos(call);
    }

    private boolean checkCameraPermissions(PluginCall call) {
        // if the manifest does not contain the camera permissions key, we don't need to ask the user
        boolean needCameraPerms = isPermissionDeclared(CAMERA);
        boolean hasCameraPerms = !needCameraPerms || getPermissionState(CAMERA) == PermissionState.GRANTED;
        boolean hasPhotoPerms = getPermissionState(PHOTOS) == PermissionState.GRANTED;

        // If we want to save to the gallery, we need two permissions
        if (settings.isSaveToGallery() && !(hasCameraPerms && hasPhotoPerms) && isFirstRequest) {
            isFirstRequest = false;
            String[] aliases;
            if (needCameraPerms) {
                aliases = new String[] { CAMERA, PHOTOS };
            } else {
                aliases = new String[] { PHOTOS };
            }
            requestPermissionForAliases(aliases, call, "cameraPermissionsCallback");
            return false;
        }
        // If we don't need to save to the gallery, we can just ask for camera permissions
        else if (!hasCameraPerms) {
            requestPermissionForAlias(CAMERA, call, "cameraPermissionsCallback");
            return false;
        }
        return true;
    }

    private boolean checkPhotosPermissions(PluginCall call) {
        if (getPermissionState(PHOTOS) != PermissionState.GRANTED) {
            requestPermissionForAlias(PHOTOS, call, "cameraPermissionsCallback");
            return false;
        }
        return true;
    }

    /**
     * Completes the plugin call after a camera permission request
     *
     * @see #getPhoto(PluginCall)
     * @param call the plugin call
     */
    @PermissionCallback
    private void cameraPermissionsCallback(PluginCall call) {
        if (call.getMethodName().equals("pickImages")) {
            openPhotos(call, true, true);
        } else {
            if (settings.getSource() == CameraSource.CAMERA && getPermissionState(CAMERA) != PermissionState.GRANTED) {
                Logger.debug(getLogTag(), "User denied camera permission: " + getPermissionState(CAMERA).toString());
                call.reject(PERMISSION_DENIED_ERROR_CAMERA);
                return;
            } else if (settings.getSource() == CameraSource.PHOTOS && getPermissionState(PHOTOS) != PermissionState.GRANTED) {
                Logger.debug(getLogTag(), "User denied photos permission: " + getPermissionState(PHOTOS).toString());
                call.reject(PERMISSION_DENIED_ERROR_PHOTOS);
                return;
            }
            doShow(call);
        }
    }

    private CameraSettings getSettings(PluginCall call) {
        CameraSettings settings = new CameraSettings();
        settings.setResultType(getResultType(call.getString("resultType")));
        settings.setSaveToGallery(call.getBoolean("saveToGallery", CameraSettings.DEFAULT_SAVE_IMAGE_TO_GALLERY));
        settings.setAllowEditing(call.getBoolean("allowEditing", false));
        settings.setQuality(call.getInt("quality", CameraSettings.DEFAULT_QUALITY));
        settings.setWidth(call.getInt("width", 0));
        settings.setHeight(call.getInt("height", 0));
        settings.setShouldResize(settings.getWidth() > 0 || settings.getHeight() > 0);
        settings.setShouldCorrectOrientation(call.getBoolean("correctOrientation", CameraSettings.DEFAULT_CORRECT_ORIENTATION));
        try {
            settings.setSource(CameraSource.valueOf(call.getString("source", CameraSource.PROMPT.getSource())));
        } catch (IllegalArgumentException ex) {
            settings.setSource(CameraSource.PROMPT);
        }
        return settings;
    }

    private CameraResultType getResultType(String resultType) {
        if (resultType == null) {
            return null;
        }
        try {
            return CameraResultType.valueOf(resultType.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            Logger.debug(getLogTag(), "Invalid result type \"" + resultType + "\", defaulting to base64");
            return CameraResultType.BASE64;
        }
    }

    public void openCamera(final PluginCall call) {
        if (checkCameraPermissions(call)) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
                // If we will be saving the photo, send the target file along
                try {
                    String appId = getAppId();
                    File photoFile = CameraUtils.createImageFile(getActivity());
                    imageFileSavePath = photoFile.getAbsolutePath();
                    // TODO: Verify provider config exists
                    imageFileUri = FileProvider.getUriForFile(getActivity(), appId + ".fileprovider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);
                } catch (Exception ex) {
                    call.reject(IMAGE_FILE_SAVE_ERROR, ex);
                    return;
                }

                startActivityForResult(call, takePictureIntent, "processCameraImage");
            } else {
                call.reject(NO_CAMERA_ACTIVITY_ERROR);
            }
        }
    }

    public void openPhotos(final PluginCall call) {
        openPhotos(call, false, false);
    }

    private void openPhotos(final PluginCall call, boolean multiple, boolean skipPermission) {
        if (skipPermission || checkPhotosPermissions(call)) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, multiple);
            intent.setType("image/*");
            try {
                if (multiple) {
                    intent.putExtra("multi-pick", multiple);
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[] { "image/*" });
                    startActivityForResult(call, intent, "processPickedImages");
                } else {
                    startActivityForResult(call, intent, "processPickedImage");
                }
            } catch (ActivityNotFoundException ex) {
                call.reject(NO_PHOTO_ACTIVITY_ERROR);
            }
        }
    }

    @ActivityCallback
    public void processCameraImage(PluginCall call, ActivityResult result) {
        settings = getSettings(call);
        if (imageFileSavePath == null) {
            call.reject(IMAGE_PROCESS_NO_FILE_ERROR);
            return;
        }
        // Load the image as a Bitmap
        File f = new File(imageFileSavePath);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Uri contentUri = Uri.fromFile(f);
        Bitmap bitmap = BitmapFactory.decodeFile(imageFileSavePath, bmOptions);

        if (bitmap == null) {
            call.reject("User cancelled photos app");
            return;
        }

        returnResult(call, bitmap, contentUri);
    }

    @ActivityCallback
    public void processPickedImage(PluginCall call, ActivityResult result) {
        settings = getSettings(call);
        Intent data = result.getData();
        if (data == null) {
            call.reject("No image picked");
            return;
        }

        Uri u = data.getData();

        imagePickedContentUri = u;

        processPickedImage(u, call);
    }

    @ActivityCallback
    public void processPickedImages(PluginCall call, ActivityResult result) {
        Intent data = result.getData();
        if (data != null) {
            Executor executor = Executors.newSingleThreadExecutor();
            executor.execute(
                () -> {
                    JSObject ret = new JSObject();
                    JSArray photos = new JSArray();
                    if (data.getClipData() != null) {
                        int count = data.getClipData().getItemCount();
                        for (int i = 0; i < count; i++) {
                            Uri imageUri = data.getClipData().getItemAt(i).getUri();
                            JSObject processResult = processPickedImages(imageUri);
                            if (processResult.getString("error") != null && !processResult.getString("error").isEmpty()) {
                                call.reject(processResult.getString("error"));
                                return;
                            } else {
                                photos.put(processResult);
                            }
                        }
                    } else if (data.getData() != null) {
                        Uri imageUri = data.getData();
                        JSObject processResult = processPickedImages(imageUri);
                        if (processResult.getString("error") != null && !processResult.getString("error").isEmpty()) {
                            call.reject(processResult.getString("error"));
                            return;
                        } else {
                            photos.put(processResult);
                        }
                    } else if (data.getExtras() != null) {
                        Bundle bundle = data.getExtras();
                        if (bundle.keySet().contains("selectedItems")) {
                            ArrayList<Parcelable> fileUris = bundle.getParcelableArrayList("selectedItems");
                            if (fileUris != null) {
                                for (Parcelable fileUri : fileUris) {
                                    if (fileUri instanceof Uri) {
                                        Uri imageUri = (Uri) fileUri;
                                        try {
                                            JSObject processResult = processPickedImages(imageUri);
                                            if (processResult.getString("error") != null && !processResult.getString("error").isEmpty()) {
                                                call.reject(processResult.getString("error"));
                                                return;
                                            } else {
                                                photos.put(processResult);
                                            }
                                        } catch (SecurityException ex) {
                                            call.reject("SecurityException");
                                        }
                                    }
                                }
                            }
                        }
                    }
                    ret.put("photos", photos);
                    call.resolve(ret);
                }
            );
        } else {
            call.reject("No images picked");
        }
    }

    private void processPickedImage(Uri imageUri, PluginCall call) {
        InputStream imageStream = null;

        try {
            imageStream = getContext().getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(imageStream);

            if (bitmap == null) {
                call.reject("Unable to process bitmap");
                return;
            }

            returnResult(call, bitmap, imageUri);
        } catch (OutOfMemoryError err) {
            call.reject("Out of memory");
        } catch (FileNotFoundException ex) {
            call.reject("No such image found", ex);
        } finally {
            if (imageStream != null) {
                try {
                    imageStream.close();
                } catch (IOException e) {
                    Logger.error(getLogTag(), UNABLE_TO_PROCESS_IMAGE, e);
                }
            }
        }
    }

    private JSObject processPickedImages(Uri imageUri) {
        InputStream imageStream = null;
        JSObject ret = new JSObject();
        try {
            imageStream = getContext().getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(imageStream);

            if (bitmap == null) {
                ret.put("error", "Unable to process bitmap");
                return ret;
            }

            ExifWrapper exif = ImageUtils.getExifData(getContext(), bitmap, imageUri);
            try {
                bitmap = prepareBitmap(bitmap, imageUri, exif);
            } catch (IOException e) {
                ret.put("error", UNABLE_TO_PROCESS_IMAGE);
                return ret;
            }
            // Compress the final image and prepare for output to client
            ByteArrayOutputStream bitmapOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, settings.getQuality(), bitmapOutputStream);

            Uri newUri = getTempImage(imageUri, bitmapOutputStream);
            exif.copyExif(newUri.getPath());
            if (newUri != null) {
                ret.put("format", "jpeg");
                ret.put("exif", exif.toJson());
                ret.put("path", newUri.toString());
                ret.put("webPath", FileUtils.getPortablePath(getContext(), bridge.getLocalUrl(), newUri));
            } else {
                ret.put("error", UNABLE_TO_PROCESS_IMAGE);
            }
            return ret;
        } catch (OutOfMemoryError err) {
            ret.put("error", "Out of memory");
        } catch (FileNotFoundException ex) {
            ret.put("error", "No such image found");
            Logger.error(getLogTag(), "No such image found", ex);
        } finally {
            if (imageStream != null) {
                try {
                    imageStream.close();
                } catch (IOException e) {
                    Logger.error(getLogTag(), UNABLE_TO_PROCESS_IMAGE, e);
                }
            }
        }
        return ret;
    }

    @ActivityCallback
    private void processEditedImage(PluginCall call, ActivityResult result) {
        isEdited = true;
        settings = getSettings(call);
        if (result.getResultCode() == Activity.RESULT_CANCELED) {
            // User cancelled the edit operation, if this file was picked from photos,
            // process the original picked image, otherwise process it as a camera photo
            if (imagePickedContentUri != null) {
                processPickedImage(imagePickedContentUri, call);
            } else {
                processCameraImage(call, result);
            }
        } else {
            processPickedImage(call, result);
        }
    }

    /**
     * Save the modified image on the same path,
     * or on a temporary location if it's a content url
     * @param uri
     * @param is
     * @return
     * @throws IOException
     */
    private Uri saveImage(Uri uri, InputStream is) throws IOException {
        File outFile = null;
        if (uri.getScheme().equals("content")) {
            outFile = getTempFile(uri);
        } else {
            outFile = new File(uri.getPath());
        }
        try {
            writePhoto(outFile, is);
        } catch (FileNotFoundException ex) {
            // Some gallery apps return read only file url, create a temporary file for modifications
            outFile = getTempFile(uri);
            writePhoto(outFile, is);
        }
        return Uri.fromFile(outFile);
    }

    private void writePhoto(File outFile, InputStream is) throws IOException {
        FileOutputStream fos = new FileOutputStream(outFile);
        byte[] buffer = new byte[1024];
        int len;
        while ((len = is.read(buffer)) != -1) {
            fos.write(buffer, 0, len);
        }
        fos.close();
    }

    private File getTempFile(Uri uri) {
        String filename = Uri.parse(Uri.decode(uri.toString())).getLastPathSegment();
        if (!filename.contains(".jpg") && !filename.contains(".jpeg")) {
            filename += "." + (new java.util.Date()).getTime() + ".jpeg";
        }
        File cacheDir = getContext().getCacheDir();
        return new File(cacheDir, filename);
    }

    /**
     * After processing the image, return the final result back to the caller.
     * @param call
     * @param bitmap
     * @param u
     */
    private void returnResult(PluginCall call, Bitmap bitmap, Uri u) {
        ExifWrapper exif = ImageUtils.getExifData(getContext(), bitmap, u);
        try {
            bitmap = prepareBitmap(bitmap, u, exif);
        } catch (IOException e) {
            call.reject(UNABLE_TO_PROCESS_IMAGE);
            return;
        }
        // Compress the final image and prepare for output to client
        ByteArrayOutputStream bitmapOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, settings.getQuality(), bitmapOutputStream);

        if (settings.isAllowEditing() && !isEdited) {
            editImage(call, u, bitmapOutputStream);
            return;
        }

        boolean saveToGallery = call.getBoolean("saveToGallery", CameraSettings.DEFAULT_SAVE_IMAGE_TO_GALLERY);
        if (saveToGallery && (imageEditedFileSavePath != null || imageFileSavePath != null)) {
            isSaved = true;
            try {
                String fileToSavePath = imageEditedFileSavePath != null ? imageEditedFileSavePath : imageFileSavePath;
                File fileToSave = new File(fileToSavePath);
                String inserted = MediaStore.Images.Media.insertImage(
                    getContext().getContentResolver(),
                    fileToSavePath,
                    fileToSave.getName(),
                    ""
                );
                if (inserted == null) {
                    isSaved = false;
                }
            } catch (FileNotFoundException e) {
                isSaved = false;
                Logger.error(getLogTag(), IMAGE_GALLERY_SAVE_ERROR, e);
            }
        }

        if (settings.getResultType() == CameraResultType.BASE64) {
            returnBase64(call, exif, bitmapOutputStream);
        } else if (settings.getResultType() == CameraResultType.URI) {
            returnFileURI(call, exif, bitmap, u, bitmapOutputStream);
        } else if (settings.getResultType() == CameraResultType.DATAURL) {
            returnDataUrl(call, exif, bitmapOutputStream);
        } else {
            call.reject(INVALID_RESULT_TYPE_ERROR);
        }
        // Result returned, clear stored paths and images
        if (settings.getResultType() != CameraResultType.URI) {
            deleteImageFile();
        }
        imageFileSavePath = null;
        imageFileUri = null;
        imagePickedContentUri = null;
        imageEditedFileSavePath = null;
    }

    private void deleteImageFile() {
        if (imageFileSavePath != null && !settings.isSaveToGallery()) {
            File photoFile = new File(imageFileSavePath);
            if (photoFile.exists()) {
                photoFile.delete();
            }
        }
    }

    private void returnFileURI(PluginCall call, ExifWrapper exif, Bitmap bitmap, Uri u, ByteArrayOutputStream bitmapOutputStream) {
        Uri newUri = getTempImage(u, bitmapOutputStream);
        exif.copyExif(newUri.getPath());
        if (newUri != null) {
            JSObject ret = new JSObject();
            ret.put("format", "jpeg");
            ret.put("exif", exif.toJson());
            ret.put("path", newUri.toString());
            ret.put("webPath", FileUtils.getPortablePath(getContext(), bridge.getLocalUrl(), newUri));
            ret.put("saved", isSaved);
            call.resolve(ret);
        } else {
            call.reject(UNABLE_TO_PROCESS_IMAGE);
        }
    }

    private Uri getTempImage(Uri u, ByteArrayOutputStream bitmapOutputStream) {
        ByteArrayInputStream bis = null;
        Uri newUri = null;
        try {
            bis = new ByteArrayInputStream(bitmapOutputStream.toByteArray());
            newUri = saveImage(u, bis);
        } catch (IOException ex) {} finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    Logger.error(getLogTag(), UNABLE_TO_PROCESS_IMAGE, e);
                }
            }
        }
        return newUri;
    }

    /**
     * Apply our standard processing of the bitmap, returning a new one and
     * recycling the old one in the process
     * @param bitmap
     * @param imageUri
     * @param exif
     * @return
     */
    private Bitmap prepareBitmap(Bitmap bitmap, Uri imageUri, ExifWrapper exif) throws IOException {
        if (settings.isShouldCorrectOrientation()) {
            final Bitmap newBitmap = ImageUtils.correctOrientation(getContext(), bitmap, imageUri, exif);
            bitmap = replaceBitmap(bitmap, newBitmap);
        }

        if (settings.isShouldResize()) {
            final Bitmap newBitmap = ImageUtils.resize(bitmap, settings.getWidth(), settings.getHeight());
            bitmap = replaceBitmap(bitmap, newBitmap);
        }

        return bitmap;
    }

    private Bitmap replaceBitmap(Bitmap bitmap, final Bitmap newBitmap) {
        if (bitmap != newBitmap) {
            bitmap.recycle();
        }
        bitmap = newBitmap;
        return bitmap;
    }

    private void returnDataUrl(PluginCall call, ExifWrapper exif, ByteArrayOutputStream bitmapOutputStream) {
        byte[] byteArray = bitmapOutputStream.toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.NO_WRAP);

        JSObject data = new JSObject();
        data.put("format", "jpeg");
        data.put("dataUrl", "data:image/jpeg;base64," + encoded);
        data.put("exif", exif.toJson());
        call.resolve(data);
    }

    private void returnBase64(PluginCall call, ExifWrapper exif, ByteArrayOutputStream bitmapOutputStream) {
        byte[] byteArray = bitmapOutputStream.toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.NO_WRAP);

        JSObject data = new JSObject();
        data.put("format", "jpeg");
        data.put("base64String", encoded);
        data.put("exif", exif.toJson());
        call.resolve(data);
    }

    @Override
    @PluginMethod
    public void requestPermissions(PluginCall call) {
        // If the camera permission is defined in the manifest, then we have to prompt the user
        // or else we will get a security exception when trying to present the camera. If, however,
        // it is not defined in the manifest then we don't need to prompt and it will just work.
        if (isPermissionDeclared(CAMERA)) {
            // just request normally
            super.requestPermissions(call);
        } else {
            // the manifest does not define camera permissions, so we need to decide what to do
            // first, extract the permissions being requested
            JSArray providedPerms = call.getArray("permissions");
            List<String> permsList = null;
            try {
                permsList = providedPerms.toList();
            } catch (JSONException e) {}

            if (permsList != null && permsList.size() == 1 && permsList.contains(CAMERA)) {
                // the only thing being asked for was the camera so we can just return the current state
                checkPermissions(call);
            } else {
                // we need to ask about photos so request storage permissions
                requestPermissionForAlias(PHOTOS, call, "checkPermissions");
            }
        }
    }

    @Override
    public Map<String, PermissionState> getPermissionStates() {
        Map<String, PermissionState> permissionStates = super.getPermissionStates();

        // If Camera is not in the manifest and therefore not required, say the permission is granted
        if (!isPermissionDeclared(CAMERA)) {
            permissionStates.put(CAMERA, PermissionState.GRANTED);
        }

        return permissionStates;
    }

    private void editImage(PluginCall call, Uri uri, ByteArrayOutputStream bitmapOutputStream) {
        try {
            Uri tempImage = getTempImage(uri, bitmapOutputStream);
            Intent editIntent = createEditIntent(tempImage);
            if (editIntent != null) {
                startActivityForResult(call, editIntent, "processEditedImage");
            } else {
                call.reject(IMAGE_EDIT_ERROR);
            }
        } catch (Exception ex) {
            call.reject(IMAGE_EDIT_ERROR, ex);
        }
    }

    private Intent createEditIntent(Uri origPhotoUri) {
        try {
            File editFile = new File(origPhotoUri.getPath());
            Uri editUri = FileProvider.getUriForFile(getActivity(), getContext().getPackageName() + ".fileprovider", editFile);
            Intent editIntent = new Intent(Intent.ACTION_EDIT);
            editIntent.setDataAndType(editUri, "image/*");
            imageEditedFileSavePath = editFile.getAbsolutePath();
            int flags = Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
            editIntent.addFlags(flags);
            editIntent.putExtra(MediaStore.EXTRA_OUTPUT, editUri);
            List<ResolveInfo> resInfoList = getContext()
                .getPackageManager()
                .queryIntentActivities(editIntent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                getContext().grantUriPermission(packageName, editUri, flags);
            }
            return editIntent;
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    protected Bundle saveInstanceState() {
        Bundle bundle = super.saveInstanceState();
        if (bundle != null) {
            bundle.putString("cameraImageFileSavePath", imageFileSavePath);
        }
        return bundle;
    }

    @Override
    protected void restoreState(Bundle state) {
        String storedImageFileSavePath = state.getString("cameraImageFileSavePath");
        if (storedImageFileSavePath != null) {
            imageFileSavePath = storedImageFileSavePath;
        }
    }
}
