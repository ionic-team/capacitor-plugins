import Foundation
import Capacitor

public class Clipboard: NSObject {

    enum ContentType: Int {
        case string
        case url
        case image
    }

    struct ClipboardError: Error {
        private let message: String

        var localizedDescription: String {
            return message
        }

        init(_ message: String) {
            self.message = message
        }
    }

    func write(content: String, ofType type: ContentType) -> Result<Void, Error> {
        switch type {
        case ContentType.string:
            UIPasteboard.general.string = content
            return .success(())
        case ContentType.url:
            if let url = URL(string: content) {
                UIPasteboard.general.url = url
                return .success(())
            } else {
                let errMsg = "Unable to form URL"
                CAPLog.print(errMsg)
                return .failure(ClipboardError(errMsg))
            }
        case ContentType.image:
            if let data = Data.capacitor.data(base64EncodedOrDataUrl: content), let image = UIImage(data: data) {
                CAPLog.print("Loaded image", image.size.width, image.size.height)
                UIPasteboard.general.image = image
                return .success(())
            } else {
                let errMsg = "Unable to encode image"
                CAPLog.print(errMsg)
                return .failure(ClipboardError(errMsg))
            }
        }
    }

    func read() -> [String: Any] {
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
