import Foundation
import UIKit

@objc public class ActionSheet: NSObject {

    @objc public func buildActionSheet(title: String?, message: String?, actions: [UIAlertAction]) -> UIAlertController {
        let controller = UIAlertController(title: title, message: message, preferredStyle: .actionSheet)
        for action in actions {
            controller.addAction(action)
        }
        return controller
    }
}
