import Foundation
import Capacitor

public struct Marker {
    let coordinate: LatLng
    let opacity: Float?
    let title: String?
    let snippet: String?
    let isFlat: Bool?
    let iconUrl: String?
    let draggable: Bool?

    init(fromJSObject: JSObject) throws {
        guard let latLngObj = fromJSObject["coordinate"] as? JSObject else {
            throw GoogleMapErrors.invalidArguments("Marker object is missing the required 'coordinate' property")
        }

        guard let lat = latLngObj["lat"] as? Double, let lng = latLngObj["lng"] as? Double else {
            throw GoogleMapErrors.invalidArguments("LatLng object is missing the required 'lat' and/or 'lng' property")
        }

        self.coordinate = LatLng(lat: lat, lng: lng)

        self.opacity = fromJSObject["opacity"] as? Float
        self.title = fromJSObject["title"] as? String
        self.snippet = fromJSObject["snippet"] as? String
        self.isFlat = fromJSObject["isFlat"] as? Bool
        self.iconUrl = fromJSObject["iconUrl"] as? String
        self.draggable = fromJSObject["draggable"] as? Bool
    }
}
