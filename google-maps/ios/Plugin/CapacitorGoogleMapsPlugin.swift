// swiftlint:disable file_length
import Foundation
import Capacitor
import GoogleMaps
import GoogleMapsUtils

extension GMSMapViewType {
    static func fromString(mapType: String) -> GMSMapViewType {
        switch mapType {
        case "Normal":
            return .normal
        case "Hybrid":
            return .hybrid
        case "Satellite":
            return .satellite
        case "Terrain":
            return .terrain
        case "None":
            return .none
        default:
            print("CapacitorGoogleMaps Warning: unknown mapView type '\(mapType)'.  Defaulting to normal.")
            return .normal
        }
    }
    static func toString(mapType: GMSMapViewType) -> String {
        switch mapType {
        case .normal:
            return "Normal"
        case .hybrid:
            return "Hybrid"
        case .satellite:
            return "Satellite"
        case .terrain:
            return "Terrain"
        case .none:
            return "None"
        default:
            return "Normal"
        }
    }
}

extension CGRect {
    static func fromJSObject(_ jsObject: JSObject) throws -> CGRect {
        guard let width = jsObject["width"] as? Double else {
            throw GoogleMapErrors.invalidArguments("bounds object is missing the required 'width' property")
        }

        guard let height = jsObject["height"] as? Double else {
            throw GoogleMapErrors.invalidArguments("bounds object is missing the required 'height' property")
        }

        guard let x = jsObject["x"] as? Double else {
            throw GoogleMapErrors.invalidArguments("bounds object is missing the required 'x' property")
        }

        guard let y = jsObject["y"] as? Double else {
            throw GoogleMapErrors.invalidArguments("bounds object is missing the required 'y' property")
        }

        return CGRect(x: x, y: y, width: width, height: height)
    }
}

// swiftlint:disable type_body_length
@objc(CapacitorGoogleMapsPlugin)
public class CapacitorGoogleMapsPlugin: CAPPlugin, GMSMapViewDelegate {
    private var maps = [String: Map]()
    private var isInitialized = false

    func checkLocationPermission() -> String {
        let locationState: String

        switch CLLocationManager.authorizationStatus() {
        case .notDetermined:
            locationState = "prompt"
        case .restricted, .denied:
            locationState = "denied"
        case .authorizedAlways, .authorizedWhenInUse:
            locationState = "granted"
        @unknown default:
            locationState = "prompt"
        }

        return locationState
    }

    @objc func create(_ call: CAPPluginCall) {
        do {
            if !isInitialized {
                guard let apiKey = call.getString("apiKey") else {
                    throw GoogleMapErrors.invalidAPIKey
                }

                GMSServices.provideAPIKey(apiKey)
                isInitialized = true
            }

            guard let id = call.getString("id") else {
                throw GoogleMapErrors.invalidMapId
            }

            guard let configObj = call.getObject("config") else {
                throw GoogleMapErrors.invalidArguments("config object is missing")
            }

            let forceCreate = call.getBool("forceCreate", false)

            let config = try GoogleMapConfig(fromJSObject: configObj)

            if self.maps[id] != nil {
                if !forceCreate {
                    call.resolve()
                    return
                }

                let removedMap = self.maps.removeValue(forKey: id)
                removedMap?.destroy()
            }

            DispatchQueue.main.sync {
                let newMap = Map(id: id, config: config, delegate: self)
                self.maps[id] = newMap
            }

            call.resolve()
        } catch {
            handleError(call, error: error)
        }
    }

    @objc func destroy(_ call: CAPPluginCall) {
        do {
            guard let id = call.getString("id") else {
                throw GoogleMapErrors.invalidMapId
            }

            guard let removedMap = self.maps.removeValue(forKey: id) else {
                throw GoogleMapErrors.mapNotFound
            }

            removedMap.destroy()
            call.resolve()
        } catch {
            handleError(call, error: error)
        }
    }

    @objc func enableTouch(_ call: CAPPluginCall) {
        do {
            guard let id = call.getString("id") else {
                throw GoogleMapErrors.invalidMapId
            }

            guard let map = self.maps[id] else {
                throw GoogleMapErrors.mapNotFound
            }

            map.enableTouch()

            call.resolve()
        } catch {
            handleError(call, error: error)
        }
    }

