import Foundation
import Capacitor
import Photos

@objc(CAPCameraPlugin)
public class CameraPlugin: CAPPlugin {
    private var call: CAPPluginCall?
    private var settings = CameraSettings()
    private var imagePicker: UIImagePickerController?

    private let defaultSource = CameraSource.prompt
    private let defaultDirection = CameraDirection.rear

    private var imageCounter = 0

    @objc func checkPermissions(_ call: CAPPluginCall) {
        var result: [String: Any] = [:]
        for permission in CameraPermissionType.allCases {
            let state: String
            switch permission {
            case .camera:
                state = AVCaptureDevice.authorizationStatus(for: .video).authorizationState
            case .writePhotos:
                // inserting captured photos into the camera roll is always allowed
                state = PHAuthorizationStatus.authorized.authorizationState
            case .readPhotos:
                state = PHPhotoLibrary.authorizationStatus().authorizationState
            }
            result[permission.rawValue] = state
        }
        call.resolve(result)
    }

    @objc func requestPermissions(_ call: CAPPluginCall) {
        // get the list of desired types, if passed
        let typeList = call.getArray("types", String.self)?.compactMap({ (type) -> CameraPermissionType? in
            return CameraPermissionType(rawValue: type)
        }) ?? []
        // otherwise check everything
        let permissions: [CameraPermissionType] = (typeList.count > 0) ? typeList : CameraPermissionType.allCases
        // request the permissions
        var result: [String: Any] = [:]
        let group = DispatchGroup()
        for permission in permissions {
            switch permission {
            case .camera:
                group.enter()
                AVCaptureDevice.requestAccess(for: .video) { granted in
                    result[permission.rawValue] = granted ?
                        AVAuthorizationStatus.authorized.authorizationState :
                        AVAuthorizationStatus.denied.authorizationState
                    group.leave()
                }
            case .writePhotos:
                result[permission.rawValue] = PHAuthorizationStatus.authorized.authorizationState
            case .readPhotos:
                group.enter()
                PHPhotoLibrary.requestAuthorization({ (status) in
                    result[permission.rawValue] = status.authorizationState
                    group.leave()
                })
            }
        }
        group.notify(queue: DispatchQueue.main) {
            call.resolve(result)
        }
    }

    @objc func getPhoto(_ call: CAPPluginCall) {
        self.call = call
        self.settings = cameraSettings(from: call)

        // Make sure they have all the necessary info.plist settings
        if let missingUsageDescription = checkUsageDescriptions() {
            bridge?.modulePrint(self, missingUsageDescription)
            call.reject(missingUsageDescription)
            bridge?.alert("Camera Error", "Missing required usage description. See console for more information")
            return
        }

        DispatchQueue.main.async {
            self.imagePicker = UIImagePickerController()
            self.imagePicker?.delegate = self
            self.imagePicker?.allowsEditing = self.settings.allowEditing

            switch self.settings.source {
            case .prompt:
                self.showPrompt()
            case .camera:
                self.showCamera()
            case .photos:
                self.showPhotos()
            }
        }
    }

    private func checkUsageDescriptions() -> String? {
        if let dict = Bundle.main.infoDictionary {
            for key in CameraPropertyListKeys.allCases where dict[key.rawValue] == nil {
                return key.missingMessage
            }
        }
        return nil
    }

    private func cameraSettings(from call: CAPPluginCall) -> CameraSettings {
        var settings = CameraSettings()
        settings.jpegQuality = min(abs(CGFloat(call.getFloat("quality") ?? 100.0)) / 100.0, 1.0)
        settings.allowEditing = call.getBool("allowEditing") ?? false
        settings.source = CameraSource(rawValue: call.getString("source") ?? defaultSource.rawValue) ?? defaultSource
        settings.direction = CameraDirection(rawValue: call.getString("direction") ?? defaultDirection.rawValue) ?? defaultDirection
        if let typeString = call.getString("resultType"), let type = CameraResultType(rawValue: typeString) {
            settings.resultType = type
        }
        settings.saveToGallery = call.getBool("saveToGallery") ?? false

        // Get the new image dimensions if provided
        settings.width = CGFloat(call.getInt("width") ?? 0)
        settings.height = CGFloat(call.getInt("height") ?? 0)
        if settings.width > 0 || settings.height > 0 {
            // We resize only if a dimension was provided
            settings.shouldResize = true
        }
        settings.shouldCorrectOrientation = call.getBool("correctOrientation") ?? true
        settings.userPromptText = CameraPromptText(title: call.getString("promptLabelHeader"),
                                                   photoAction: call.getString("promptLabelPhoto"),
                                                   cameraAction: call.getString("promptLabelPicture"),
                                                   cancelAction: call.getString("promptLabelCancel"))
        if let styleString = call.getString("presentationStyle"), styleString == "popover" {
            settings.presentationStyle = .popover
        } else {
            settings.presentationStyle = .fullScreen
        }

        return settings
    }
}

