import UIKit
import Photos

internal protocol CameraAuthorizationState {
    var authorizationState: String { get }
}

extension AVAuthorizationStatus: CameraAuthorizationState {
    var authorizationState: String {
        switch self {
        case .denied, .restricted:
            return "denied"
        case .authorized:
            return "granted"
        case .notDetermined:
            fallthrough
        @unknown default:
            return "prompt"
        }
    }
}

extension PHAuthorizationStatus: CameraAuthorizationState {
    var authorizationState: String {
        switch self {
        case .denied, .restricted:
            return "denied"
        case .authorized:
            return "granted"
        case .limited:
            return "limited"
        case .notDetermined:
            fallthrough
        @unknown default:
            return "prompt"
        }
    }
}

internal extension PHAsset {
    /**
     Retrieves the image metadata for the asset.
     */
    var imageData: [String: Any] {
        let options = PHImageRequestOptions()
        options.isSynchronous = true
        options.resizeMode = .none
        options.isNetworkAccessAllowed = false
        options.version = .current

        var result: [String: Any] = [:]
        _ = PHCachingImageManager().requestImageDataAndOrientation(for: self, options: options) { (data, _, _, _) in
            if let data = data as NSData? {
                let options = [kCGImageSourceShouldCache as String: kCFBooleanFalse] as CFDictionary
                if let imgSrc = CGImageSourceCreateWithData(data, options),
                   let metadata = CGImageSourceCopyPropertiesAtIndex(imgSrc, 0, options) as? [String: Any] {
                    result = metadata
                }
            }
        }
        return result
    }
}

internal extension UIImage {
    /**
     Generates a new image from the existing one, implicitly resetting any orientation.
     Dimensions greater than 0 will resize the image while preserving the aspect ratio.
     */
    func reformat(to size: CGSize? = nil) -> UIImage {
        let imageHeight = self.size.height
        let imageWidth = self.size.width
        // determine the max dimensions, 0 is treated as 'no restriction'
        var maxWidth: CGFloat
        if let size = size, size.width > 0 {
            maxWidth = size.width
        } else {
            maxWidth = imageWidth
        }
        let maxHeight: CGFloat
        if let size = size, size.height > 0 {
            maxHeight = size.height
        } else {
            maxHeight = imageHeight
        }
        // adjust to preserve aspect ratio
        var targetWidth = min(imageWidth, maxWidth)
        var targetHeight = (imageHeight * targetWidth) / imageWidth
        if targetHeight > maxHeight {
            targetWidth = (imageWidth * maxHeight) / imageHeight
            targetHeight = maxHeight
        }
        // generate the new image and return
        UIGraphicsBeginImageContextWithOptions(.init(width: targetWidth, height: targetHeight), false, 1.0) // size, opaque and scale
        self.draw(in: .init(origin: .zero, size: .init(width: targetWidth, height: targetHeight)))
        let resizedImage = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        return resizedImage ?? self
    }
}