    @objc func disableTouch(_ call: CAPPluginCall) {
        do {
            guard let id = call.getString("id") else {
                throw GoogleMapErrors.invalidMapId
            }

            guard let map = self.maps[id] else {
                throw GoogleMapErrors.mapNotFound
            }

            map.disableTouch()

            call.resolve()
        } catch {
            handleError(call, error: error)
        }
    }

    @objc func addMarker(_ call: CAPPluginCall) {
        do {
            guard let id = call.getString("id") else {
                throw GoogleMapErrors.invalidMapId
            }

            guard let markerObj = call.getObject("marker") else {
                throw GoogleMapErrors.invalidArguments("marker object is missing")
            }

            let marker = try Marker(fromJSObject: markerObj)

            guard let map = self.maps[id] else {
                throw GoogleMapErrors.mapNotFound
            }

            let markerId = try map.addMarker(marker: marker)

            call.resolve(["id": String(markerId)])

        } catch {
            handleError(call, error: error)
        }
    }

    @objc func addMarkers(_ call: CAPPluginCall) {
        do {
            guard let id = call.getString("id") else {
                throw GoogleMapErrors.invalidMapId
            }

            guard let markerObjs = call.getArray("markers") as? [JSObject] else {
                throw GoogleMapErrors.invalidArguments("markers array is missing")
            }

            if markerObjs.isEmpty {
                throw GoogleMapErrors.invalidArguments("markers requires at least one marker")
            }

            guard let map = self.maps[id] else {
                throw GoogleMapErrors.mapNotFound
            }

            var markers: [Marker] = []

            try markerObjs.forEach { marker in
                let marker = try Marker(fromJSObject: marker)
                markers.append(marker)
            }

            let ids = try map.addMarkers(markers: markers)

            call.resolve(["ids": ids.map({ id in
                return String(id)
            })])

        } catch {
            handleError(call, error: error)
        }
    }

    @objc func removeMarkers(_ call: CAPPluginCall) {
        do {
            guard let id = call.getString("id") else {
                throw GoogleMapErrors.invalidMapId
            }

            guard let markerIdStrings = call.getArray("markerIds") as? [String] else {
                throw GoogleMapErrors.invalidArguments("markerIds are invalid or missing")
            }

            if markerIdStrings.isEmpty {
                throw GoogleMapErrors.invalidArguments("markerIds requires at least one marker id")
            }

            let ids: [Int] = try markerIdStrings.map { idString in
                guard let markerId = Int(idString) else {
                    throw GoogleMapErrors.invalidArguments("markerIds are invalid or missing")
                }

                return markerId
            }

            guard let map = self.maps[id] else {
                throw GoogleMapErrors.mapNotFound
            }

            try map.removeMarkers(ids: ids)

            call.resolve()
        } catch {
            handleError(call, error: error)
        }
    }

    @objc func removeMarker(_ call: CAPPluginCall) {
        do {
            guard let id = call.getString("id") else {
                throw GoogleMapErrors.invalidMapId
            }

            guard let markerIdString = call.getString("markerId") else {
                throw GoogleMapErrors.invalidArguments("markerId is invalid or missing")
            }

            guard let markerId = Int(markerIdString) else {
                throw GoogleMapErrors.invalidArguments("markerId is invalid or missing")
            }

            guard let map = self.maps[id] else {
                throw GoogleMapErrors.mapNotFound
            }

            try map.removeMarker(id: markerId)

            call.resolve()

        } catch {
            handleError(call, error: error)
        }
    }

    @objc func addPolygons(_ call: CAPPluginCall) {
        do {
            guard let id = call.getString("id") else {
                throw GoogleMapErrors.invalidMapId
            }

            guard let shapeObjs = call.getArray("polygons") as? [JSObject] else {
                throw GoogleMapErrors.invalidArguments("polygons array is missing")
            }

            if shapeObjs.isEmpty {
                throw GoogleMapErrors.invalidArguments("polygons requires at least one shape")
            }

            guard let map = self.maps[id] else {
                throw GoogleMapErrors.mapNotFound
            }

            var shapes: [Polygon] = []

            try shapeObjs.forEach { shapeObj in
                let polygon = try Polygon(fromJSObject: shapeObj)
                shapes.append(polygon)
            }

            let ids = try map.addPolygons(polygons: shapes)

            call.resolve(["ids": ids.map({ id in
                return String(id)
            })])
        } catch {
            handleError(call, error: error)
        }
    }

