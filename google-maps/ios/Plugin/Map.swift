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
            clusterManager.cluster()
        }
    }
}

public class Map {
    var id: String
    var config: GoogleMapConfig
    var mapViewController: GMViewController
    var markers = [Int: GMSMarker]()
    private var delegate: CapacitorGoogleMapsPlugin

    init(id: String, config: GoogleMapConfig, delegate: CapacitorGoogleMapsPlugin) {
        self.id = id
        self.config = config
        self.delegate = delegate
        self.mapViewController = GMViewController()

        self.render()
    }

    func render() {
        DispatchQueue.main.async {
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
            if let bridge = self.delegate.bridge {
                bridge.viewController!.view.addSubview(self.mapViewController.view)
                self.mapViewController.GMapView.delegate = self.delegate
            }
        }
    }
    func destroy() {
        DispatchQueue.main.async {
            self.mapViewController.view = nil
        }
    }

    func addMarker(marker: Marker) throws -> Int {
        var markerHash = 0

        DispatchQueue.main.sync {
            let newMarker = GMSMarker()
            newMarker.position = CLLocationCoordinate2D(latitude: marker.coordinate.lat, longitude: marker.coordinate.lng)
            newMarker.title = marker.title
            newMarker.snippet = marker.snippet
            newMarker.isFlat = marker.isFlat ?? false
            newMarker.opacity = marker.opacity ?? 1
            newMarker.isDraggable = marker.draggable ?? false

            if self.mapViewController.clusteringEnabled {
                self.mapViewController.addMarkersToCluster(markers: [newMarker])
            } else {
                newMarker.map = self.mapViewController.GMapView
            }

            self.markers[newMarker.hash.hashValue] = newMarker

            markerHash = newMarker.hash.hashValue
        }

        return markerHash
    }

    func addMarkers(markers: [Marker]) throws -> [Int] {
        var markerHashes: [Int] = []

        DispatchQueue.main.sync {
            var googleMapsMarkers: [GMSMarker] = []

            markers.forEach { marker in
                let newMarker = GMSMarker()
                newMarker.position = CLLocationCoordinate2D(latitude: marker.coordinate.lat, longitude: marker.coordinate.lng)
                newMarker.title = marker.title
                newMarker.snippet = marker.snippet
                newMarker.isFlat = marker.isFlat ?? false
                newMarker.opacity = marker.opacity ?? 1
                newMarker.isDraggable = marker.draggable ?? false

                if self.mapViewController.clusteringEnabled {
                    googleMapsMarkers.append(newMarker)
                } else {
                    newMarker.map = self.mapViewController.GMapView
                }

                self.markers[newMarker.hash.hashValue] = newMarker

                markerHashes.append(newMarker.hash.hashValue)
            }

            if self.mapViewController.clusteringEnabled {
                self.mapViewController.addMarkersToCluster(markers: googleMapsMarkers)
            }
        }

        return markerHashes
    }

    func enableClustering() {
        if !self.mapViewController.clusteringEnabled {
            DispatchQueue.main.sync {
                self.mapViewController.initClusterManager()

                // add existing markers to the cluster
                if !self.markers.isEmpty {
                    var existingMarkers: [GMSMarker] = []
                    for (_, marker) in self.markers {
                        marker.map = nil
                        existingMarkers.append(marker)
                    }

                    self.mapViewController.addMarkersToCluster(markers: existingMarkers)
                }
            }
        }
    }

    func disableClustering() {
        DispatchQueue.main.sync {
            self.mapViewController.destroyClusterManager()

            // add existing markers back to the map
            if !self.markers.isEmpty {
                for (_, marker) in self.markers {
                    marker.map = self.mapViewController.GMapView
                }
            }
        }
    }

    func removeMarker(id: Int) throws {
        if let marker = self.markers[id] {
            DispatchQueue.main.async {
                if self.mapViewController.clusteringEnabled {
                    self.mapViewController.removeMarkersFromCluster(markers: [marker])
                }

                marker.map = nil
                self.markers.removeValue(forKey: id)

            }
        } else {
            throw GoogleMapErrors.markerNotFound
        }
    }

    func setCamera(config: GoogleMapCameraConfig) throws {
        let currentCamera = self.mapViewController.GMapView.camera

        let lat = config.coordinate?.lat ?? currentCamera.target.latitude
        let lng = config.coordinate?.lng ?? currentCamera.target.longitude

        let zoom = config.zoom ?? currentCamera.zoom
        let bearing = config.bearing ?? Double(currentCamera.bearing)
        let angle = config.angle ?? currentCamera.viewingAngle

        let animate = config.animate ?? false

        DispatchQueue.main.sync {
            let newCamera = GMSCameraPosition(latitude: lat, longitude: lng, zoom: zoom, bearing: bearing, viewingAngle: angle)

            if animate {
                self.mapViewController.GMapView.animate(to: newCamera)
            } else {
                self.mapViewController.GMapView.camera = newCamera
            }
        }

    }

    func setMapType(mapType: GMSMapViewType) throws {
        DispatchQueue.main.sync {
            self.mapViewController.GMapView.mapType = mapType
        }
    }

    func enableIndoorMaps(enabled: Bool) throws {
        DispatchQueue.main.sync {
            self.mapViewController.GMapView.isIndoorEnabled = enabled
        }
    }

    func enableTrafficLayer(enabled: Bool) throws {
        DispatchQueue.main.sync {
            self.mapViewController.GMapView.isTrafficEnabled = enabled
        }
    }

    func enableAccessibilityElements(enabled: Bool) throws {
        DispatchQueue.main.sync {
            self.mapViewController.GMapView.accessibilityElementsHidden = enabled
        }
    }

    func enableCurrentLocation(enabled: Bool) throws {
        DispatchQueue.main.sync {
            self.mapViewController.GMapView.isMyLocationEnabled = enabled
        }
    }

    func setPadding(padding: GoogleMapPadding) throws {
        DispatchQueue.main.sync {
            let mapInsets = UIEdgeInsets(top: CGFloat(padding.top), left: CGFloat(padding.left), bottom: CGFloat(padding.bottom), right: CGFloat(padding.right))
            self.mapViewController.GMapView.padding = mapInsets
        }
    }

    func removeMarkers(ids: [Int]) throws {
        DispatchQueue.main.sync {
            var markers: [GMSMarker] = []
            ids.forEach { id in
                if let marker = self.markers[id] {
                    marker.map = nil
                    self.markers.removeValue(forKey: id)
                    markers.append(marker)
                }
            }

            if self.mapViewController.clusteringEnabled {
                self.mapViewController.removeMarkersFromCluster(markers: markers)
            }
        }
    }
}
