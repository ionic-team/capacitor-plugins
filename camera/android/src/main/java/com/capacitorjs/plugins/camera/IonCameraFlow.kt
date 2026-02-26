package com.capacitorjs.plugins.camera


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.getcapacitor.FileUtils
import com.getcapacitor.JSObject
import com.getcapacitor.PermissionState
import com.getcapacitor.PluginCall
import io.ionic.libs.ioncameralib.helper.OSCAMRExifHelper
import io.ionic.libs.ioncameralib.helper.OSCAMRFileHelper
import io.ionic.libs.ioncameralib.helper.OSCAMRImageHelper
import io.ionic.libs.ioncameralib.helper.OSCAMRMediaHelper
import io.ionic.libs.ioncameralib.manager.CameraManager
import io.ionic.libs.ioncameralib.model.IONError
import io.ionic.libs.ioncameralib.model.IONMediaResult
import io.ionic.libs.ioncameralib.model.IONParameters
import java.io.File


class IonCameraFlow(
    private val plugin: CameraPlugin
) {
    private var isFirstRequest = true
    private var cameraManager: CameraManager? = null
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var cropLauncher: ActivityResultLauncher<Intent>

    private var currentCall: PluginCall? = null
    private var settings = CameraSettings()

    fun load() {
        setupLaunchers()
        cameraManager = CameraManager(
            plugin.getAppId(),
            ".fileprovider",
            cameraLauncher,
            OSCAMRExifHelper(),
            OSCAMRFileHelper(),
            OSCAMRMediaHelper(),
            OSCAMRImageHelper()
        )
    }

    fun takePhoto(call: PluginCall) {
        settings = plugin.getSettings(call)
        currentCall = call
        doShow(call)
    }

    // ----------------------------------------------------
    // Launchers
    // ----------------------------------------------------
    private fun setupLaunchers() {
        cameraLauncher = plugin.activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            handleCameraResult(result)
        }

        cropLauncher = plugin.activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            handleCropResult(result)
        }

    }

    private fun doShow(call: PluginCall) {
        when (settings.source) {
            CameraSource.CAMERA -> showCamera(call)
            //  CameraSource.PHOTOS -> showPhotos(call)
            else -> Log.d("CAMERA_DEBUG", "PROMPT ")//showPrompt(call)
        }
    }

    private fun showCamera(call: PluginCall) {
        if (!plugin.getContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
        ) {
            sendError(IONError.NO_CAMERA_AVAILABLE_ERROR)
            return
        }
        openCamera(call)
    }

    fun openCamera(call: PluginCall) {
        if (checkCameraPermissions(call)) {
            try {
                val manager = cameraManager ?: run {
                    sendError(IONError.CONTEXT_ERROR)
                    return
                }

                currentCall = call
                manager.takePhoto(plugin.getActivity(), CameraPlugin.ENCODING_TYPE)
            } catch (ex: Exception) {
                sendError(IONError.FAILED_TO_CAPTURE_IMAGE_ERROR)
            }
        }
    }

    private fun handleCameraResult(result: ActivityResult) {
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                if (settings.allowEditing) {
                    editPhoto()
                } else {
                    processResult()
                }
            }

            Activity.RESULT_CANCELED -> {
                sendError(IONError.NO_PICTURE_TAKEN_ERROR)
            }

            else -> {
                sendError(IONError.TAKE_PHOTO_ERROR)
            }
        }
    }

    private fun editPhoto() {
        val manager = cameraManager ?: run {
            sendError(IONError.CONTEXT_ERROR)
            return
        }

        val appId = plugin.getAppId()
        val tmpFile = FileProvider.getUriForFile(
            plugin.activity,
            "$appId.fileprovider",
            manager.createCaptureFile(
                plugin.activity,
                CameraPlugin.ENCODING_TYPE,
                plugin.activity.getSharedPreferences(
                    CameraPlugin.STORE,
                    Context.MODE_PRIVATE
                ).getString(CameraPlugin.EDIT_FILE_NAME_KEY, "") ?: ""
            )
        )

        manager.openCropActivity(
            plugin.activity,
            tmpFile,
            cropLauncher
        )
    }

    private fun handleCropResult(result: ActivityResult) {
        when (result.resultCode) {
            Activity.RESULT_OK -> processResult()
            Activity.RESULT_CANCELED -> sendError(IONError.EDIT_OPERATION_CANCELLED_ERROR)
            else -> sendError(IONError.EDIT_IMAGE_ERROR)
        }
    }

    private fun handleBase64Result(image: String) {
        val ret = JSObject()
        ret.put("format", "jpeg")

        when (settings.resultType) {
            CameraResultType.BASE64 -> {
                ret.put("base64String", image)
            }

            CameraResultType.DATAURL -> {
                ret.put("dataUrl", "data:image/jpeg;base64,$image")
            }

            else -> {
                sendError(IONError.PROCESS_IMAGE_ERROR)
                return
            }
        }
        currentCall?.resolve(ret)
        currentCall = null
    }

    private fun handleMediaResult(mediaResult: IONMediaResult) {
        val file = File(mediaResult.uri)
        val uri = Uri.fromFile(file)
        val bitmap = BitmapFactory.decodeFile(mediaResult.uri)
        if (bitmap == null) {
            sendError(IONError.PROCESS_IMAGE_ERROR)
            return
        }

        val exif = ImageUtils.getExifData(plugin.context, bitmap, uri)
        val ret = JSObject()
        ret.put("format", "jpeg")
        ret.put("exif", exif.toJson())
        ret.put("path", uri.toString())
        ret.put("webPath", FileUtils.getPortablePath(plugin.context, plugin.bridge.localUrl, uri))
        ret.put("saved", mediaResult.saved)
        currentCall?.resolve(ret)
        currentCall = null

    }

    private fun processResult() {
        val manager = cameraManager ?: return
        val ionParams = settings.toIonParameters()
        manager.processResultFromCamera(
            plugin.activity,
            ionParams,
            { image ->
                handleBase64Result(image)
            },
            { mediaResult ->
                handleMediaResult(mediaResult)
            },
            { error ->
                sendError(error)
            }
        )
    }

    private fun CameraSettings.toIonParameters(): IONParameters {
        val useLatestVersion = (resultType == CameraResultType.URI)
        return IONParameters(
            mQuality = quality,
            targetWidth = width,
            targetHeight = height,
            encodingType = CameraPlugin.ENCODING_TYPE, // JPEG
            mediaType = CameraPlugin.MEDIA_TYPE_PICTURE,
            allowEdit = allowEditing,
            correctOrientation = shouldCorrectOrientation,
            saveToPhotoAlbum = saveToGallery,
            includeMetadata = true, // Keep true
            latestVersion = useLatestVersion
        )
    }

    fun checkCameraPermissions(call: PluginCall): Boolean {
        // if the manifest does not contain the camera permissions key, we don't need to ask the user
        val needCameraPerms = plugin.isPermissionDeclared(CameraPlugin.CAMERA)
        val hasCameraPerms =
            !needCameraPerms || plugin.getPermissionState(CameraPlugin.CAMERA) == PermissionState.GRANTED
        val hasGalleryPerms =
            plugin.getPermissionState(CameraPlugin.SAVE_GALLERY) == PermissionState.GRANTED

        // If we want to save to the gallery, we need two permissions
        // actually we only need permissions to save to gallery for Android <= 9 (API 28)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // we might still need to request permission for the camera
            if (!hasCameraPerms) {
                plugin.requestLegacyPermissionForAlias(
                    CameraPlugin.CAMERA,
                    call,
                    "cameraPermissionsCallback"
                )
                return false
            }
            return true
        }

        // we need to request permissions to save to gallery for Android <= 9
        if (settings.saveToGallery && !(hasCameraPerms && hasGalleryPerms) && isFirstRequest) {
            isFirstRequest = false
            val aliases: Array<String> = if (needCameraPerms) {
                arrayOf(CameraPlugin.CAMERA, CameraPlugin.SAVE_GALLERY)
            } else {
                arrayOf(CameraPlugin.SAVE_GALLERY)
            }
            plugin.requestLegacyPermissionForAliases(aliases, call, "cameraPermissionsCallback")
            return false
        } else if (!hasCameraPerms) {
            plugin.requestLegacyPermissionForAlias(
                CameraPlugin.CAMERA,
                call,
                "cameraPermissionsCallback"
            )
            return false
        }
        return true
    }


    private fun sendError(error: IONError) {
        try {
            val jsonResult = JSObject()
            jsonResult.put("code", formatErrorCode(error.code))
            jsonResult.put("message", error.description)
            currentCall?.reject(error.description, formatErrorCode(error.code))
            currentCall = null
        } catch (e: Exception) {
            currentCall?.reject("There was an error performing the operation.")
            currentCall = null
        }
    }

    private fun formatErrorCode(code: Int): String {
        val stringCode = Integer.toString(code)
        return CameraPlugin.ERROR_FORMAT_PREFIX + "0000$stringCode".substring(stringCode.length)
    }
}