    @objc func addPolylines(_ call: CAPPluginCall) {
        do {
            guard let id = call.getString("id") else {
                throw GoogleMapErrors.invalidMapId
            }

            guard let lineObjs = call.getArray("polylines") as? [JSObject] else {
                throw GoogleMapErrors.invalidArguments("polylines array is missing")
            }

            if lineObjs.isEmpty {
                throw GoogleMapErrors.invalidArguments("polylines requires at least one line")
            }

            guard let map = self.maps[id] else {
                throw GoogleMapErrors.mapNotFound
            }

            var lines: [Polyline] = []

            try lineObjs.forEach { lineObj in
                let line = try Polyline(fromJSObject: lineObj)
                lines.append(line)
            }

            let ids = try map.addPolylines(lines: lines)

            call.resolve(["ids": ids.map({ id in
                return String(id)
            })])
        } catch {
            handleError(call, error: error)
        }
    }

    @objc func removePolygons(_ call: CAPPluginCall) {
        do {
            guard let id = call.getString("id") else {
                throw GoogleMapErrors.invalidMapId
            }

            guard let polygonIdsStrings = call.getArray("polygonIds") as? [String] else {
                throw GoogleMapErrors.invalidArguments("polygonIds are invalid or missing")
            }

            if polygonIdsStrings.isEmpty {
                throw GoogleMapErrors.invalidArguments("polygonIds requires at least one polygon id")
            }

            let ids: [Int] = try polygonIdsStrings.map { idString in
                guard let polygonId = Int(idString) else {
                    throw GoogleMapErrors.invalidArguments("polygonIds are invalid or missing")
                }

                return polygonId
            }

            guard let map = self.maps[id] else {
                throw GoogleMapErrors.mapNotFound
            }

            try map.removePolygons(ids: ids)

            call.resolve()
        } catch {
            handleError(call, error: error)
        }
    }

    @objc func addCircles(_ call: CAPPluginCall) {
        do {
            guard let id = call.getString("id") else {
                throw GoogleMapErrors.invalidMapId
            }

            guard let circleObjs = call.getArray("circles") as? [JSObject] else {
                throw GoogleMapErrors.invalidArguments("circles array is missing")
            }

            if circleObjs.isEmpty {
                throw GoogleMapErrors.invalidArguments("circles requires at least one circle")
            }

            guard let map = self.maps[id] else {
                throw GoogleMapErrors.mapNotFound
            }

            var circles: [Circle] = []

            try circleObjs.forEach { circleObj in
                let circle = try Circle(from: circleObj)
                circles.append(circle)
            }

            let ids = try map.addCircles(circles: circles)

            call.resolve(["ids": ids.map({ id in
                return String(id)
            })])
        } catch {
            handleError(call, error: error)
        }
    }

    @objc func removeCircles(_ call: CAPPluginCall) {
        do {
            guard let id = call.getString("id") else {
                throw GoogleMapErrors.invalidMapId
            }

            guard let circleIdsStrings = call.getArray("circleIds") as? [String] else {
                throw GoogleMapErrors.invalidArguments("circleIds are invalid or missing")
            }

            if circleIdsStrings.isEmpty {
                throw GoogleMapErrors.invalidArguments("circleIds requires at least one cicle id")
            }

            let ids: [Int] = try circleIdsStrings.map { idString in
                guard let circleId = Int(idString) else {
                    throw GoogleMapErrors.invalidArguments("circleIds are invalid or missing")
                }

                return circleId
            }

            guard let map = self.maps[id] else {
                throw GoogleMapErrors.mapNotFound
            }

            try map.removeCircles(ids: ids)

            call.resolve()
        } catch {
            handleError(call, error: error)
        }
    }

