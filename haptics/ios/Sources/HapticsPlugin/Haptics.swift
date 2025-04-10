import AudioToolbox
import UIKit
import CoreHaptics

@objc public class Haptics: NSObject {

    var selectionFeedbackGenerator: UISelectionFeedbackGenerator?

    // Haptic Engine Management
    private var hapticEngine: CHHapticEngine?
    private var idleTimer: Timer?
    private let idleInterval: TimeInterval = 5.0 // Engine will shut down after 5 seconds of inactivity

    /**
    * Initializes the haptic engine and starts it.
    */
    private func initializeEngine() {
        if CHHapticEngine.capabilitiesForHardware().supportsHaptics {
            do {
                hapticEngine = try CHHapticEngine()
                try hapticEngine?.start()
            } catch {
                print("Error initializing haptic engine: \(error)")
                hapticEngine = nil
            }
        }
    }

    /**
    * Reset the idle timer each time a haptic event is fired.
    */
    private func resetIdleTimer() {
        idleTimer?.invalidate()
        idleTimer = Timer.scheduledTimer(withTimeInterval: idleInterval, repeats: false) { [weak self] _ in
            self?.shutdownEngine()
        }
    }

    /**
    * Shuts down the haptic engine if it is not in use.
    */
    private func shutdownEngine() {
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
            // Reuse the existing engine or initialize a new one if needed.
            if hapticEngine == nil {
                initializeEngine()
            }
            // Reset the idle timer so the engine remains alive.
            resetIdleTimer()
            
            guard let engine = hapticEngine else {
                vibrate()  // fallback if the engine couldn’t be created
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
