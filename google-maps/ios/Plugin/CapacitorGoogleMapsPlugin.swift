import Foundation
import Capacitor
import GoogleMaps

@objc(CapacitorGoogleMapsPlugin)
public class CapacitorGoogleMapsPlugin: CAPPlugin, GMSMapViewDelegate {
    private var maps = [String: Map]()

    @objc func create(_ call: CAPPluginCall) {
        do {
            guard let id = call.getString("id") else {
                throw GoogleMapErrors.invalidMapId
            }

            guard let apiKey = call.getString("apiKey") else {
                throw GoogleMapErrors.invalidAPIKey
            }

            guard let configObj = call.getObject("config") else {
                throw GoogleMapErrors.invalidArguments("GoogleMapConfig is missing")
            }

            let forceCreate = call.getBool("forceCreate", false)

            GMSServices.provideAPIKey(apiKey)
            let config = try GoogleMapConfig(fromJSObject: configObj)

            if self.maps[id] != nil {
                if !forceCreate {
                    call.resolve()
                    return
                }

                self.maps.removeValue(forKey: id)
            }

            let newMap = Map(config: config)
            self.maps[id] = newMap
            renderMap(newMap)

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

            let removedMap = self.maps.removeValue(forKey: id)
            if removedMap == nil {
                throw GoogleMapErrors.mapNotFound
            }

            self.destroyMapInView(removedMap!)
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
                throw GoogleMapErrors.invalidArguments("Marker is missing")
            }
            
            let marker = try Marker(fromJSObject: markerObj)
            
            guard let map = self.maps[id] else {
                throw GoogleMapErrors.mapNotFound
            }
            
            let markerId = map.addMarker(marker: marker)
            
            call.resolve(["id": String(markerId)])
            
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
            
            map.removeMarker(id: markerId)
            
            call.resolve()            
            
        } catch {
            handleError(call, error: error)
        }
    }

    private func handleError(_ call: CAPPluginCall, error: Error) {
        let errObject = getErrorObject(error)
        call.reject(errObject.message, "\(errObject.code)", error, [:])
    }

    private func renderMap(_ map: Map) {
        DispatchQueue.main.async {
            map.setupView()
            self.bridge?.viewController?.view.addSubview(map.mapViewController.view)
            map.mapViewController.GMapView.delegate = self
        }
    }

    private func destroyMapInView(_ map: Map) {
        DispatchQueue.main.async {
            map.mapViewController.view = nil
        }
    }
}
