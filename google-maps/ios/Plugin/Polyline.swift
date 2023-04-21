import Foundation
import Capacitor

public struct StyleSpan {
    let color: UIColor
    let segments: Double?
}

public struct Polyline {
    let path: [LatLng]
    let strokeWidth: CGFloat
    let strokeColor: UIColor
    let title: String?
    let tappable: Bool?
    let geodesic: Bool?
    let zIndex: Int32
    let tag: String?
    let styleSpans: [StyleSpan]

    init(fromJSObject: JSObject) throws {
        var strokeColor = UIColor.blue
        var strokeWidth: CGFloat = 1.0
        var path: [LatLng] = []
        var styleSpans: [StyleSpan] = []

        if let width = fromJSObject["strokeWeight"] as? Float {
            strokeWidth = CGFloat(width)
        }

        let strokeOpacity = fromJSObject["strokeOpacity"] as? Double

        if let hexColor = fromJSObject["strokeColor"] as? String {
            strokeColor = UIColor(hex: hexColor) ?? UIColor.blue
        }

        strokeColor = strokeColor.withAlphaComponent(strokeOpacity ?? 1.0)

        guard let pathJSArray = fromJSObject["path"] as? JSArray else {
            throw GoogleMapErrors.invalidArguments("Polyline object is missing the required 'path' property")
        }

        try pathJSArray.forEach { obj in
            guard let jsCoord = obj as? JSObject else {
                throw GoogleMapErrors.invalidArguments("LatLng object is missing the required 'lat' and/or 'lng' property")
            }

            guard let lat = jsCoord["lat"] as? Double, let lng = jsCoord["lng"] as? Double else {
                throw GoogleMapErrors.invalidArguments("LatLng object is missing the required 'lat' and/or 'lng' property")
            }

            path.append(LatLng(lat: lat, lng: lng))
        }

        if let styleSpanJSArray = fromJSObject["styleSpans"] as? JSArray {
            styleSpanJSArray.forEach({ obj in
                if let styleSpanObj = obj as? JSObject,
                   let hexColor = styleSpanObj["color"] as? String,
                   let color = UIColor(hex: hexColor) {
                    let segments = styleSpanObj["segments"] as? Double
                    styleSpans.append(StyleSpan(color: color, segments: segments))
                }
            })
        }

        self.strokeColor = strokeColor
        self.strokeWidth = strokeWidth
        self.tag = fromJSObject["tag"] as? String
        self.title = fromJSObject["title"] as? String
        self.tappable = fromJSObject["clickable"] as? Bool
        self.geodesic = fromJSObject["geodesic"] as? Bool
        self.zIndex = Int32((fromJSObject["zIndex"] as? Int) ?? 0)
        self.path = path
        self.styleSpans = styleSpans
    }
}
