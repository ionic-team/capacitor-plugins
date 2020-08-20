import Foundation
import UIKit

@objc public class TextZoom: NSObject {
    static var baseFontSize: Double = 17

    @objc public func preferredFontSize() -> Double {
        return Double(UIFont.preferredFont(forTextStyle: .body).pointSize) / TextZoom.baseFontSize
    }
}
