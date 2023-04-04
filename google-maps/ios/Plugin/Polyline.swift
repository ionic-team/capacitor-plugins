import Foundation
import Capacitor

public struct Polyline {
    let path: [LatLng]
    let strokeWidth: CGFloat
    let strokeColor: UIColor
    let title: String?
    let tappable: Bool?
    let zIndex: Int32
    
    init(fromJSObject: JSObject) throws {
        var strokeColor = UIColor.blue
        var strokeWidth: CGFloat = 1.0
        var path: [LatLng] = []
        
        if let width = fromJSObject["strokeWidth"] as? Float {
            strokeWidth = CGFloat(width)
        }
        
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
        
        self.strokeColor = strokeColor
        self.strokeWidth = strokeWidth
        self.title = fromJSObject["title"] as? String
        self.tappable = fromJSObject["tappable"] as? Bool
        self.zIndex = Int32((fromJSObject["zIndex"] as? Int) ?? 0)
        self.path = path
    }
}
