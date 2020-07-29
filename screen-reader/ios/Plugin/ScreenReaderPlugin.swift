import Foundation
import Capacitor

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(ScreenReaderPlugin)
public class ScreenReaderPlugin: CAPPlugin {
    static let screenReaderStateChangeEvent = "screenReaderStateChange"

    override public func load() {
        NotificationCenter.default.addObserver(self,
                                               selector: #selector(self.onVoiceOverStateChanged(notification:)),
                                               name: UIAccessibility.voiceOverStatusDidChangeNotification,
                                               object: nil)
    }

    @objc func isEnabled(_ call: CAPPluginCall) {
        let enabled = UIAccessibility.isVoiceOverRunning

        call.success([
            "value": enabled
        ])
    }

    @objc func speak(_ call: CAPPluginCall) {
        guard let value = call.getString("value") else {
            call.error("No value provided")
            return
        }

        if UIAccessibility.isVoiceOverRunning {
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) {
                UIAccessibility.post(notification: .announcement, argument: value)
            }
        }

        call.success()
    }

    @objc private func onVoiceOverStateChanged(notification: NSNotification) {
        notifyListeners(ScreenReaderPlugin.screenReaderStateChangeEvent, data: [
            "value": UIAccessibility.isVoiceOverRunning
        ])
    }
}
