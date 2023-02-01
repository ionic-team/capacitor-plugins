import Foundation
import UIKit

public class ScreenOrientation: NSObject {

    public func getCurrentOrientationType() -> String {
        let currentOrientation: UIDeviceOrientation = UIDevice.current.orientation
        return fromDeviceOrientationToOrientationType(currentOrientation)
    }

    public func lock(_ orientationType: String) async throws -> UIInterfaceOrientationMask {
        let mask = self.fromOrientationTypeToMask(orientationType)
        let orientation = self.fromOrientationTypeToInt(orientationType)
        #if swift(>=5.7)
        if #available(iOS 16, *) {
            let windowScene = await UIApplication.shared.connectedScenes.first as? UIWindowScene
            await windowScene?.keyWindow?.rootViewController?.setNeedsUpdateOfSupportedInterfaceOrientations()
            await windowScene?.requestGeometryUpdate(.iOS(interfaceOrientations: mask))
        } else {
            await UIDevice.current.setValue(orientation, forKey: "orientation")
            await UINavigationController.attemptRotationToDeviceOrientation()
        }
        #else
        await UIDevice.current.setValue(orientation, forKey: "orientation")
        await UINavigationController.attemptRotationToDeviceOrientation()
        #endif
        return mask
    }

    public func unlock() async throws {
        #if swift(>=5.7)
        if #available(iOS 16, *) {
            let windowScene = await UIApplication.shared.connectedScenes.first as? UIWindowScene
            await windowScene?.keyWindow?.rootViewController?.setNeedsUpdateOfSupportedInterfaceOrientations()
            await windowScene?.requestGeometryUpdate(.iOS(interfaceOrientations: .all))
        } else {
            await UIDevice.current.setValue(UIInterfaceOrientation.unknown.rawValue, forKey: "orientation")
            await UINavigationController.attemptRotationToDeviceOrientation()
        }
        #else
        await UIDevice.current.setValue(UIInterfaceOrientation.unknown.rawValue, forKey: "orientation")
        await UINavigationController.attemptRotationToDeviceOrientation()
        #endif
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