    @objc func removePolylines(_ call: CAPPluginCall) {
        do {
            guard let id = call.getString("id") else {
                throw GoogleMapErrors.invalidMapId
            }

            guard let polylineIdsStrings = call.getArray("polylineIds") as? [String] else {
                throw GoogleMapErrors.invalidArguments("polylineIds are invalid or missing")
            }

            if polylineIdsStrings.isEmpty {
                throw GoogleMapErrors.invalidArguments("polylineIds requires at least one polyline id")
            }

            let ids: [Int] = try polylineIdsStrings.map { idString in
                guard let polylineId = Int(idString) else {
                    throw GoogleMapErrors.invalidArguments("polylineIds are invalid or missing")
                }

                return polylineId
            }

            guard let map = self.maps[id] else {
                throw GoogleMapErrors.mapNotFound
            }

            try map.removePolylines(ids: ids)

            call.resolve()
        } catch {
            handleError(call, error: error)
        }
    }

    @objc func setCamera(_ call: CAPPluginCall) {
        do {
            guard let id = call.getString("id") else {
                throw GoogleMapErrors.invalidMapId
            }

            guard let map = self.maps[id] else {
                throw GoogleMapErrors.mapNotFound
            }

            guard let configObj = call.getObject("config") else {
                throw GoogleMapErrors.invalidArguments("config object is missing")
            }

            let config = try GoogleMapCameraConfig(fromJSObject: configObj)

            try map.setCamera(config: config)

            call.resolve()
        } catch {
            handleError(call, error: error)
        }
    }

    @objc func getMapType(_ call: CAPPluginCall) {
        do {
            guard let id = call.getString("id") else {
                throw GoogleMapErrors.invalidMapId
            }

            guard let map = self.maps[id] else {
                throw GoogleMapErrors.mapNotFound
            }

            let mapType = GMSMapViewType.toString(mapType: map.getMapType())

            call.resolve([
                "type": mapType
            ])
        } catch {
            handleError(call, error: error)
        }
    }

    @objc func setMapType(_ call: CAPPluginCall) {
        do {
            guard let id = call.getString("id") else {
                throw GoogleMapErrors.invalidMapId
            }

            guard let map = self.maps[id] else {
                throw GoogleMapErrors.mapNotFound
            }

            guard let mapTypeString = call.getString("mapType") else {
                throw GoogleMapErrors.invalidArguments("mapType is missing")
            }

            let mapType = GMSMapViewType.fromString(mapType: mapTypeString)

            try map.setMapType(mapType: mapType)

            call.resolve()
        } catch {
            handleError(call, error: error)
        }
    }

    @objc func enableIndoorMaps(_ call: CAPPluginCall) {
        do {
            guard let id = call.getString("id") else {
                throw GoogleMapErrors.invalidMapId
            }

            guard let map = self.maps[id] else {
                throw GoogleMapErrors.mapNotFound
            }

            guard let enabled = call.getBool("enabled") else {
                throw GoogleMapErrors.invalidArguments("enabled is missing")
            }

            try map.enableIndoorMaps(enabled: enabled)

            call.resolve()
        } catch {
            handleError(call, error: error)
        }
    }

    @objc func enableTrafficLayer(_ call: CAPPluginCall) {
        do {
            guard let id = call.getString("id") else {
                throw GoogleMapErrors.invalidMapId
            }

            guard let map = self.maps[id] else {
                throw GoogleMapErrors.mapNotFound
            }

            guard let enabled = call.getBool("enabled") else {
                throw GoogleMapErrors.invalidArguments("enabled is missing")
            }

            try map.enableTrafficLayer(enabled: enabled)

            call.resolve()
        } catch {
            handleError(call, error: error)
        }
    }

    @objc func enableAccessibilityElements(_ call: CAPPluginCall) {
        do {
            guard let id = call.getString("id") else {
                throw GoogleMapErrors.invalidMapId
            }

            guard let map = self.maps[id] else {
                throw GoogleMapErrors.mapNotFound
            }

            guard let enabled = call.getBool("enabled") else {
                throw GoogleMapErrors.invalidArguments("enabled is missing")
            }

            try map.enableAccessibilityElements(enabled: enabled)

            call.resolve()
        } catch {
            handleError(call, error: error)
        }
    }

    @objc func setPadding(_ call: CAPPluginCall) {
        do {
            guard let id = call.getString("id") else {
                throw GoogleMapErrors.invalidMapId
            }

            guard let map = self.maps[id] else {
                throw GoogleMapErrors.mapNotFound
            }

            guard let configObj = call.getObject("padding") else {
                throw GoogleMapErrors.invalidArguments("padding is missing")
            }

            let padding = try GoogleMapPadding.init(fromJSObject: configObj)

            try map.setPadding(padding: padding)

            call.resolve()
        } catch {
            handleError(call, error: error)
        }
    }

