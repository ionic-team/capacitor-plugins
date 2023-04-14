import Foundation
import Capacitor

public struct Circle {
    let center: LatLng
    let radius: Double
    let strokeWidth: CGFloat
    let strokeColor: UIColor
    let fillColor: UIColor
    let tappable: Bool?
    let title: String?
    let zIndex: Int32
    let tag: String?

    init(fromJSObject: JSObject) throws {
        var strokeColor = UIColor.blue
        var strokeWidth: CGFloat = 1.0
        var fillColor = UIColor.blue

        guard let centerLatLng = fromJSObject["center"] as? JSObject else {
            throw GoogleMapErrors.invalidArguments("Circle object is missing the required 'center' property")
        }

        guard let lat = centerLatLng["lat"] as? Double, let lng = centerLatLng["lng"] as? Double else {
            throw GoogleMapErrors.invalidArguments("LatLng object is missing the required 'lat' and/or 'lng' property")
        }

        guard let radius = fromJSObject["radius"] as? Double else {
            throw GoogleMapErrors.invalidArguments("Circle object is missing the required 'radius' property")
        }

        if let width = fromJSObject["strokeWeight"] as? Float {
            strokeWidth = CGFloat(width)
        }

        let strokeOpacity = fromJSObject["strokeOpacity"] as? Double

        if let hexColor = fromJSObject["strokeColor"] as? String {
            strokeColor = UIColor(hex: hexColor) ?? UIColor.blue
        }

        strokeColor = strokeColor.withAlphaComponent(strokeOpacity ?? 1.0)

        let fillOpacity = fromJSObject["fillOpacity"] as? Double

        if let hexColor = fromJSObject["fillColor"] as? String {
            fillColor = UIColor(hex: hexColor) ?? UIColor.blue
        }

        fillColor = fillColor.withAlphaComponent(fillOpacity ?? 1.0)

        self.center = LatLng(lat: lat, lng: lng)
        self.radius = radius
        self.fillColor = fillColor
        self.strokeColor = strokeColor
        self.strokeWidth = strokeWidth
        self.tag = fromJSObject["tag"] as? String
        self.tappable = fromJSObject["clickable"] as? Bool
        self.title = fromJSObject["title"] as? String
        self.zIndex = Int32((fromJSObject["zIndex"] as? Int) ?? 0)
    }
}
