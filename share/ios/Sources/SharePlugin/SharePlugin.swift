import Foundation
import Capacitor

@objc(SharePlugin)
public class SharePlugin: CAPPlugin, CAPBridgedPlugin {
    public let identifier = "SharePlugin"
    public let jsName = "Share"
    public let pluginMethods: [CAPPluginMethod] = [
        CAPPluginMethod(name: "canShare", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "share", returnType: CAPPluginReturnPromise)
    ]

    @objc func canShare(_ call: CAPPluginCall) {
        call.resolve([
            "value": true
        ])
    }

    @objc func share(_ call: CAPPluginCall) {
        var items = [Any]()

        if let text = call.getString("text") {
            items.append(text)
        }

        if let url = call.getString("url"), let urlObj = URL(string: url) {
            items.append(urlObj)
        }

        let title = call.getString("title")

        if let files = call.getArray("files") {
            files.forEach { file in
                if let url = file as? String, let fileUrl = URL(string: url) {
                    items.append(fileUrl)
                }
            }
        }

        if items.count == 0 {
            call.reject("Must provide at least url, text or files")
            return
        }

        DispatchQueue.main.async { [weak self] in
            let actionController = UIActivityViewController(activityItems: items, applicationActivities: nil)

            if title != nil {
                actionController.setValue(title, forKey: "subject")
            }

            actionController.completionWithItemsHandler = { (activityType, completed, _ returnedItems, activityError) in
                if activityError != nil {
                    call.reject("Error sharing item", nil, activityError)
                    return
                }

                if completed {
                    call.resolve([
                        "activityType": activityType?.rawValue ?? ""
                    ])
                } else {
                    call.reject("Share canceled")
                }

            }
            // Present from the topmost presented view controller so the share sheet appears
            // above any view controller the app has presented over the webview. With nothing
            // presented this resolves to the bridge view controller, i.e. unchanged behaviour.
            // A share is only in progress when a share sheet is already presented.
            var presenter = self?.bridge?.viewController
            while let presented = presenter?.presentedViewController {
                if presented is UIActivityViewController {
                    call.reject("Can't share while sharing is in progress")
                    return
                }
                presenter = presented
            }
            // `setCenteredPopover` anchors to the bridge view controller's view, which is not in
            // the presenter's hierarchy when presenting from a different view controller. Anchor
            // the popover to the presenting view controller's own view instead, centered and
            // without an arrow, matching `setCenteredPopover` behaviour.
            if let popover = actionController.popoverPresentationController, let presenterView = presenter?.view {
                popover.sourceView = presenterView
                popover.sourceRect = CGRect(x: presenterView.bounds.midX, y: presenterView.bounds.midY, width: 0, height: 0)
                popover.permittedArrowDirections = []
            }
            presenter?.present(actionController, animated: true, completion: nil)
        }
    }
}
