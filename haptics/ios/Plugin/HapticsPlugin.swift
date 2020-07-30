import Foundation
import Capacitor

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(HapticsPlugin)
public class HapticsPlugin: CAPPlugin {
    private let implementation = Haptics()

    @objc func echo(_ call: CAPPluginCall) {
        let value = call.getString("value") ?? ""
        call.success([
            "value": implementation.echo(value)
        ])
    }
}