    @objc func enableCurrentLocation(_ call: CAPPluginCall) {
        do {
            guard let id = call.getString("id") else {
                throw GoogleMapErrors.invalidMapId
            }

            guard let map = self.maps[id] else {
                throw GoogleMapErrors.mapNotFound
            }

            guard let enabled = call.getBool("enabled") else {
                throw GoogleMapErrors.invalidArguments("enabled is missing")
            }

            if enabled && checkLocationPermission() != "granted" {
                throw GoogleMapErrors.permissionsDeniedLocation
            }

            try map.enableCurrentLocation(enabled: enabled)

            call.resolve()
        } catch {
            handleError(call, error: error)
        }
    }

    @objc func enableClustering(_ call: CAPPluginCall) {
        do {
            guard let id = call.getString("id") else {
                throw GoogleMapErrors.invalidMapId
            }

            guard let map = self.maps[id] else {
                throw GoogleMapErrors.mapNotFound
            }

            let minClusterSize = call.getInt("minClusterSize")

            map.enableClustering(minClusterSize)
            call.resolve()

        } catch {
            handleError(call, error: error)
        }
    }

    @objc func disableClustering(_ call: CAPPluginCall) {
        do {
            guard let id = call.getString("id") else {
                throw GoogleMapErrors.invalidMapId
            }

            guard let map = self.maps[id] else {
                throw GoogleMapErrors.mapNotFound
            }

            map.disableClustering()
            call.resolve()
        } catch {
            handleError(call, error: error)
        }
    }

    @objc func onScroll(_ call: CAPPluginCall) {
        call.unavailable("not supported on iOS")
    }

    @objc func onResize(_ call: CAPPluginCall) {
        do {
            guard let id = call.getString("id") else {
                throw GoogleMapErrors.invalidMapId
            }

            guard let map = self.maps[id] else {
                throw GoogleMapErrors.mapNotFound
            }

            guard let mapBoundsObj = call.getObject("mapBounds") else {
                throw GoogleMapErrors.invalidArguments("map bounds not set")
            }

            let mapBounds = try CGRect.fromJSObject(mapBoundsObj)

            map.updateRender(mapBounds: mapBounds)

            call.resolve()
        } catch {
            handleError(call, error: error)
        }
    }

    @objc func onDisplay(_ call: CAPPluginCall) {
        do {
            guard let id = call.getString("id") else {
                throw GoogleMapErrors.invalidMapId
            }

            guard let map = self.maps[id] else {
                throw GoogleMapErrors.mapNotFound
            }

            guard let mapBoundsObj = call.getObject("mapBounds") else {
                throw GoogleMapErrors.invalidArguments("map bounds not set")
            }

            let mapBounds = try CGRect.fromJSObject(mapBoundsObj)

            map.rebindTargetContainer(mapBounds: mapBounds)

            call.resolve()
        } catch {
            handleError(call, error: error)
        }
    }

    @objc func getMapBounds(_ call: CAPPluginCall) {
        do {
            guard let id = call.getString("id") else {
                throw GoogleMapErrors.invalidMapId
            }

            guard let map = self.maps[id] else {
                throw GoogleMapErrors.mapNotFound
            }

            try DispatchQueue.main.sync {
                guard let bounds = map.getMapLatLngBounds() else {
                    throw GoogleMapErrors.unhandledError("Google Map Bounds could not be found.")
                }

                call.resolve(
                    formatMapBoundsForResponse(
                        bounds: bounds,
                        cameraPosition: map.mapViewController.GMapView.camera
                    )
                )
            }
        } catch {
            handleError(call, error: error)
        }
    }

    @objc func mapBoundsContains(_ call: CAPPluginCall) {
        do {
            guard let boundsObject = call.getObject("bounds") else {
                throw GoogleMapErrors.invalidArguments("Invalid bounds provided")
            }

            guard let pointObject = call.getObject("point") else {
                throw GoogleMapErrors.invalidArguments("Invalid point provided")
            }

            let bounds = try getGMSCoordinateBounds(boundsObject)
            let point = try getCLLocationCoordinate(pointObject)

            call.resolve([
                "contains": bounds.contains(point)
            ])
        } catch {
            handleError(call, error: error)
        }
    }

