import AudioToolbox
import UIKit
import CoreHaptics

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

    @objc public func vibrate(_ duration: Double) {
        if #available(iOS 13, *) {
            if CHHapticEngine.capabilitiesForHardware().supportsHaptics {
                do {
                    let engine = try CHHapticEngine()
                    try engine.start()
                    engine.resetHandler = { [] in
                        do {
                            try engine.start()
                        } catch {
                            self.vibrate()
                        }
                    }
                    let intensity = CHHapticEventParameter(parameterID: .hapticIntensity, value: 1.0)
                    let sharpness = CHHapticEventParameter(parameterID: .hapticSharpness, value: 1.0)

                    let continuousEvent = CHHapticEvent(eventType: .hapticContinuous,
                                                        parameters: [intensity, sharpness],
                                                        relativeTime: 0.0,
                                                        duration: duration)
                    let pattern = try CHHapticPattern(events: [continuousEvent], parameters: [])
                    let player = try engine.makePlayer(with: pattern)

                    try player.start(atTime: 0)
                } catch {
                    vibrate()
                }
            } else {
                vibrate()
            }
        } else {
            vibrate()
        }
    }
}
