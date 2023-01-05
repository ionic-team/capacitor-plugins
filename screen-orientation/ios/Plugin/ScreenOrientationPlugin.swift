import Foundation
import Capacitor

@objc(ScreenOrientationPlugin)
public class ScreenOrientationPlugin: CAPPlugin {

    private let implementation = ScreenOrientation()
    public static var supportedOrientations = UIInterfaceOrientationMask.all

    override public func load() {
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(self.orientationDidChange),
            name: UIDevice.orientationDidChangeNotification,
            object: nil)
    }

    deinit {
        NotificationCenter.default.removeObserver(self)
    }

    @objc public func orientation(_ call: CAPPluginCall) {
        let orientationType = implementation.getCurrentOrientationType()
        call.resolve(["type": orientationType])
    }

    @objc public func lock(_ call: CAPPluginCall) {
        guard let lockToOrientation = call.getString("orientation") else {
            call.reject("Input option 'orientation' must be provided.")
            return
        }
        implementation.lock(lockToOrientation, completion: { (mask) -> Void in
            ScreenOrientationPlugin.supportedOrientations = mask
            call.resolve()
        })
    }

    @objc public func unlock(_ call: CAPPluginCall) {
        implementation.unlock {
            ScreenOrientationPlugin.supportedOrientations = UIInterfaceOrientationMask.all
            call.resolve()
        }
    }

    @objc private func orientationDidChange() {
        // Ignore changes in orientation if unknown, face up, or face down
        if UIDevice.current.orientation.isValidInterfaceOrientation {
            let orientation = implementation.getCurrentOrientationType()
            notifyListeners("screenOrientationChange", data: ["type": orientation])
        }
    }
}