    @objc func fitBounds(_ call: CAPPluginCall) {
        do {
            guard let id = call.getString("id") else {
                throw GoogleMapErrors.invalidMapId
            }

            guard let map = self.maps[id] else {
                throw GoogleMapErrors.mapNotFound
            }

            guard let boundsObject = call.getObject("bounds") else {
                throw GoogleMapErrors.invalidArguments("Invalid bounds provided")
            }

            let bounds = try getGMSCoordinateBounds(boundsObject)
            let padding = CGFloat(call.getInt("padding", 0))

            map.fitBounds(bounds: bounds, padding: padding)
            call.resolve()
        } catch {
            handleError(call, error: error)
        }
    }

    @objc func mapBoundsExtend(_ call: CAPPluginCall) {
        do {
            guard let boundsObject = call.getObject("bounds") else {
                throw GoogleMapErrors.invalidArguments("Invalid bounds provided")
            }

            guard let pointObject = call.getObject("point") else {
                throw GoogleMapErrors.invalidArguments("Invalid point provided")
            }

            let bounds = try getGMSCoordinateBounds(boundsObject)
            let point = try getCLLocationCoordinate(pointObject)

            DispatchQueue.main.sync {
                let newBounds = bounds.includingCoordinate(point)
                call.resolve([
                    "bounds": formatMapBoundsForResponse(newBounds)
                ])
            }
        } catch {
            handleError(call, error: error)
        }
    }

    private func getGMSCoordinateBounds(_ bounds: JSObject) throws -> GMSCoordinateBounds {
        guard let southwest = bounds["southwest"] as? JSObject else {
            throw GoogleMapErrors.unhandledError("Bounds southwest property not formatted properly.")
        }

        guard let northeast = bounds["northeast"] as? JSObject else {
            throw GoogleMapErrors.unhandledError("Bounds northeast property not formatted properly.")
        }

        return GMSCoordinateBounds(
            coordinate: try getCLLocationCoordinate(southwest),
            coordinate: try getCLLocationCoordinate(northeast)
        )
    }

    private func getCLLocationCoordinate(_ point: JSObject) throws -> CLLocationCoordinate2D {
        guard let lat = point["lat"] as? Double else {
            throw GoogleMapErrors.unhandledError("Point lat property not formatted properly.")
        }

        guard let lng = point["lng"] as? Double else {
            throw GoogleMapErrors.unhandledError("Point lng property not formatted properly.")
        }

        return CLLocationCoordinate2D(latitude: lat, longitude: lng)
    }

    private func formatMapBoundsForResponse(bounds: GMSCoordinateBounds?, cameraPosition: GMSCameraPosition) -> PluginCallResultData {
        return [
            "southwest": [
                "lat": bounds?.southWest.latitude,
                "lng": bounds?.southWest.longitude
            ],
            "center": [
                "lat": cameraPosition.target.latitude,
                "lng": cameraPosition.target.longitude
            ],
            "northeast": [
                "lat": bounds?.northEast.latitude,
                "lng": bounds?.northEast.longitude
            ]
        ]
    }

    private func formatMapBoundsForResponse(_ bounds: GMSCoordinateBounds) -> PluginCallResultData {
        let centerLatitude = (bounds.southWest.latitude + bounds.northEast.latitude) / 2.0
        let centerLongitude = (bounds.southWest.longitude + bounds.northEast.longitude) / 2.0

        return [
            "southwest": [
                "lat": bounds.southWest.latitude,
                "lng": bounds.southWest.longitude
            ],
            "center": [
                "lat": centerLatitude,
                "lng": centerLongitude
            ],
            "northeast": [
                "lat": bounds.northEast.latitude,
                "lng": bounds.northEast.longitude
            ]
        ]
    }

    private func handleError(_ call: CAPPluginCall, error: Error) {
        let errObject = getErrorObject(error)
        call.reject(errObject.message, "\(errObject.code)", error, [:])
    }

