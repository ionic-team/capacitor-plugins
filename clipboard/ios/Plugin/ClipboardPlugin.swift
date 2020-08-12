import Foundation
import Capacitor

@objc(ClipboardPlugin)
public class ClipboardPlugin: CAPPlugin {
    private let implementation = Clipboard()

    @objc func read(_ call: CAPPluginCall) {
        let result = implementation.read()
        
        if !result.isEmpty {
            call.success(result)
        }
    }
    
    @objc func write(_ call: CAPPluginCall) {
        var success : Bool = false
        
        if let string = call.options["string"] as? String {
            implementation.write(Clipboard.ContentType.string, string)
            success = true
        } else if let urlString = call.options["url"] as? String {
            implementation.write(Clipboard.ContentType.url, urlString)
            success = true
        } else if let imageBase64 = call.options["image"] as? String {
            implementation.write(Clipboard.ContentType.image, imageBase64)
            success = true
        }
        
        if success {
            call.success()
        } else {
            call.error("No data provided")
        }
    }
}
