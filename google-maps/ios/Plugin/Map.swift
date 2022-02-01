import Foundation
import GoogleMaps

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
        let frame = CGRect(x: mapViewBounds["x"] ?? 0, y: mapViewBounds["y"]!, width: mapViewBounds["width"] ?? 0, height: mapViewBounds["height"] ?? 0)
        self.GMapView = GMSMapView.map(withFrame: frame, camera: camera)
        self.view = GMapView
    }
}

public class Map {
    var config: GoogleMapConfig
    var mapViewController: GMViewController
    var markers = [Int: GMSMarker]()

    init(config: GoogleMapConfig) {
        self.config = config
        self.mapViewController = GMViewController()
    }

    func setupView() {
        self.mapViewController = GMViewController()
        self.mapViewController.mapViewBounds = [
            "width": self.config.width,
            "height": self.config.height,
            "x": self.config.x,
            "y": self.config.y
        ]
        self.mapViewController.cameraPosition = [
            "latitude": self.config.center.lat,
            "longitude": self.config.center.lng,
            "zoom": self.config.zoom
        ]
    }
    
    func addMarker(marker: Marker) -> Int {
        let newMarker = GMSMarker()
        newMarker.position = CLLocationCoordinate2D(latitude: marker.coordinate.lat, longitude: marker.coordinate.lng)
        newMarker.title = marker.title
        newMarker.snippet = marker.snippet
        newMarker.isFlat = marker.isFlat ?? false
        newMarker.opacity = marker.opacity ?? 1
        newMarker.isDraggable = marker.draggable ?? false
        
        DispatchQueue.main.async {
            newMarker.map = self.mapViewController.GMapView
            self.markers[newMarker.hash.hashValue] = newMarker
        }
        
        return newMarker.hash.hashValue
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