    private func findMapIdByMapView(_ mapView: GMSMapView) -> String {
        for (mapId, map) in self.maps {
            if map.mapViewController.GMapView === mapView {
                return mapId
            }
        }
        return ""
    }

    // --- EVENT LISTENERS ---

    // onCameraIdle
    public func mapView(_ mapView: GMSMapView, idleAt cameraPosition: GMSCameraPosition) {
        let mapId = self.findMapIdByMapView(mapView)
        let map = self.maps[mapId]
        let bounds = map?.getMapLatLngBounds()

        let data: PluginCallResultData = [
            "mapId": mapId,
            "bounds": formatMapBoundsForResponse(
                bounds: bounds,
                cameraPosition: cameraPosition
            ),
            "bearing": cameraPosition.bearing,
            "latitude": cameraPosition.target.latitude,
            "longitude": cameraPosition.target.longitude,
            "tilt": cameraPosition.viewingAngle,
            "zoom": cameraPosition.zoom
        ]

        self.notifyListeners("onBoundsChanged", data: data)
        self.notifyListeners("onCameraIdle", data: data)
    }

    // onCameraMoveStarted
    public func mapView(_ mapView: GMSMapView, willMove gesture: Bool) {
        self.notifyListeners("onCameraMoveStarted", data: [
            "mapId": self.findMapIdByMapView(mapView),
            "isGesture": gesture
        ])
    }

    // onMapClick
    public func mapView(_ mapView: GMSMapView, didTapAt coordinate: CLLocationCoordinate2D) {
        self.notifyListeners("onMapClick", data: [
            "mapId": self.findMapIdByMapView(mapView),
            "latitude": coordinate.latitude,
            "longitude": coordinate.longitude
        ])
    }

    // onPolygonClick, onPolylineClick, onCircleClick
    public func mapView(_ mapView: GMSMapView, didTap overlay: GMSOverlay) {
        if let polygon = overlay as? GMSPolygon {
            self.notifyListeners("onPolygonClick", data: [
                "mapId": self.findMapIdByMapView(mapView),
                "polygonId": String(overlay.hash.hashValue),
                "tag": polygon.userData as? String
            ])
        }

        if let circle = overlay as? GMSCircle {
            self.notifyListeners("onCircleClick", data: [
                "mapId": self.findMapIdByMapView(mapView),
                "circleId": String(overlay.hash.hashValue),
                "tag": circle.userData as? String,
                "latitude": circle.position.latitude,
                "longitude": circle.position.longitude,
                "radius": circle.radius
            ])
        }

        if let polyline = overlay as? GMSPolyline {
            self.notifyListeners("onPolylineClick", data: [
                "mapId": self.findMapIdByMapView(mapView),
                "polylineId": String(overlay.hash.hashValue),
                "tag": polyline.userData as? String
            ])
        }
    }

    // onClusterClick, onMarkerClick
    public func mapView(_ mapView: GMSMapView, didTap marker: GMSMarker) -> Bool {
        if let cluster = marker.userData as? GMUCluster {
            var items: [[String: Any?]] = []

            for item in cluster.items {
                items.append([
                    "markerId": String(item.hash.hashValue),
                    "latitude": item.position.latitude,
                    "longitude": item.position.longitude,
                    "title": item.title ?? "",
                    "snippet": item.snippet ?? ""
                ])
            }

            self.notifyListeners("onClusterClick", data: [
                "mapId": self.findMapIdByMapView(mapView),
                "latitude": cluster.position.latitude,
                "longitude": cluster.position.longitude,
                "size": cluster.count,
                "items": items
            ])
        } else {
            self.notifyListeners("onMarkerClick", data: [
                "mapId": self.findMapIdByMapView(mapView),
                "markerId": String(marker.hash.hashValue),
                "latitude": marker.position.latitude,
                "longitude": marker.position.longitude,
                "title": marker.title ?? "",
                "snippet": marker.snippet ?? ""
            ])
        }
        return false
    }

    // onMarkerDragStart
    public func mapView(_ mapView: GMSMapView, didBeginDragging marker: GMSMarker) {
        self.notifyListeners("onMarkerDragStart", data: [
            "mapId": self.findMapIdByMapView(mapView),
            "markerId": String(marker.hash.hashValue),
            "latitude": marker.position.latitude,
            "longitude": marker.position.longitude,
            "title": marker.title ?? "",
            "snippet": marker.snippet ?? ""
        ])
    }

