import Foundation
import Capacitor

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(ActionSheetPlugin)
public class ActionSheetPlugin: CAPPlugin, CAPBridgedPlugin, UIAdaptivePresentationControllerDelegate {
    public let identifier = "ActionSheetPlugin"
    public let jsName = "ActionSheet"
    public let pluginMethods: [CAPPluginMethod] = [
        CAPPluginMethod(name: "showActions", returnType: CAPPluginReturnPromise)
    ]
    private let implementation = ActionSheet()
    private var currentCall: CAPPluginCall?

    @objc func showActions(_ call: CAPPluginCall) {
        let title = call.options["title"] as? String
        let message = call.options["message"] as? String

        let options = call.getArray("options", JSObject.self) ?? []
        var alertActions = [UIAlertAction]()
        var hasCancellableButton = false
        for (index, option) in options.enumerated() {
            let style = option["style"] as? String ?? "DEFAULT"
            let title = option["title"] as? String ?? ""
            var buttonStyle: UIAlertAction.Style = .default
            if style == "DESTRUCTIVE" {
                buttonStyle = .destructive
            } else if style == "CANCEL" {
                hasCancellableButton = true
                buttonStyle = .cancel
            }
            let action = UIAlertAction(title: title, style: buttonStyle, handler: { [weak self] (_) in
                if buttonStyle == .cancel {
                    call.actionSheetCanceled()
                } else {
                    call.resolve([
                        "index": index,
                        "canceled": false
                    ])
                }
                self?.currentCall = nil
            })
            alertActions.append(action)
        }

        DispatchQueue.main.async { [weak self] in
            if let alertController = self?.implementation.buildActionSheet(title: title, message: message, actions: alertActions) {
                self?.setCenteredPopover(alertController)
                self?.bridge?.viewController?.present(alertController, animated: true) {
                    if !hasCancellableButton {
                        self?.setupCancelationListerners(alertController, call)
                    }
                }
            }
        }
    }

    private func setupCancelationListerners(_ alertController: UIAlertController, _ call: CAPPluginCall) {
        if #available(iOS 26, *) {
            self.currentCall = call
            alertController.presentationController?.delegate = self
        } else {
            // For iOS versions below 26, setting the presentation controller delegate would result in a crash
            //  "Terminating app due to uncaught exception 'NSInternalInconsistencyException', reason: 'The presentation controller of an alert controller presenting as an alert must not have its delegate modified"
            //  Hence, the alternative by adding a gesture recognizer (which only works for iOS versions below 26)
            let gestureRecognizer = TapGestureRecognizerWithClosure {
                alertController.dismiss(animated: true, completion: nil)
                call.actionSheetCanceled()
            }
            let backroundView = alertController.view.superview?.subviews[0]
            backroundView?.addGestureRecognizer(gestureRecognizer)
        }
    }

    // MARK: - UIAdaptivePresentationControllerDelegate

    public func presentationControllerDidDismiss(_ presentationController: UIPresentationController) {
        self.currentCall?.actionSheetCanceled()
        self.currentCall = nil
    }
}

// MARK: - TapGestureRecognizerWithClosure
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

private extension CAPPluginCall {
    func actionSheetCanceled() {
        resolve([
            "index": -1,
            "canceled": true
        ])
    }
}
