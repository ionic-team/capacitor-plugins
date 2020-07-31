import Capacitor

@objc(HapticsPlugin)
public class HapticsPlugin: CAPPlugin {
    private let implementation = Haptics()

    @objc public func impact(_ call: CAPPluginCall) {
        implementation.impact(call.options["style"] as? String)
        call.resolve()
    }

    @objc public func notification(_ call: CAPPluginCall) {
        implementation.notification(call.options["type"] as? String)
        call.resolve()
    }

    @objc public func selectionStart(_ call: CAPPluginCall) {
        implementation.selectionStart()
        call.resolve()
    }

    @objc public func selectionChanged(_ call: CAPPluginCall) {
        implementation.selectionChanged()
        call.resolve()
    }

    @objc public func selectionEnd(_ call: CAPPluginCall) {
        implementation.selectionEnd()
        call.resolve()
    }

    @objc public func vibrate(_ call: CAPPluginCall) {
        implementation.vibrate()
        call.resolve()
    }
}
