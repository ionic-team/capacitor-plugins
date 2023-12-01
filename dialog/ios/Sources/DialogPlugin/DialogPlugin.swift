import Foundation
import Capacitor

/**
 * Implement three common dialog types: alert, confirm, and prompt
 */
@objc(DialogPlugin)
public class DialogPlugin: CAPPlugin, CAPBridgedPlugin {
    public let identifier = "DialogPlugin"
    public let jsName = "Dialog"
    public let pluginMethods: [CAPPluginMethod] = [
        CAPPluginMethod(name: "alert", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "prompt", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "confirm", returnType: CAPPluginReturnPromise)
    ]

    @objc public func alert(_ call: CAPPluginCall) {
        let title = call.options["title"] as? String
        guard let message = call.options["message"] as? String else {
            call.reject("Please provide a message for the dialog")
            return
        }
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
        let title = call.options["title"] as? String
        guard let message = call.options["message"] as? String else {
            call.reject("Please provide a message for the dialog")
            return
        }
        let okButtonTitle = call.options["okButtonTitle"] as? String ?? "OK"
        let cancelButtonTitle = call.options["cancelButtonTitle"] as? String ?? "Cancel"

        DispatchQueue.main.async { [weak self] in
            let alert = UIAlertController(title: title, message: message, preferredStyle: UIAlertController.Style.alert)
            alert.addAction(UIAlertAction(title: cancelButtonTitle, style: UIAlertAction.Style.default, handler: { (_) -> Void in
                call.resolve([
                    "value": false
                ])
            }))
            alert.addAction(UIAlertAction(title: okButtonTitle, style: UIAlertAction.Style.default, handler: { (_) -> Void in
                call.resolve([
                    "value": true
                ])
            }))
            self?.bridge?.viewController?.present(alert, animated: true, completion: nil)
        }
    }

    @objc public func prompt (_ call: CAPPluginCall) {
        let title = call.options["title"] as? String
        guard let message = call.options["message"] as? String else {
            call.reject("Please provide a message for the dialog")
            return
        }
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

            alert.addAction(UIAlertAction(title: cancelButtonTitle, style: UIAlertAction.Style.default, handler: { (_) -> Void in
                call.resolve([
                    "value": "",
                    "cancelled": true
                ])
            }))
            alert.addAction(UIAlertAction(title: okButtonTitle, style: UIAlertAction.Style.default, handler: { (_) -> Void in
                let textField = alert.textFields?[0]
                call.resolve([
                    "value": textField?.text ?? "",
                    "cancelled": false
                ])
            }))

            self?.bridge?.viewController?.present(alert, animated: true, completion: nil)
        }
    }
}
