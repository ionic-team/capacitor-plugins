import Foundation
import Capacitor

@objc(ScreenReaderPlugin)
public class ScreenReaderPlugin: CAPPlugin, CAPBridgedPlugin {
    public let identifier = "ScreenReaderPlugin"
    public let jsName = "ScreenReader"
    public let pluginMethods: [CAPPluginMethod] = [
        CAPPluginMethod(name: "speak", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "isEnabled", returnType: CAPPluginReturnPromise)
    ]
    static let stateChangeEvent = "stateChange"

    override public func load() {
        NotificationCenter.default.addObserver(self,
                                               selector: #selector(self.onVoiceOverStateChanged(notification:)),
                                               name: UIAccessibility.voiceOverStatusDidChangeNotification,
                                               object: nil)
    }

    deinit {
        NotificationCenter.default.removeObserver(self)
    }

    @objc func isEnabled(_ call: CAPPluginCall) {
        let enabled = UIAccessibility.isVoiceOverRunning

        call.resolve([
            "value": enabled
        ])
    }

    @objc func speak(_ call: CAPPluginCall) {
        guard let value = call.getString("value") else {
            call.reject("No value provided")
            return
        }

        if UIAccessibility.isVoiceOverRunning {
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) {
                UIAccessibility.post(notification: .announcement, argument: value)
            }
        }

        call.resolve()
    }

    @objc private func onVoiceOverStateChanged(notification: NSNotification) {
        notifyListeners(ScreenReaderPlugin.stateChangeEvent, data: [
            "value": UIAccessibility.isVoiceOverRunning
        ])
    }
}