// public delegate methods
extension CameraPlugin: UIImagePickerControllerDelegate, UINavigationControllerDelegate, UIPopoverPresentationControllerDelegate {
    public func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
        picker.dismiss(animated: true)
        self.call?.reject("User cancelled photos app")
    }

    public func popoverPresentationControllerDidDismissPopover(_ popoverPresentationController: UIPopoverPresentationController) {
        self.call?.reject("User cancelled photos app")
    }

    public func presentationControllerDidDismiss(_ presentationController: UIPresentationController) {
        self.call?.reject("User cancelled photos app")
    }

    public func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey: Any]) {

        guard let processedImage = self.processImage(from: info) else {
            self.call?.reject("Error processing image")
            return
        }

        guard let jpeg = processedImage.generateJPEG(with: settings.jpegQuality) else {
            self.call?.reject("Unable to convert image to jpeg")
            return
        }

        if settings.resultType == CameraResultType.base64 {
            call?.resolve([
                "base64String": jpeg.base64EncodedString(),
                "exif": processedImage.exifData,
                "format": "jpeg"
            ])
        } else if settings.resultType == CameraResultType.dataURL {
            call?.resolve([
                "dataUrl": "data:image/jpeg;base64," + jpeg.base64EncodedString(),
                "exif": processedImage.exifData,
                "format": "jpeg"
            ])
        } else if settings.resultType == CameraResultType.uri {
            guard let path = try? saveTemporaryImage(jpeg),
                  let webPath = CAPFileManager.getPortablePath(host: bridge?.getLocalUrl() ?? "", uri: URL(string: path)) else {
                call?.reject("Unable to get portable path to file")
                return
            }
            call?.resolve([
                "path": path,
                "exif": processedImage.exifData,
                "webPath": webPath,
                "format": "jpeg"
            ])
        }

        picker.dismiss(animated: true, completion: nil)
    }
}

private extension CameraPlugin {
    func showPrompt() {
        // Build the action sheet
        let alert = UIAlertController(title: settings.userPromptText.title, message: nil, preferredStyle: UIAlertController.Style.actionSheet)
        alert.addAction(UIAlertAction(title: settings.userPromptText.photoAction, style: .default, handler: { [weak self] (_: UIAlertAction) in
            self?.showPhotos()
        }))

        alert.addAction(UIAlertAction(title: settings.userPromptText.cameraAction, style: .default, handler: { [weak self] (_: UIAlertAction) in
            self?.showCamera()
        }))

        alert.addAction(UIAlertAction(title: settings.userPromptText.cancelAction, style: .cancel, handler: { [weak self] (_: UIAlertAction) in
            self?.call?.reject("User cancelled photos app")
        }))

        self.bridge?.viewController?.present(alert, animated: true, completion: nil)
    }

