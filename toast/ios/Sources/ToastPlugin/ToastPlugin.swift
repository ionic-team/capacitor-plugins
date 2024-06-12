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
        var duration = call.getInt("durationMilliseconds", 0)
        if(duration <= 0) {
            let durationType = call.getString("duration", "short")
            duration = durationType == "long" ? 3500 : 2000
        }
        let position = call.getString("position", "bottom")

        guard let viewController = bridge?.viewController else {
            call.reject("Unable to display toast!")
            return
        }
        Toast.showToast(in: viewController, text: text, duration: duration, position: position, completion: {(_) in
            call.resolve()
        })
    }
}
