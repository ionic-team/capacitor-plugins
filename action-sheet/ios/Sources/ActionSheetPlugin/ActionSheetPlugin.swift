import Foundation
import Capacitor

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(ActionSheetPlugin)
public class ActionSheetPlugin: CAPPlugin, CAPBridgedPlugin {
    public let identifier = "ActionSheetPlugin"
    public let jsName = "ActionSheet"
    public let pluginMethods: [CAPPluginMethod] = [
        CAPPluginMethod(name: "showActions", returnType: CAPPluginReturnPromise)
    ]
    private let implementation = ActionSheet()

    @objc func showActions(_ call: CAPPluginCall) {
        let title = call.options["title"] as? String
        let message = call.options["message"] as? String

        let options = call.getArray("options", JSObject.self) ?? []
        var alertActions = [UIAlertAction]()
        for (index, option) in options.enumerated() {
            let style = option["style"] as? String ?? "DEFAULT"
            let title = option["title"] as? String ?? ""
            var buttonStyle: UIAlertAction.Style = .default
            if style == "DESTRUCTIVE" {
                buttonStyle = .destructive
            } else if style == "CANCEL" {
                buttonStyle = .cancel
            }
            let action = UIAlertAction(title: title, style: buttonStyle, handler: { (_) -> Void in
                call.resolve([
                    "index": index
                ])
            })
            alertActions.append(action)
        }

        DispatchQueue.main.async { [weak self] in
            if let alertController = self?.implementation.buildActionSheet(title: title, message: message, actions: alertActions) {
                self?.setCenteredPopover(alertController)
                self?.bridge?.viewController?.present(alertController, animated: true, completion: nil)
            }
        }
    }

}
