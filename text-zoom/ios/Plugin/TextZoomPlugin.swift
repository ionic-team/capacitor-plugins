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
}
