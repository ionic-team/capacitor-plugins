import Foundation
import Capacitor

@objc public class Clipboard: NSObject {

    @objc enum ContentType: Int {
        case string
        case url
        case image
    }

    @objc func write(content: String, ofType type: ContentType) {
        switch type {
        case ContentType.string:
            UIPasteboard.general.string = content
        case ContentType.url:
            if let url = URL(string: content) {
                UIPasteboard.general.url = url
            }
        case ContentType.image:
            if let data = Data.capacitor.data(base64EncodedOrDataUrl: content), let image = UIImage(data: data) {
                CAPLog.print("Loaded image", image.size.width, image.size.height)
                UIPasteboard.general.image = image
            } else {
                CAPLog.print("Unable to encode image")
            }
        default:
            CAPLog.print("Unknown clipboard content type")
        }
    }

    @objc func read() -> [String: Any] {
        if let stringValue = UIPasteboard.general.string {
            return [
                "value": stringValue,
                "type": "text/plain"
            ]
        }

        if let url = UIPasteboard.general.url {
            return [
                "value": url.absoluteString,
                "type": "text/plain"
            ]
        }

        if let image = UIPasteboard.general.image {
            let data = image.pngData()
            if let base64 = data?.base64EncodedString() {
                return [
                    "value": "data:image/png;base64," + base64,
                    "type": "image/png"
                ]
            }
        }

        return [:]
    }
}

private extension String {
    func lastCommaSeparatedValue() -> String {
        if let part = self.split(separator: ",").last {
            return String(part)
        }
        return self
    }
}
