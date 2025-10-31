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
            if self?.bridge?.viewController?.presentedViewController != nil {
                call.reject("Can't share while sharing is in progress")
                return
            }
            self?.setCenteredPopover(actionController)
            self?.bridge?.viewController?.present(actionController, animated: true, completion: nil)
        }
    }
}
