import Foundation
import Capacitor

@objc(ClipboardPlugin)
public class ClipboardPlugin: CAPPlugin {
    private let implementation = Clipboard()

    @objc func read(_ call: CAPPluginCall) {
        let result = implementation.read()
        
        if (!result.isEmpty) {
            call.success(result)
        }
    }
    
    @objc func write(_ call: CAPPluginCall) {
        if let string = call.options["string"] as? String {
            implementation.write(Clipboard.ContentType.string, string)
        } else if let urlString = call.options["url"] as? String {
            implementation.write(Clipboard.ContentType.url, urlString)
        } else if let imageBase64 = call.options["image"] as? String {
            implementation.write(Clipboard.ContentType.string, imageBase64)
        }
        
        call.success()
    }
}
