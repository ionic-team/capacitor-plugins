import Foundation
import UIKit

public class ScreenOrientation: NSObject {

    public func getCurrentOrientationType() -> String {
        let currentOrientation: UIDeviceOrientation = UIDevice.current.orientation
        return fromDeviceOrientationToOrientationType(currentOrientation)
    }

    public func lock(_ orientationType: String, completion: @escaping (UIInterfaceOrientationMask) -> Void) {
        DispatchQueue.main.async {
            let mask = self.fromOrientationTypeToMask(orientationType)
            let orientation = self.fromOrientationTypeToInt(orientationType)
            #if swift(>=5.7)
            if #available(iOS 16, *) {
                let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene
                windowScene?.keyWindow?.rootViewController?.setNeedsUpdateOfSupportedInterfaceOrientations()
                windowScene?.requestGeometryUpdate(.iOS(interfaceOrientations: mask)) { error in
                    print(error)
                    print(windowScene?.effectiveGeometry ?? "")
                }
            } else {
                UIDevice.current.setValue(orientation, forKey: "orientation")
                UINavigationController.attemptRotationToDeviceOrientation()
                completion(mask)
            }
            #else
            UIDevice.current.setValue(orientation, forKey: "orientation")
            UINavigationController.attemptRotationToDeviceOrientation()
            completion(mask)
            #endif
        }
    }

    public func unlock(completion: @escaping () -> Void) {
        DispatchQueue.main.async {
            #if swift(>=5.7)
            if #available(iOS 16, *) {
                let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene
                windowScene?.keyWindow?.rootViewController?.setNeedsUpdateOfSupportedInterfaceOrientations()
                windowScene?.requestGeometryUpdate(.iOS(interfaceOrientations: .all)) { error in
                    print(error)
                    print(windowScene?.effectiveGeometry ?? "")
                }
            } else {
                UIDevice.current.setValue(UIInterfaceOrientation.unknown.rawValue, forKey: "orientation")
                UINavigationController.attemptRotationToDeviceOrientation()
                completion()
            }
            #else
            UIDevice.current.setValue(UIInterfaceOrientation.unknown.rawValue, forKey: "orientation")
            UINavigationController.attemptRotationToDeviceOrientation()
            completion()
            #endif
        }
    }

    private func fromDeviceOrientationToOrientationType(_ orientation: UIDeviceOrientation) -> String {
        switch orientation {
        case .landscapeLeft:
            return "landscape-primary"
        case .landscapeRight:
            return "landscape-secondary"
        case .portraitUpsideDown:
            return "portrait-secondary"
        default:
            // Case: portrait
            return "portrait-primary"
        }
    }

    private func fromOrientationTypeToMask(_ orientationType: String) -> UIInterfaceOrientationMask {
        switch orientationType {
        case "landscape-primary":
            return UIInterfaceOrientationMask.landscapeLeft
        case "landscape-secondary":
            return UIInterfaceOrientationMask.landscapeRight
        case "portrait-secondary":
            return UIInterfaceOrientationMask.portraitUpsideDown
        default:
            // Case: portrait-primary
            return UIInterfaceOrientationMask.portrait
        }
    }

    private func fromOrientationTypeToInt(_ orientationType: String) -> Int {
        switch orientationType {
        case "landscape-primary":
            return UIInterfaceOrientation.landscapeLeft.rawValue
        case "landscape-secondary":
            return UIInterfaceOrientation.landscapeRight.rawValue
        case "portrait-secondary":
            return UIInterfaceOrientation.portraitUpsideDown.rawValue
        default:
            // Case: portrait-primary
            return UIInterfaceOrientation.portrait.rawValue
        }
    }

}
