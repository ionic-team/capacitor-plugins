import Foundation
import Capacitor

@objc(TextZoomPlugin)
public class TextZoomPlugin: CAPPlugin, CAPBridgedPlugin {
    public let identifier = "TextZoomPlugin"
    public let jsName = "TextZoom"
    public let pluginMethods: [CAPPluginMethod] = [
        CAPPluginMethod(name: "getPreferred", returnType: CAPPluginReturnPromise)
    ]
    private let textZoom = TextZoom()

    @objc func getPreferred(_ call: CAPPluginCall) {
        call.resolve([
            "value": textZoom.preferredFontSize()
        ])
    }
}
