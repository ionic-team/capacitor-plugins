package com.capacitorjs.plugins.camera

class CameraSettings {
    var resultType: CameraResultType? = CameraResultType.BASE64
    var quality: Int = DEFAULT_QUALITY
    var shouldResize: Boolean = false
    var shouldCorrectOrientation: Boolean = DEFAULT_CORRECT_ORIENTATION
    var saveToGallery: Boolean = DEFAULT_SAVE_IMAGE_TO_GALLERY
    var allowEditing: Boolean = false
    var width: Int = 0
    var height: Int = 0
    var source: CameraSource? = CameraSource.PROMPT

    companion object {
        const val DEFAULT_QUALITY: Int = 90
        const val DEFAULT_SAVE_IMAGE_TO_GALLERY: Boolean = false
        const val DEFAULT_CORRECT_ORIENTATION: Boolean = true
    }
}
