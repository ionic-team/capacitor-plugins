import UIKit

@objc internal class ToastLabel: UILabel {
    var padding: UIEdgeInsets?

    override open func draw(_ rect: CGRect) {
        if let insets = padding {
            self.drawText(in: rect.inset(by: insets))
        } else {
            self.drawText(in: rect)
        }
    }
}
