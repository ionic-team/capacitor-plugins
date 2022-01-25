import Foundation
import Capacitor

public struct GoogleMapConfig: Codable {
    let width: Int
    let height: Int
    let x: Int
    let y: Int
    let center: LatLng
    let zoom: Int

    init(fromJSObject: JSObject) throws {
        guard let width = fromJSObject["width"] as? Int else {
            throw GoogleMapErrors.invalidArguments("GoogleMapConfig object is missing the required 'width' property")
        }

        guard let height = fromJSObject["height"] as? Int else {
            throw GoogleMapErrors.invalidArguments("GoogleMapConfig object is missing the required 'height' property")
        }

        guard let x = fromJSObject["x"] as? Int else {
            throw GoogleMapErrors.invalidArguments("GoogleMapConfig object is missing the required 'x' property")
        }

        guard let y = fromJSObject["y"] as? Int else {
            throw GoogleMapErrors.invalidArguments("GoogleMapConfig object is missing the required 'y' property")
        }

        guard let zoom = fromJSObject["zoom"] as? Int else {
            throw GoogleMapErrors.invalidArguments("GoogleMapConfig object is missing the required 'zoom' property")
        }

        guard let latLngObj = fromJSObject["center"] as? JSObject else {
            throw GoogleMapErrors.invalidArguments("GoogleMapConfig object is missing the required 'center' property")
        }

        guard let lat = latLngObj["lat"] as? Float, let lng = latLngObj["lng"] as? Float else {
            throw GoogleMapErrors.invalidArguments("LatLng object is missing the required 'lat' and/or 'lng' property")
        }

        self.width = width
        self.height = height
        self.x = x
        self.y = y
        self.zoom = zoom
        self.center = LatLng(lat: lat, lng: lng)
    }
}
