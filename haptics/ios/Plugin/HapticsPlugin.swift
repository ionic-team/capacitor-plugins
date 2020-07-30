import Capacitor

@objc(HapticsPlugin)
public class HapticsPlugin: CAPPlugin {
    private let implementation = Haptics()

    @objc public func impact(_ call: CAPPluginCall) {
        implementation.impact(call.options["style"] as? String)
    }

    @objc public func notification(_ call: CAPPluginCall) {
        implementation.notification(call.options["type"] as? String)
    }

    @objc public func selectionStart(_ call: CAPPluginCall) {
        implementation.selectionStart()
    }

    @objc public func selectionChanged(_ call: CAPPluginCall) {
        implementation.selectionChanged()
    }

    @objc public func selectionEnd(_ call: CAPPluginCall) {
        implementation.selectionEnd()
    }

    @objc public func vibrate(_ call: CAPPluginCall) {
        implementation.vibrate()
    }
}
