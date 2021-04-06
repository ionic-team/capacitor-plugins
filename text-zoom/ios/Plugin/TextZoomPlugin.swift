import Foundation
import Capacitor

@objc(TextZoomPlugin)
public class TextZoomPlugin: CAPPlugin {
    private let textZoom = TextZoom()

    @objc func getPreferred(_ call: CAPPluginCall) {
        call.resolve([
            "value": textZoom.preferredFontSize()
        ])
    }

    @objc func get(_ call: CAPPluginCall) {
        call.unimplemented("Not available on iOS")
    }

    @objc func set(_ call: CAPPluginCall) {
        call.unimplemented("Not available on iOS")
    }
}
