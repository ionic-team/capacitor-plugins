import Foundation
import GoogleMaps

public struct LatLng: Codable {
    let lat: Double
    let lng: Double
}

class GMViewController: UIViewController {

    var mapViewBounds: [String : Double]!
    var GMapView: GMSMapView!
    var cameraPosition: [String: Double]!

    var DEFAULT_ZOOM: Float = 12

    override func viewDidLoad() {
        super.viewDidLoad()
        let camera = GMSCameraPosition.camera(withLatitude: cameraPosition["latitude"] ?? 0, longitude: cameraPosition["longitude"] ?? 0, zoom: Float(cameraPosition["zoom"] ?? Double(DEFAULT_ZOOM)))
        let frame = CGRect(x: mapViewBounds["x"] ?? 0, y: mapViewBounds["y"]!, width: mapViewBounds["width"] ?? 0, height: mapViewBounds["height"] ?? 0)
        self.GMapView = GMSMapView.map(withFrame: frame, camera: camera)
        self.view = GMapView
    }
}

public class Map {
    var config: GoogleMapConfig
    var mapViewController: GMViewController
    
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
}
