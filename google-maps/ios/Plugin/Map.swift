import Foundation
import GoogleMaps
import Capacitor

public struct LatLng: Codable {
    let lat: Double
    let lng: Double
}

class GMViewController: UIViewController {
    var mapViewBounds: [String: Double]!
    var GMapView: GMSMapView!
    var cameraPosition: [String: Double]!

    override func viewDidLoad() {
        super.viewDidLoad()
        let camera = GMSCameraPosition.camera(withLatitude: cameraPosition["latitude"] ?? 0, longitude: cameraPosition["longitude"] ?? 0, zoom: Float(cameraPosition["zoom"] ?? 12))
        let frame = CGRect(x: mapViewBounds["x"] ?? 0, y: mapViewBounds["y"] ?? 0, width: mapViewBounds["width"] ?? 0, height: mapViewBounds["height"] ?? 0)
        self.GMapView = GMSMapView.map(withFrame: frame, camera: camera)
        self.view = GMapView
    }
}

public class Map {
    var id: String
    var config: GoogleMapConfig
    var mapViewController: GMViewController?
    var markers = [Int: GMSMarker]()
    private var delegate: CapacitorGoogleMapsPlugin
    

    init(id: String, config: GoogleMapConfig, delegate: CapacitorGoogleMapsPlugin) {
        self.id = id
        self.config = config
        self.delegate = delegate
        
        self.render()
    }
    
    func render() {
        DispatchQueue.main.async {
            self.mapViewController = GMViewController()
            
            if let mapViewController = self.mapViewController {
                mapViewController.mapViewBounds = [
                    "width": self.config.width,
                    "height": self.config.height,
                    "x": self.config.x,
                    "y": self.config.y
                ]
                mapViewController.cameraPosition = [
                    "latitude": self.config.center.lat,
                    "longitude": self.config.center.lng,
                    "zoom": self.config.zoom
                ]
                
                if let bridge = self.delegate.bridge {
                    bridge.viewController!.view.addSubview(mapViewController.view)
                    mapViewController.GMapView.delegate = self.delegate
                }
            }
        }
    }
    
    func destroy() {
        DispatchQueue.main.async {
            if let mapViewController = self.mapViewController {
                mapViewController.view = nil
                self.mapViewController = nil
            }
        }
    }
    
    func addMarker(marker: Marker) throws -> Int {
        guard let mapViewController = mapViewController else {
            throw GoogleMapErrors.unhandledError("map view controller not available")
        }

        var markerHash = 0
        
        DispatchQueue.main.sync {
            let newMarker = GMSMarker()
            newMarker.position = CLLocationCoordinate2D(latitude: marker.coordinate.lat, longitude: marker.coordinate.lng)
            newMarker.title = marker.title
            newMarker.snippet = marker.snippet
            newMarker.isFlat = marker.isFlat ?? false
            newMarker.opacity = marker.opacity ?? 1
            newMarker.isDraggable = marker.draggable ?? false
        
            newMarker.map = mapViewController.GMapView
            self.markers[newMarker.hash.hashValue] = newMarker
            
            markerHash = newMarker.hash.hashValue
        }
        
        return markerHash
    }
    
    func removeMarker(id: Int) {
        DispatchQueue.main.async {
            if let marker = self.markers[id] {
                marker.map = nil
                self.markers.removeValue(forKey: id)
            }
        }
    }
}