    func showCamera() {
        // check if we have a camera
        if (bridge?.isSimulator() ?? false) || !UIImagePickerController.isSourceTypeAvailable(UIImagePickerController.SourceType.camera) {
            bridge?.modulePrint(self, "Camera not available in simulator")
            bridge?.alert("Camera Error", "Camera not available in Simulator")
            call?.reject("Camera not available while running in Simulator")
            return
        }
        // check for permission
        let authStatus = AVCaptureDevice.authorizationStatus(for: .video)
        if authStatus == .restricted || authStatus == .denied {
            call?.reject("User denied access to camera")
            return
        }
        // we either already have permission or can prompt
        AVCaptureDevice.requestAccess(for: .video) { [weak self] granted in
            if granted {
                DispatchQueue.main.async {
                    guard let imagePicker = self?.imagePicker, let settings = self?.settings else {
                        return
                    }
                    // select the input
                    if settings.direction == .rear, UIImagePickerController.isCameraDeviceAvailable(.rear) {
                        imagePicker.cameraDevice = .rear
                    } else if settings.direction == .front, UIImagePickerController.isCameraDeviceAvailable(.front) {
                        imagePicker.cameraDevice = .front
                    }
                    // present
                    imagePicker.modalPresentationStyle = settings.presentationStyle
                    imagePicker.sourceType = .camera

                    self?.bridge?.viewController?.present(imagePicker, animated: true, completion: nil)
                }
            } else {
                self?.call?.reject("User denied access to camera")
            }
        }
    }

    func showPhotos() {
        // check for permission
        let authStatus = PHPhotoLibrary.authorizationStatus()
        if authStatus == .restricted || authStatus == .denied {
            call?.reject("User denied access to photos")
            return
        }
        // we either already have permission or can prompt
        if authStatus == .authorized {
            presentPhotoPicker()
        } else {
            PHPhotoLibrary.requestAuthorization({ [weak self] (status) in
                if status == PHAuthorizationStatus.authorized {
                    DispatchQueue.main.async { [weak self] in
                        self?.presentPhotoPicker()
                    }
                } else {
                    self?.call?.reject("User denied access to photos")
                }
            })
        }
    }

    func presentPhotoPicker() {
        guard let imagePicker = imagePicker else {
            return
        }
        if settings.presentationStyle == .popover {
            imagePicker.modalPresentationStyle = .popover
            imagePicker.popoverPresentationController?.delegate = self
            setCenteredPopover(imagePicker)
        }
        imagePicker.sourceType = .photoLibrary
        self.bridge?.viewController?.present(imagePicker, animated: true, completion: nil)
    }

    func saveTemporaryImage(_ data: Data) throws -> String {
        var url: URL
        repeat {
            imageCounter += 1
            url = URL(fileURLWithPath: NSTemporaryDirectory()).appendingPathComponent("photo-\(imageCounter).jpg")
        } while FileManager.default.fileExists(atPath: url.absoluteString)

        try data.write(to: url, options: .atomic)
        return url.absoluteString
    }

    func processImage(from info: [UIImagePickerController.InfoKey: Any]) -> ProcessedImage? {
        // get the image
        var result: ProcessedImage
        var flags: PhotoFlags = []
        if let image = info[UIImagePickerController.InfoKey.editedImage] as? UIImage {
            result = ProcessedImage(image: image, metadata: [:]) // use the edited version
            flags = flags.union([.edited])
        } else if let image = info[UIImagePickerController.InfoKey.originalImage] as? UIImage {
            result = ProcessedImage(image: image, metadata: [:]) // use the original version
        } else {
            return nil
        }
        // get the image's metadata from the picker or from the photo album
        if let photoMetadata = info[UIImagePickerController.InfoKey.mediaMetadata] as? [String: Any] {
            result.metadata = photoMetadata
            flags = flags.union([.gallery])
        }
        if let asset = info[UIImagePickerController.InfoKey.phAsset] as? PHAsset {
            result.metadata = asset.imageData
        }
        // resizing the image only makes sense if we have real values to which to constrain it
        if settings.shouldResize, settings.width > 0 || settings.height > 0 {
            result.image = result.image.reformat(to: CGSize(width: settings.width, height: settings.height))
            result.overwriteMetadataOrientation(to: 1)
        } else if settings.shouldCorrectOrientation {
            // resizing implicitly reformats the image so this is only needed if we aren't resizing
            result.image = result.image.reformat()
            result.overwriteMetadataOrientation(to: 1)
        }
        // conditionally save the image
        if settings.saveToGallery, flags.contains(.edited) == true, flags.contains(.gallery) == false {
            UIImageWriteToSavedPhotosAlbum(result.image, nil, nil, nil)
        }

        return result
    }
}
