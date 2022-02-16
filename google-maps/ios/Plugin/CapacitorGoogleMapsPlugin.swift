import Foundation
import Capacitor
import GoogleMaps

@objc(CapacitorGoogleMapsPlugin)
public class CapacitorGoogleMapsPlugin: CAPPlugin, GMSMapViewDelegate {
    private var maps = [String: Map]()
    private var isInitialized = false

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
                throw GoogleMapErrors.invalidArguments("GoogleMapConfig is missing")
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

            let newMap = Map(id: id, config: config, delegate: self)
            self.maps[id] = newMap
            
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

    @objc func addMarker(_ call: CAPPluginCall) {
        do {
            guard let id = call.getString("id") else {
                throw GoogleMapErrors.invalidMapId
            }

            guard let markerObj = call.getObject("marker") else {
                throw GoogleMapErrors.invalidArguments("Marker object is missing")
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
                throw GoogleMapErrors.invalidArguments("Markers array is missing")
            }
            
            if markerObjs.isEmpty {
                throw GoogleMapErrors.invalidArguments("Markers requires at least one marker")
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
                throw GoogleMapErrors.invalidArguments("Marker hash id is invalid or missing")
            }

            guard let markerId = Int(markerIdString) else {
                throw GoogleMapErrors.invalidArguments("Marker hash id is invalid or missing")
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
    
    @objc func enableClustering(_ call: CAPPluginCall) {
        do {
            guard let id = call.getString("id") else {
                throw GoogleMapErrors.invalidMapId
            }
            
            guard let map = self.maps[id] else {
                throw GoogleMapErrors.mapNotFound
            }
            
            map.enableClustering()
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

    private func handleError(_ call: CAPPluginCall, error: Error) {
        let errObject = getErrorObject(error)
        call.reject(errObject.message, "\(errObject.code)", error, [:])
    }

}
