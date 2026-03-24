import Foundation
import Capacitor

public class Clipboard {

    enum ContentType: Int {
        case string
        case url
        case image
    }

    public enum ClipboardError: LocalizedError {
        case invalidURL, invalidImage

        public var errorDescription: String? {
            switch self {
            case .invalidURL:
                return "Unable to form URL"
            case .invalidImage:
                return "Unable to encode image"
            }
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
                return .failure(ClipboardError.invalidURL)
            }
        case ContentType.image:
            if let data = Data.capacitor.data(base64EncodedOrDataUrl: content), let image = UIImage(data: data) {
                CAPLog.print("Loaded image", image.size.width, image.size.height)
                UIPasteboard.general.image = image
                return .success(())
            } else {
                return .failure(ClipboardError.invalidImage)
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
