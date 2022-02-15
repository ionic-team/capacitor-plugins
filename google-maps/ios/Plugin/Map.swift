import Foundation
import GoogleMaps
import Capacitor
import GoogleMapsUtils

public struct LatLng: Codable {
    let lat: Double
    let lng: Double
}

class GMViewController: UIViewController {
    var mapViewBounds: [String: Double]!
    var GMapView: GMSMapView!
    var cameraPosition: [String: Double]!

    private var clusterManager: GMUClusterManager?
    
    var clusteringEnabled: Bool {
        return clusterManager != nil
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        
        let camera = GMSCameraPosition.camera(withLatitude: cameraPosition["latitude"] ?? 0, longitude: cameraPosition["longitude"] ?? 0, zoom: Float(cameraPosition["zoom"] ?? 12))
        let frame = CGRect(x: mapViewBounds["x"] ?? 0, y: mapViewBounds["y"] ?? 0, width: mapViewBounds["width"] ?? 0, height: mapViewBounds["height"] ?? 0)
        self.GMapView = GMSMapView.map(withFrame: frame, camera: camera)
        self.view = GMapView
    }
    
    
    
    func initClusterManager() {
        let iconGenerator = GMUDefaultClusterIconGenerator()
        let algorithm = GMUNonHierarchicalDistanceBasedAlgorithm()
        let renderer = GMUDefaultClusterRenderer(mapView: self.GMapView, clusterIconGenerator: iconGenerator)
        
        self.clusterManager = GMUClusterManager(map: self.GMapView, algorithm: algorithm, renderer: renderer)
    }
    
    func destroyClusterManager() {
        self.clusterManager = nil
    }
    
    func addMarkersToCluster(markers: [GMSMarker]) {
        if let clusterManager = clusterManager {
            clusterManager.add(markers)
            clusterManager.cluster()
        }
    }
    
    func removeMarkersFromCluster(markers: [GMSMarker]) {
        if let clusterManager = clusterManager {
            markers.forEach { marker in
                clusterManager.remove(marker)
            }
        }
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
            
            if mapViewController.clusteringEnabled {
                mapViewController.addMarkersToCluster(markers: [newMarker])
            } else {
                newMarker.map = mapViewController.GMapView
            }
            
            self.markers[newMarker.hash.hashValue] = newMarker

            markerHash = newMarker.hash.hashValue
        }

        return markerHash
    }
    
    func enableClustering() {
        if let mapViewController = mapViewController {
            if !mapViewController.clusteringEnabled {
                DispatchQueue.main.sync {
                    mapViewController.initClusterManager()
                    
                    // add existing markers to the cluster
                    if !self.markers.isEmpty {
                        var existingMarkers: [GMSMarker] = []
                        for (_, marker) in self.markers {
                            marker.map = nil
                            existingMarkers.append(marker)
                        }
                        
                        mapViewController.addMarkersToCluster(markers: existingMarkers)
                    }
                }
            }
        }
    }
    
    func disableClustering() {
        if let mapViewController = mapViewController {
            DispatchQueue.main.sync {
                mapViewController.destroyClusterManager()
                
                // add existing markers back to the map
                if !self.markers.isEmpty {
                    for (_, marker) in self.markers {
                        marker.map = mapViewController.GMapView
                    }
                }
            }
        }
    }

    func removeMarker(id: Int) throws {
        if let marker = self.markers[id] {
            DispatchQueue.main.async {
                if let mapViewController = self.mapViewController {
                    if mapViewController.clusteringEnabled {
                        mapViewController.removeMarkersFromCluster(markers: [marker])
                    }
                }
                
                marker.map = nil
                self.markers.removeValue(forKey: id)
                
                
            }
        } else {
            throw GoogleMapErrors.markerNotFound
        }
    }
}
