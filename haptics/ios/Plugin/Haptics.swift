import AudioToolbox
import UIKit

@objc public class Haptics: NSObject {

    var selectionFeedbackGenerator: UISelectionFeedbackGenerator?

    @objc public func impact(_ impactStyle: UIImpactFeedbackGenerator.FeedbackStyle) {
        let generator = UIImpactFeedbackGenerator(style: impactStyle)
        generator.impactOccurred()
    }

    @objc public func notification(_ notificationType: UINotificationFeedbackGenerator.FeedbackType) {
        let generator = UINotificationFeedbackGenerator()
        generator.notificationOccurred(notificationType)
    }

    @objc public func selectionStart() {
        self.selectionFeedbackGenerator = UISelectionFeedbackGenerator()
        self.selectionFeedbackGenerator?.prepare()
    }

    @objc public func selectionChanged() {
        if let generator = self.selectionFeedbackGenerator {
            generator.selectionChanged()
            generator.prepare()
        }
    }

    @objc public func selectionEnd() {
        self.selectionFeedbackGenerator = nil
    }

    @objc public func vibrate() {
        AudioServicesPlayAlertSound(kSystemSoundID_Vibrate)
    }
}
