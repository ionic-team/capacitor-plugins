import Foundation
import Capacitor

/**
 * Implement three common dialog types: alert, confirm, and prompt
 */
@objc(DialogPlugin)
public class DialogPlugin: CAPPlugin {

    @objc public func alert(_ call: CAPPluginCall) {
        guard let title = call.options["title"] as? String else {
            call.reject("title must be provided")
            return
        }
        let message = call.options["message"] as? String
        let buttonTitle = call.options["buttonTitle"] as? String ?? "OK"

        DispatchQueue.main.async { [weak self] in
            let alert = UIAlertController(title: title, message: message, preferredStyle: UIAlertController.Style.alert)
            alert.addAction(UIAlertAction(title: buttonTitle, style: UIAlertAction.Style.default, handler: { (_) -> Void in
                call.resolve()
            }))
            self?.bridge?.viewController?.present(alert, animated: true, completion: nil)
        }
    }

    @objc public func confirm(_ call: CAPPluginCall) {
        guard let title = call.options["title"] as? String else {
            call.reject("title must be provided")
            return
        }
        let message = call.options["message"] as? String ?? ""
        let okButtonTitle = call.options["okButtonTitle"] as? String ?? "OK"
        let cancelButtonTitle = call.options["cancelButtonTitle"] as? String ?? "Cancel"

        DispatchQueue.main.async { [weak self] in
            let alert = UIAlertController(title: title, message: message, preferredStyle: UIAlertController.Style.alert)
            alert.addAction(UIAlertAction(title: okButtonTitle, style: UIAlertAction.Style.default, handler: { (_) -> Void in
                call.resolve([
                    "value": true
                ])
            }))
            alert.addAction(UIAlertAction(title: cancelButtonTitle, style: UIAlertAction.Style.default, handler: { (_) -> Void in
                call.resolve([
                    "value": false
                ])
            }))
            self?.bridge?.viewController?.present(alert, animated: true, completion: nil)
        }
    }

    @objc public func prompt (_ call: CAPPluginCall) {
        guard let title = call.options["title"] as? String else {
            call.reject("title must be provided")
            return
        }
        let message = call.options["message"] as? String ?? ""
        let okButtonTitle = call.options["okButtonTitle"] as? String ?? "OK"
        let cancelButtonTitle = call.options["cancelButtonTitle"] as? String ?? "Cancel"
        let inputPlaceholder = call.options["inputPlaceholder"] as? String ?? ""
        let inputText = call.options["inputText"] as? String ?? ""

        DispatchQueue.main.async { [weak self] in
            let alert = UIAlertController(title: title, message: message, preferredStyle: UIAlertController.Style.alert)

            alert.addTextField { (textField) in
                textField.placeholder = inputPlaceholder
                textField.text = inputText
            }

            alert.addAction(UIAlertAction(title: okButtonTitle, style: UIAlertAction.Style.default, handler: { (_) -> Void in
                let textField = alert.textFields?[0]
                call.resolve([
                    "value": textField?.text ?? "",
                    "cancelled": false
                ])
            }))
            alert.addAction(UIAlertAction(title: cancelButtonTitle, style: UIAlertAction.Style.default, handler: { (_) -> Void in
                call.resolve([
                    "value": "",
                    "cancelled": true
                ])
            }))

            self?.bridge?.viewController?.present(alert, animated: true, completion: nil)
        }
    }
}
