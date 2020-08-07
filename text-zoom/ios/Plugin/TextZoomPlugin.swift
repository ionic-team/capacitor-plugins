import Foundation
import Capacitor

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(TextZoomPlugin)
public class TextZoomPlugin: CAPPlugin {
    private let textZoom = TextZoom()

    @objc func getPreferred(_ call: CAPPluginCall) {
        call.resolve([
            "value": textZoom.getPreferred()
        ])
    }
}
