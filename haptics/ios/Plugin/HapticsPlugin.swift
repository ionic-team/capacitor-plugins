import Capacitor

@objc(HapticsPlugin)
public class HapticsPlugin: CAPPlugin {
    private let implementation = Haptics()

    @objc public func impact(_ call: CAPPluginCall) {
        var impactStyle = UIImpactFeedbackGenerator.FeedbackStyle.heavy
        if let style = call.options["style"] as? String {
            if style == "MEDIUM" {
                impactStyle = UIImpactFeedbackGenerator.FeedbackStyle.medium
            } else if style == "LIGHT" {
                impactStyle = UIImpactFeedbackGenerator.FeedbackStyle.light
            }
        }
        DispatchQueue.main.async {
            self.implementation.impact(impactStyle)
        }
        call.resolve()
    }

    @objc public func notification(_ call: CAPPluginCall) {
        var notificationType = UINotificationFeedbackGenerator.FeedbackType.success
        if let type = call.options["type"] as? String {
            if type == "WARNING" {
                notificationType = UINotificationFeedbackGenerator.FeedbackType.warning
            } else if type == "ERROR" {
                notificationType = UINotificationFeedbackGenerator.FeedbackType.error
            }
        }
        DispatchQueue.main.async {
            self.implementation.notification(notificationType)
        }
        call.resolve()
    }

    @objc public func selectionStart(_ call: CAPPluginCall) {
        DispatchQueue.main.async {
            self.implementation.selectionStart()
        }
        call.resolve()
    }

    @objc public func selectionChanged(_ call: CAPPluginCall) {
        DispatchQueue.main.async {
            self.implementation.selectionChanged()
        }
        call.resolve()
    }

    @objc public func selectionEnd(_ call: CAPPluginCall) {
        DispatchQueue.main.async {
            self.implementation.selectionEnd()
        }
        call.resolve()
    }

    @objc public func vibrate(_ call: CAPPluginCall) {
        DispatchQueue.main.async {
            self.implementation.vibrate()
        }
        call.resolve()
    }
}
