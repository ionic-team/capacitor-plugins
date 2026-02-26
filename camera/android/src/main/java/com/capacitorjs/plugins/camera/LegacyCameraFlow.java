package com.capacitorjs.plugins.camera;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Base64;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistryOwner;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.FileProvider;
import com.getcapacitor.FileUtils;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Logger;
import com.getcapacitor.PermissionState;
import com.getcapacitor.PluginCall;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class LegacyCameraFlow {

    private final CameraPlugin plugin;

    // Message constants
    private static final String INVALID_RESULT_TYPE_ERROR = "Invalid resultType option";
    private static final String PERMISSION_DENIED_ERROR_CAMERA = "User denied access to camera";
    private static final String NO_CAMERA_ERROR = "Device doesn't have a camera available";
    private static final String NO_CAMERA_ACTIVITY_ERROR = "Unable to resolve camera activity";
    private static final String NO_PHOTO_ACTIVITY_ERROR = "Unable to resolve photo activity";
    private static final String IMAGE_FILE_SAVE_ERROR = "Unable to create photo on disk";
    private static final String IMAGE_PROCESS_NO_FILE_ERROR = "Unable to process image, file not found on disk";
    private static final String UNABLE_TO_PROCESS_IMAGE = "Unable to process image";
    private static final String IMAGE_EDIT_ERROR = "Unable to edit image";
    private static final String IMAGE_GALLERY_SAVE_ERROR = "Unable to save the image in the gallery";
    private static final String USER_CANCELLED = "User cancelled photos app";

    private String imageFileSavePath;
    private String imageEditedFileSavePath;
    private Uri imageFileUri;
    private Uri imagePickedContentUri;
    private boolean isEdited = false;
    private boolean isFirstRequest = true;
    private boolean isSaved = false;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMultipleMedia = null;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia = null;

    private final AtomicInteger mNextLocalRequestCode = new AtomicInteger();

    private CameraSettings settings = new CameraSettings();

    public LegacyCameraFlow(CameraPlugin plugin) {
        this.plugin = plugin;
    }

    public void getPhoto(PluginCall call) {
        isEdited = false;
        settings = plugin.getSettings(call);
        doShow(call);
    }

    public void pickImages(PluginCall call) {
        settings = plugin.getSettings(call);
        openPhotos(call, true);
    }

    public void pickLimitedLibraryPhotos(PluginCall call) {
        call.unimplemented("not supported on android");
    }

    public void getLimitedLibraryPhotos(PluginCall call) {
        call.unimplemented("not supported on android");
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
                (index) -> {
                    if (index == 0) {
                        settings.setSource(CameraSource.PHOTOS);
                        openPhotos(call);
                    } else if (index == 1) {
                        settings.setSource(CameraSource.CAMERA);
                        openCamera(call);
                    }
                },
                () -> call.reject(USER_CANCELLED)
        );
        fragment.show(plugin.getActivity().getSupportFragmentManager(), "capacitorModalsActionSheet");
    }

    private void showCamera(final PluginCall call) {
        if (!plugin.getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            call.reject(NO_CAMERA_ERROR);
            return;
        }
        openCamera(call);
    }

    private void showPhotos(final PluginCall call) {
        openPhotos(call);
    }

    public boolean checkCameraPermissions(PluginCall call) {
        // if the manifest does not contain the camera permissions key, we don't need to ask the user
        boolean needCameraPerms = plugin.isPermissionDeclared(CameraPlugin.CAMERA);
        boolean hasCameraPerms = !needCameraPerms || plugin.getPermissionState(CameraPlugin.CAMERA) == PermissionState.GRANTED;
        boolean hasGalleryPerms = plugin.getPermissionState(CameraPlugin.SAVE_GALLERY) == PermissionState.GRANTED;

        // If we want to save to the gallery, we need two permissions
        // actually we only need permissions to save to gallery for Android <= 9 (API 28)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // we might still need to request permission for the camera
            if (!hasCameraPerms) {
                plugin.requestLegacyPermissionForAlias(CameraPlugin.CAMERA, call, "cameraPermissionsCallback");
                return false;
            }
            return true;
        }

        // we need to request permissions to save to gallery for Android <= 9
        if (settings.getSaveToGallery() && !(hasCameraPerms && hasGalleryPerms) && isFirstRequest) {
            isFirstRequest = false;
            String[] aliases;
            if (needCameraPerms) {
                aliases = new String[] { CameraPlugin.CAMERA, CameraPlugin.SAVE_GALLERY };
            } else {
                aliases = new String[] { CameraPlugin.SAVE_GALLERY };
            }
            plugin.requestLegacyPermissionForAliases(aliases, call, "cameraPermissionsCallback");
            return false;
        }
        // If we don't need to save to the gallery, we can just ask for camera permissions
        else if (!hasCameraPerms) {
            plugin.requestLegacyPermissionForAlias(CameraPlugin.CAMERA, call, "cameraPermissionsCallback");
            return false;
        }
        return true;
    }

    public void handleCameraPermissionsCallback(PluginCall call) {
        if (call.getMethodName().equals("pickImages")) {
            openPhotos(call, true);
        } else {
            if (settings.getSource() == CameraSource.CAMERA && plugin.getPermissionState(CameraPlugin.CAMERA) != PermissionState.GRANTED) {
                Logger.debug(plugin.getLegacyLogTag(), "User denied camera permission: " + plugin.getPermissionState(CameraPlugin.CAMERA));
                call.reject(PERMISSION_DENIED_ERROR_CAMERA);
                return;
            }
            doShow(call);
        }
    }

    public void openCamera(final PluginCall call) {
        if (checkCameraPermissions(call)) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(plugin.getContext().getPackageManager()) != null) {
                // If we will be saving the photo, send the target file along
                try {
                    String appId = plugin.getAppId();
                    File photoFile = CameraUtils.createImageFile(plugin.getActivity());
                    imageFileSavePath = photoFile.getAbsolutePath();
                    // TODO: Verify provider config exists
                    imageFileUri = FileProvider.getUriForFile(plugin.getActivity(), appId + ".fileprovider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);
                } catch (Exception ex) {
                    call.reject(IMAGE_FILE_SAVE_ERROR, ex);
                    return;
                }

                plugin.startActivityForResult(call, takePictureIntent, "processCameraImage");
            } else {
                call.reject(NO_CAMERA_ACTIVITY_ERROR);
            }
        }
    }

    public void openPhotos(final PluginCall call) {
        openPhotos(call, false);
    }

    private <I, O> ActivityResultLauncher<I> registerActivityResultLauncher(
            ActivityResultContract<I, O> contract,
            ActivityResultCallback<O> callback
    ) {
        String key = "cap_activity_rq#" + mNextLocalRequestCode.getAndIncrement();
        if (plugin.getBridge().getFragment() != null) {
            Object host = plugin.getBridge().getFragment().getHost();
            if (host instanceof ActivityResultRegistryOwner) {
                return ((ActivityResultRegistryOwner) host).getActivityResultRegistry().register(key, contract, callback);
            }
            return plugin.getBridge().getFragment().requireActivity().getActivityResultRegistry().register(key, contract, callback);
        }
        return plugin.getBridge().getActivity().getActivityResultRegistry().register(key, contract, callback);
    }

    private ActivityResultContract<PickVisualMediaRequest, List<Uri>> getContractForCall(final PluginCall call) {
        int limit = call.getInt("limit", 0);
        if (limit > 1) {
            return new ActivityResultContracts.PickMultipleVisualMedia(limit);
        } else {
            return new ActivityResultContracts.PickMultipleVisualMedia();
        }
    }

    private void openPhotos(final PluginCall call, boolean multiple) {
        try {
            if (multiple) {
                pickMultipleMedia = registerActivityResultLauncher(getContractForCall(call), (uris) -> {
                    if (!uris.isEmpty()) {
                        Executor executor = Executors.newSingleThreadExecutor();
                        executor.execute(() -> {
                            JSObject ret = new JSObject();
                            JSArray photos = new JSArray();
                            for (Uri imageUri : uris) {
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
                            ret.put("photos", photos);
                            call.resolve(ret);
                        });
                    } else {
                        call.reject(USER_CANCELLED);
                    }
                    pickMultipleMedia.unregister();
                });
                pickMultipleMedia.launch(
                        new PickVisualMediaRequest.Builder().setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE).build()
                );
            } else {
                pickMedia = registerActivityResultLauncher(new ActivityResultContracts.PickVisualMedia(), (uri) -> {
                    if (uri != null) {
                        imagePickedContentUri = uri;
                        processPickedImage(uri, call);
                    } else {
                        call.reject(USER_CANCELLED);
                    }
                    pickMedia.unregister();
                });
                pickMedia.launch(
                        new PickVisualMediaRequest.Builder().setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE).build()
                );
            }
        } catch (ActivityNotFoundException ex) {
            call.reject(NO_PHOTO_ACTIVITY_ERROR);
        }
    }

    public void processCameraImage(PluginCall call, ActivityResult result) {
        settings = plugin.getSettings(call);
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
            call.reject(USER_CANCELLED);
            return;
        }

        returnResult(call, bitmap, contentUri);
    }

    public void processPickedImage(PluginCall call, ActivityResult result) {
        settings = plugin.getSettings(call);
        Intent data = result.getData();
        if (data == null) {
            call.reject(USER_CANCELLED);
            return;
        }

        Uri u = data.getData();

        imagePickedContentUri = u;

        processPickedImage(u, call);
    }

    @SuppressWarnings("deprecation")
    private ArrayList<Parcelable> getLegacyParcelableArrayList(Bundle bundle, String key) {
        return bundle.getParcelableArrayList(key);
    }

    private void processPickedImage(Uri imageUri, PluginCall call) {
        InputStream imageStream = null;

        try {
            imageStream = plugin.getContext().getContentResolver().openInputStream(imageUri);
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
                    Logger.error(plugin.getLegacyLogTag(), UNABLE_TO_PROCESS_IMAGE, e);
                }
            }
        }
    }

    private JSObject processPickedImages(Uri imageUri) {
        InputStream imageStream = null;
        JSObject ret = new JSObject();
        try {
            imageStream = plugin.getContext().getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(imageStream);

            if (bitmap == null) {
                ret.put("error", "Unable to process bitmap");
                return ret;
            }

            ExifWrapper exif = ImageUtils.getExifData(plugin.getContext(), bitmap, imageUri);
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
                ret.put("webPath", FileUtils.getPortablePath(plugin.getContext(), plugin.getBridge().getLocalUrl(), newUri));
            } else {
                ret.put("error", UNABLE_TO_PROCESS_IMAGE);
            }
            return ret;
        } catch (OutOfMemoryError err) {
            ret.put("error", "Out of memory");
        } catch (FileNotFoundException ex) {
            ret.put("error", "No such image found");
            Logger.error(plugin.getLegacyLogTag(), "No such image found", ex);
        } finally {
            if (imageStream != null) {
                try {
                    imageStream.close();
                } catch (IOException e) {
                    Logger.error(plugin.getLegacyLogTag(), UNABLE_TO_PROCESS_IMAGE, e);
                }
            }
        }
        return ret;
    }

    public void processEditedImage(PluginCall call, ActivityResult result) {
        isEdited = true;
        settings = plugin.getSettings(call);
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
        File cacheDir = plugin.getContext().getCacheDir();
        return new File(cacheDir, filename);
    }

    /**
     * After processing the image, return the final result back to the caller.
     * @param call
     * @param bitmap
     * @param u
     */
    @SuppressWarnings("deprecation")
    private void returnResult(PluginCall call, Bitmap bitmap, Uri u) {
        ExifWrapper exif = ImageUtils.getExifData(plugin.getContext(), bitmap, u);
        try {
            bitmap = prepareBitmap(bitmap, u, exif);
        } catch (IOException e) {
            call.reject(UNABLE_TO_PROCESS_IMAGE);
            return;
        }
        // Compress the final image and prepare for output to client
        ByteArrayOutputStream bitmapOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, settings.getQuality(), bitmapOutputStream);

        if (settings.getAllowEditing() && !isEdited) {
            editImage(call, u, bitmapOutputStream);
            return;
        }

        boolean saveToGallery = call.getBoolean("saveToGallery", CameraSettings.DEFAULT_SAVE_IMAGE_TO_GALLERY);
        if (saveToGallery && (imageEditedFileSavePath != null || imageFileSavePath != null)) {
            isSaved = true;
            try {
                String fileToSavePath = imageEditedFileSavePath != null ? imageEditedFileSavePath : imageFileSavePath;
                File fileToSave = new File(fileToSavePath);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ContentResolver resolver = plugin.getContext().getContentResolver();
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileToSave.getName());
                    values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
                    values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM);

                    final Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    Uri uri = resolver.insert(contentUri, values);

                    if (uri == null) {
                        throw new IOException("Failed to create new MediaStore record.");
                    }

                    OutputStream stream = resolver.openOutputStream(uri);
                    if (stream == null) {
                        throw new IOException("Failed to open output stream.");
                    }

                    Boolean inserted = bitmap.compress(Bitmap.CompressFormat.JPEG, settings.getQuality(), stream);

                    if (!inserted) {
                        isSaved = false;
                    }
                } else {
                    String inserted = MediaStore.Images.Media.insertImage(
                            plugin.getContext().getContentResolver(),
                            fileToSavePath,
                            fileToSave.getName(),
                            ""
                    );

                    if (inserted == null) {
                        isSaved = false;
                    }
                }
            } catch (FileNotFoundException e) {
                isSaved = false;
                Logger.error(plugin.getLegacyLogTag(), IMAGE_GALLERY_SAVE_ERROR, e);
            } catch (IOException e) {
                isSaved = false;
                Logger.error(plugin.getLegacyLogTag(), IMAGE_GALLERY_SAVE_ERROR, e);
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
        if (imageFileSavePath != null && !settings.getSaveToGallery()) {
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
            ret.put("webPath", FileUtils.getPortablePath(plugin.getContext(), plugin.getBridge().getLocalUrl(), newUri));
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
        } catch (IOException ex) {
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    Logger.error(plugin.getLegacyLogTag(), UNABLE_TO_PROCESS_IMAGE, e);
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
        if (settings.getShouldCorrectOrientation()) {
            final Bitmap newBitmap = ImageUtils.correctOrientation(plugin.getContext(), bitmap, imageUri, exif);
            bitmap = replaceBitmap(bitmap, newBitmap);
        }

        if (settings.getShouldResize()) {
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

    private void editImage(PluginCall call, Uri uri, ByteArrayOutputStream bitmapOutputStream) {
        try {
            Uri tempImage = getTempImage(uri, bitmapOutputStream);
            Intent editIntent = createEditIntent(tempImage);
            if (editIntent != null) {
                plugin.startActivityForResult(call, editIntent, "processEditedImage");
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
            Uri editUri = FileProvider.getUriForFile(plugin.getActivity(), plugin.getContext().getPackageName() + ".fileprovider", editFile);
            Intent editIntent = new Intent(Intent.ACTION_EDIT);
            editIntent.setDataAndType(editUri, "image/*");
            imageEditedFileSavePath = editFile.getAbsolutePath();
            int flags = Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
            editIntent.addFlags(flags);
            editIntent.putExtra(MediaStore.EXTRA_OUTPUT, editUri);

            List<ResolveInfo> resInfoList;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                resInfoList = plugin.getContext()
                        .getPackageManager()
                        .queryIntentActivities(editIntent, PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY));
            } else {
                resInfoList = legacyQueryIntentActivities(editIntent);
            }

            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                plugin.getContext().grantUriPermission(packageName, editUri, flags);
            }
            return editIntent;
        } catch (Exception ex) {
            return null;
        }
    }

    @SuppressWarnings("deprecation")
    private List<ResolveInfo> legacyQueryIntentActivities(Intent intent) {
        return plugin.getContext().getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
    }

    public void onSaveInstanceState(Bundle bundle) {
        if (bundle != null) {
            bundle.putString("cameraImageFileSavePath", imageFileSavePath);
        }
    }

    public void onRestoreState(Bundle state) {
        String storedImageFileSavePath = state.getString("cameraImageFileSavePath");
        if (storedImageFileSavePath != null) {
            imageFileSavePath = storedImageFileSavePath;
        }
    }

    public void onDestroy() {
        if (pickMedia != null) {
            pickMedia.unregister();
        }
        if (pickMultipleMedia != null) {
            pickMultipleMedia.unregister();
        }
    }

}