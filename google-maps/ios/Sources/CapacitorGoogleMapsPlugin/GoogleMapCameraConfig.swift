import Foundation
import Capacitor

public struct GoogleMapCameraConfig {
    let coordinate: LatLng?
    let zoom: Float?
    let bearing: Double?
    let angle: Double?
    let animate: Bool?
    let animationDuration: Double?

    init(fromJSObject: JSObject) throws {
        zoom = fromJSObject["zoom"] as? Float
        bearing = fromJSObject["bearing"] as? Double
        angle = fromJSObject["angle"] as? Double
        animate = fromJSObject["animate"] as? Bool
        animationDuration = fromJSObject["animationDuration"] as? Double

        if let latLngObj = fromJSObject["coordinate"] as? JSObject {
            guard let lat = latLngObj["lat"] as? Double, let lng = latLngObj["lng"] as? Double else {
                throw GoogleMapErrors.invalidArguments("LatLng object is missing the required 'lat' and/or 'lng' property")
            }

            self.coordinate = LatLng(lat: lat, lng: lng)
        } else {
            self.coordinate = nil
        }
    }
}
