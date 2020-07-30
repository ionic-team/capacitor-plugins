import AudioToolbox
import UIKit

@objc public class Haptics: NSObject {

    var selectionFeedbackGenerator: UISelectionFeedbackGenerator?

    @objc public func impact(_ style: String?) {
        DispatchQueue.main.async {
            if let style = style {
                var impactStyle = UIImpactFeedbackGenerator.FeedbackStyle.heavy
                if style == "MEDIUM" {
                    impactStyle = UIImpactFeedbackGenerator.FeedbackStyle.medium
                } else if style == "LIGHT" {
                    impactStyle = UIImpactFeedbackGenerator.FeedbackStyle.light
                }

                let generator = UIImpactFeedbackGenerator(style: impactStyle)
                generator.impactOccurred()
            } else {
                let generator = UIImpactFeedbackGenerator(style: .heavy)
                generator.impactOccurred()
            }
        }
    }

    @objc public func notification(_ type: String?) {
        DispatchQueue.main.async {
            let generator = UINotificationFeedbackGenerator()
            if let type = type {
                var notificationType = UINotificationFeedbackGenerator.FeedbackType.success
                if type == "WARNING" {
                    notificationType = UINotificationFeedbackGenerator.FeedbackType.warning
                } else if type == "ERROR" {
                    notificationType = UINotificationFeedbackGenerator.FeedbackType.error
                }
                generator.notificationOccurred(notificationType)
            } else {
                generator.notificationOccurred(.success)
            }
        }
    }

    @objc public func selectionStart() {
        DispatchQueue.main.async {
            self.selectionFeedbackGenerator = UISelectionFeedbackGenerator()
            self.selectionFeedbackGenerator?.prepare()
        }
    }

    @objc public func selectionChanged() {
        DispatchQueue.main.async {
            if let generator = self.selectionFeedbackGenerator {
                generator.selectionChanged()
                generator.prepare()
            }
        }
    }

    @objc public func selectionEnd() {
        DispatchQueue.main.async {
            self.selectionFeedbackGenerator = nil
        }
    }

    @objc public func vibrate() {
        DispatchQueue.main.async {
            AudioServicesPlayAlertSound(kSystemSoundID_Vibrate)
        }
    }
}
