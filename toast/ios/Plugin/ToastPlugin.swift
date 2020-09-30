import Foundation
import Capacitor

@objc(ToastPlugin)
public class ToastPlugin: CAPPlugin {

    @objc func show(_ call: CAPPluginCall) {
        guard let text = call.get("text", String.self) else {
            call.reject("text must be provided and must be a string.")
            return
        }
        let durationType = call.get("duration", String.self, "short")!
        let duration = durationType == "long" ? 3500 : 2000
        let position = call.get("position", String.self, "bottom")!

        guard let viewController = bridge?.viewController else {
            call.reject("Unable to display toast!")
            return
        }
        Toast.showToast(in: viewController, text: text, duration: duration, position: position, completion: {(_) in
            call.resolve()
        })
    }
}
