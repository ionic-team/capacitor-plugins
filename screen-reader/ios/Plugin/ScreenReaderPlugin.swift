import Foundation
import Capacitor

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(ScreenReaderPlugin)
public class ScreenReaderPlugin: CAPPlugin {
// swiftlint:disable:previous type_name
    private let implementation = ScreenReader()

    @objc func echo(_ call: CAPPluginCall) {
        let value = call.getString("value") ?? ""
        call.success([
            "value": implementation.echo(value)
        ])
    }
}
