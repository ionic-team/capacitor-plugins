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
        let title = call.getString("title")
        let message = call.getString("message")
        let cancelable = call.getBool("cancelable", false)

        let options = call.getArray("options", JSObject.self) ?? []
        var alertActions = [UIAlertAction]()
        var forceCancelableOnClickOutside = cancelable
        for (index, option) in options.enumerated() {
            let style = option["style"] as? String ?? "DEFAULT"
            let title = option["title"] as? String ?? ""
            var buttonStyle: UIAlertAction.Style = .default
            if style == "DESTRUCTIVE" {
                buttonStyle = .destructive
            } else if style == "CANCEL" {
                // if there's a cancel action, then it will already be cancelable when clicked outside
                forceCancelableOnClickOutside = false
                buttonStyle = .cancel
            }
            let action = UIAlertAction(title: title, style: buttonStyle, handler: { (_) -> Void in
                call.resolve([
                    "index": index,
                    "canceled": false
                ])
            })
            alertActions.append(action)
        }

        DispatchQueue.main.async { [weak self] in
            if let alertController = self?.implementation.buildActionSheet(title: title, message: message, actions: alertActions) {
                self?.setCenteredPopover(alertController)
                self?.bridge?.viewController?.present(alertController, animated: true) {
                    if (forceCancelableOnClickOutside) {
                        let gestureRecognizer = TapGestureRecognizerWithClosure {
                            alertController.dismiss(animated: true, completion: nil)
                            call.resolve([
                                "index": -1,
                                "canceled": true
                            ])
                        }
                        let backroundView = alertController.view.superview?.subviews[0]
                        backroundView?.addGestureRecognizer(gestureRecognizer)
                    }
                }
            }
        }
    }
    
    private final class TapGestureRecognizerWithClosure: UITapGestureRecognizer {
        private let onTap: () -> Void

        init(onTap: @escaping () -> Void) {
            self.onTap = onTap
            super.init(target: nil, action: nil)
            self.addTarget(self, action: #selector(action))
        }

        @objc private func action() {
            onTap()
        }
    }
}
