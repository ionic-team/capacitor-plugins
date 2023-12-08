import Foundation
import Capacitor

@objc(ClipboardPlugin)
public class ClipboardPlugin: CAPPlugin, CAPBridgedPlugin {
    public let identifier = "ClipboardPlugin"
    public let jsName = "Clipboard"
    public let pluginMethods: [CAPPluginMethod] = [
        CAPPluginMethod(name: "read", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "write", returnType: CAPPluginReturnPromise)
    ]
    private let implementation = Clipboard()

    @objc func read(_ call: CAPPluginCall) {
        let result = implementation.read()

        if !result.isEmpty {
            call.resolve(result)
        } else {
            call.reject("There is no data on the clipboard")
        }
    }

    @objc func write(_ call: CAPPluginCall) {
        var result: Result<Void, Error>

        if let string = call.options["string"] as? String {
            result = implementation.write(content: string, ofType: Clipboard.ContentType.string)
        } else if let urlString = call.options["url"] as? String {
            result = implementation.write(content: urlString, ofType: Clipboard.ContentType.url)
        } else if let imageBase64 = call.options["image"] as? String {
            result = implementation.write(content: imageBase64, ofType: Clipboard.ContentType.image)
        } else {
            call.reject("No content provided")
            return
        }

        switch result {
        case .success:
            call.resolve()
        case .failure(let err):
            CAPLog.print(err.localizedDescription)
            call.reject(err.localizedDescription)
        }
    }
}
