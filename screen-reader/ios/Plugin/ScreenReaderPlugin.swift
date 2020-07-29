import Foundation
import Capacitor

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(ScreenReaderPlugin)
public class ScreenReaderPlugin: CAPPlugin {
    private let implementation = ScreenReader()

    @objc func speak(_ call: CAPPluginCall) {
        guard let value = call.getString("value") else {
            call.error("No value provided")
            return
        }

        implementation.speak(value)

        call.success()
    }
}
