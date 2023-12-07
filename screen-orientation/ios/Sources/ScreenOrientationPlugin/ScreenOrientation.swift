import Foundation
import UIKit
import Capacitor

public class ScreenOrientation: NSObject {

    enum ScreenOrientationError: Error {
        case noWindowScene
    }

    private var supportedOrientations: [Int] = []
    private var capViewController: CAPBridgeViewController?

    public func setCapacitorViewController(_ viewController: CAPBridgeViewController) {
        self.capViewController = viewController
        self.supportedOrientations =  viewController.supportedOrientations
    }

    public func getCurrentOrientationType() -> String {
        let currentOrientation: UIDeviceOrientation = UIDevice.current.orientation
        return fromDeviceOrientationToOrientationType(currentOrientation)
    }

    private func lockLegacy(_ orientation: Int) {
        UIDevice.current.setValue(orientation, forKey: "orientation")
        UINavigationController.attemptRotationToDeviceOrientation()
    }

    public func lock(_ orientationType: String, completion: @escaping (Error?) -> Void) {
        DispatchQueue.main.async {
            let orientation = self.fromOrientationTypeToInt(orientationType)
            self.capViewController?.supportedOrientations = [orientation]
            let mask = self.fromOrientationTypeToMask(orientationType)
            if #available(iOS 16.0, *) {
                if let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene {
                    windowScene.keyWindow?.rootViewController?.setNeedsUpdateOfSupportedInterfaceOrientations()
                    windowScene.requestGeometryUpdate(.iOS(interfaceOrientations: mask)) { error in
                        completion(error)
                    }
                } else {
                    completion(ScreenOrientationError.noWindowScene)
                }
            } else {
                self.lockLegacy(orientation)
            }
            completion(nil)
        }
    }

    public func unlock(completion: @escaping (Error?) -> Void) {
        DispatchQueue.main.async {
            self.capViewController?.supportedOrientations = self.supportedOrientations
            if #available(iOS 16.0, *) {
                if let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene {
                    windowScene.keyWindow?.rootViewController?.setNeedsUpdateOfSupportedInterfaceOrientations()
                    windowScene.requestGeometryUpdate(.iOS(interfaceOrientations: .all)) { error in
                        completion(error)
                    }
                } else {
                    completion(ScreenOrientationError.noWindowScene)
                }
            } else {
                UINavigationController.attemptRotationToDeviceOrientation()
            }
            completion(nil)
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
        case "any":
            return UIInterfaceOrientationMask.all
        case "landscape", "landscape-primary":
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
        case "any":
            return UIInterfaceOrientation.unknown.rawValue
        case "landscape", "landscape-primary":
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