    // onMarkerDrag
    public func mapView(_ mapView: GMSMapView, didDrag marker: GMSMarker) {
        self.notifyListeners("onMarkerDrag", data: [
            "mapId": self.findMapIdByMapView(mapView),
            "markerId": String(marker.hash.hashValue),
            "latitude": marker.position.latitude,
            "longitude": marker.position.longitude,
            "title": marker.title ?? "",
            "snippet": marker.snippet ?? ""
        ])
    }

    // onMarkerDragEnd
    public func mapView(_ mapView: GMSMapView, didEndDragging marker: GMSMarker) {
        self.notifyListeners("onMarkerDragEnd", data: [
            "mapId": self.findMapIdByMapView(mapView),
            "markerId": String(marker.hash.hashValue),
            "latitude": marker.position.latitude,
            "longitude": marker.position.longitude,
            "title": marker.title ?? "",
            "snippet": marker.snippet ?? ""
        ])
    }

    // onClusterInfoWindowClick, onInfoWindowClick
    public func mapView(_ mapView: GMSMapView, didTapInfoWindowOf marker: GMSMarker) {
        if let cluster = marker.userData as? GMUCluster {
            var items: [[String: Any?]] = []

            for item in cluster.items {
                items.append([
                    "markerId": String(item.hash.hashValue),
                    "latitude": item.position.latitude,
                    "longitude": item.position.longitude,
                    "title": item.title ?? "",
                    "snippet": item.snippet ?? ""
                ])
            }

            self.notifyListeners("onClusterInfoWindowClick", data: [
                "mapId": self.findMapIdByMapView(mapView),
                "latitude": cluster.position.latitude,
                "longitude": cluster.position.longitude,
                "size": cluster.count,
                "items": items
            ])
        } else {
            self.notifyListeners("onInfoWindowClick", data: [
                "mapId": self.findMapIdByMapView(mapView),
                "markerId": String(marker.hash.hashValue),
                "latitude": marker.position.latitude,
                "longitude": marker.position.longitude,
                "title": marker.title ?? "",
                "snippet": marker.snippet ?? ""
            ])
        }
    }

    // onMyLocationButtonClick
    public func didTapMyLocationButtonForMapView(for mapView: GMSMapView) -> Bool {
        self.notifyListeners("onMyLocationButtonClick", data: [
            "mapId": self.findMapIdByMapView(mapView)
        ])
        return false
    }

    // onMyLocationClick
    public func mapView(_ mapView: GMSMapView, didTapMyLocation location: CLLocationCoordinate2D) {
        self.notifyListeners("onMyLocationButtonClick", data: [
            "mapId": self.findMapIdByMapView(mapView),
            "latitude": location.latitude,
            "longitude": location.longitude
        ])
    }
}

// snippet from https://www.hackingwithswift.com/example-code/uicolor/how-to-convert-a-hex-color-to-a-uicolor
extension UIColor {
    public convenience init?(hex: String) {
        let r, g, b, a: CGFloat

        if hex.hasPrefix("#") {
            let start = hex.index(hex.startIndex, offsetBy: 1)
            let hexColor = String(hex[start...])

            let scanner = Scanner(string: hexColor)
            var hexNumber: UInt64 = 0
            if hexColor.count == 8 {
                if scanner.scanHexInt64(&hexNumber) {
                    r = CGFloat((hexNumber & 0xff000000) >> 24) / 255
                    g = CGFloat((hexNumber & 0x00ff0000) >> 16) / 255
                    b = CGFloat((hexNumber & 0x0000ff00) >> 8) / 255
                    a = CGFloat(hexNumber & 0x000000ff) / 255

                    self.init(red: r, green: g, blue: b, alpha: a)
                    return
                }
            } else {
                if scanner.scanHexInt64(&hexNumber) {
                    r = CGFloat((hexNumber & 0xff0000) >> 16) / 255
                    g = CGFloat((hexNumber & 0x00ff00) >> 8) / 255
                    b = CGFloat((hexNumber & 0x0000ff) >> 0) / 255

                    self.init(red: r, green: g, blue: b, alpha: 1)
                    return
                }
            }
        }

        return nil
    }
}
