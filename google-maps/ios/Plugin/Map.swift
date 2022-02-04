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
        let frame = CGRect(x: mapViewBounds["x"] ?? 0, y: mapViewBounds["y"]!, width: mapViewBounds["width"] ?? 0, height: mapViewBounds["height"] ?? 0)
        self.GMapView = GMSMapView.map(withFrame: frame, camera: camera)
        self.view = GMapView
    }
}

public class Map {
    var id: String
    var config: GoogleMapConfig
    private var bridge: CAPBridgeProtocol
    private var delegate: GMSMapViewDelegate
    var mapViewController: GMViewController?

    init(id: String, config: GoogleMapConfig, bridge: CAPBridgeProtocol, delegate: GMSMapViewDelegate) {
        self.id = id
        self.config = config
        self.bridge = bridge
        self.delegate = delegate
        
        self.render()
    }
    
    func render() {
        DispatchQueue.main.async {
            self.mapViewController = GMViewController()
            self.mapViewController!.mapViewBounds = [
                "width": self.config.width,
                "height": self.config.height,
                "x": self.config.x,
                "y": self.config.y
            ]
            self.mapViewController!.cameraPosition = [
                "latitude": self.config.center.lat,
                "longitude": self.config.center.lng,
                "zoom": self.config.zoom
            ]
            self.bridge.viewController!.view.addSubview(self.mapViewController!.view)
            self.mapViewController!.GMapView.delegate = self.delegate
        }
    }
    
    func destroy() {
        DispatchQueue.main.async {
            if nil != self.mapViewController {
                self.mapViewController!.view = nil
                self.mapViewController = nil
            }
        }
    }
}
