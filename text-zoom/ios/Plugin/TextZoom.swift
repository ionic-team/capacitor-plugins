import Foundation

@objc public class TextZoom: NSObject {
    static var baseFontSize: Double = 17

    @objc public func getPreferred() -> Double {
        return Double(UIFont.preferredFont(forTextStyle: .body).pointSize) / TextZoom.baseFontSize
    }
}
