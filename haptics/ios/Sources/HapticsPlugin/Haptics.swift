import AudioToolbox
import UIKit
import CoreHaptics

@objc public class Haptics: NSObject {

    var selectionFeedbackGenerator: UISelectionFeedbackGenerator?

    private var hapticEngine: CHHapticEngine?
    private var idleTimer: Timer?
    private let idleInterval: TimeInterval = 5.0

    private func initializeEngine() {
        do {
            let engine = try CHHapticEngine()
            engine.resetHandler = { [weak self] in
                do {
                    try self?.hapticEngine?.start()
                } catch {
                    self?.hapticEngine = nil
                }
            }
            engine.stoppedHandler = { [weak self] _ in
                self?.hapticEngine = nil
            }
            try engine.start()
            hapticEngine = engine
        } catch {
            hapticEngine = nil
        }
    }

    private func resetIdleTimer(after duration: TimeInterval) {
        idleTimer?.invalidate()
        idleTimer = Timer.scheduledTimer(
            withTimeInterval: duration + idleInterval, repeats: false
        ) { [weak self] _ in
            self?.shutdownEngine()
        }
    }

    private func shutdownEngine() {
        idleTimer?.invalidate()
        idleTimer = nil
        hapticEngine?.stop(completionHandler: nil)
        hapticEngine = nil
    }

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
        if CHHapticEngine.capabilitiesForHardware().supportsHaptics {
            if hapticEngine == nil {
                initializeEngine()
            }
            resetIdleTimer(after: duration)

            guard let engine = hapticEngine else {
                vibrate()
                return
            }

            do {
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
    }
}
