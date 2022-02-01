import Foundation
import Capacitor
import GoogleMaps

@objc(CapacitorGoogleMapsPlugin)
public class CapacitorGoogleMapsPlugin: CAPPlugin {
    private var maps = [String: Map]()

    @objc func create(_ call: CAPPluginCall) {
        do {
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

                self.maps.removeValue(forKey: id)
            }

            let newMap = Map(config: config)
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

            let removedMap = self.maps.removeValue(forKey: id)
            if removedMap == nil {
                throw GoogleMapErrors.mapNotFound
            }

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
            if nil != map.mapViewController {
            
            }
        /*
         if self.mapViewController != nil {
                         self.mapViewController.view = nil
                     }

                     self.mapViewController = GMViewController();
                     self.mapViewController.mapViewBounds = [
                         "width": call.getDouble("width") ?? 500,
                         "height": call.getDouble("height") ?? 500,
                         "x": call.getDouble("x") ?? 0,
                         "y": call.getDouble("y") ?? 0,
                     ]
                     self.mapViewController.cameraPosition = [
                         "latitude": call.getDouble("latitude") ?? 0.0,
                         "longitude": call.getDouble("longitude") ?? 0.0,
                         "zoom": call.getDouble("zoom") ?? (self.DEFAULT_ZOOM)
                     ]
                     self.bridge?.viewController?.view.addSubview(self.mapViewController.view)
                     self.mapViewController.GMapView.delegate = self
                     self.notifyListeners("onMapReady", data: nil)
         */
        }
    }
}
