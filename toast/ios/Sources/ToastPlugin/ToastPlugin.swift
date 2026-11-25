import Foundation
import Capacitor

@objc(ToastPlugin)
public class ToastPlugin: CAPPlugin, CAPBridgedPlugin {
    public let identifier = "ToastPlugin"
    public let jsName = "Toast"
    public let pluginMethods: [CAPPluginMethod] = [
        CAPPluginMethod(name: "show", returnType: CAPPluginReturnPromise)
    ]

    @objc func show(_ call: CAPPluginCall) {
        guard let text = call.getString("text") else {
            call.reject("text must be provided and must be a string.")
            return
        }
        let durationType = call.getString("duration", "short")
        let duration = durationType == "long" ? 3500 : 2000
        let position = call.getString("position", "bottom")

        DispatchQueue.main.async { [weak self] in
            guard var viewController = self?.bridge?.viewController else {
                call.reject("Unable to display toast!")
                return
            }
            // A view controller presented over the webview (e.g. the Browser plugin) hides the
            // bridge view controller's view, so show the toast in the topmost one instead.
            // UIAlertController is skipped: its view is only the alert box.
            while let presented = viewController.presentedViewController,
                  !presented.isBeingDismissed,
                  !(presented is UIAlertController) {
                viewController = presented
            }
            Toast.showToast(in: viewController, text: text, duration: duration, position: position, completion: {(_) in
                call.resolve()
            })
        }
    }
}
