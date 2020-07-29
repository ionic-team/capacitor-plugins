import Foundation

@objc public class ScreenReader: NSObject {
    @objc public func speak(_ value: String) {
        if UIAccessibility.isVoiceOverRunning {
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) {
                UIAccessibility.post(notification: .announcement, argument: value)
            }
        }
    }
}
