import Foundation
import Capacitor

public struct Marker {
    let coordinate: LatLng
    let opacity: Float?
    let title: String?
    let snippet: String?
    let isFlat: Bool?
    let iconUrl: String?
    let iconSize: CGSize?
    let iconAnchor: CGPoint?
    let draggable: Bool?

    init(fromJSObject: JSObject) throws {
        guard let latLngObj = fromJSObject["coordinate"] as? JSObject else {
            throw GoogleMapErrors.invalidArguments("Marker object is missing the required 'coordinate' property")
        }

        guard let lat = latLngObj["lat"] as? Double, let lng = latLngObj["lng"] as? Double else {
            throw GoogleMapErrors.invalidArguments("LatLng object is missing the required 'lat' and/or 'lng' property")
        }
        
        var iconSize: CGSize? = nil
        
        if let sizeObj = fromJSObject["iconSize"] as? JSObject {
            if let width = sizeObj["width"] as? Double, let height = sizeObj["height"] as? Double {
                iconSize = CGSize(width: width, height: height)
            }
        }
        
        var iconAnchor: CGPoint? = nil
        if let anchorObject = fromJSObject["iconAnchor"] as? JSObject {
            if let x = anchorObject["x"] as? Double, let y = anchorObject["y"] as? Double {
                if let size = iconSize {
                    let u = x / size.width
                    let v = y / size.height
                    
                    iconAnchor = CGPoint(x: u, y: v)
                }
            }
        }        

        self.coordinate = LatLng(lat: lat, lng: lng)
        self.opacity = fromJSObject["opacity"] as? Float
        self.title = fromJSObject["title"] as? String
        self.snippet = fromJSObject["snippet"] as? String
        self.isFlat = fromJSObject["isFlat"] as? Bool
        self.iconUrl = fromJSObject["iconUrl"] as? String
        self.draggable = fromJSObject["draggable"] as? Bool
        self.iconSize = iconSize
        self.iconAnchor = iconAnchor
    }
}
