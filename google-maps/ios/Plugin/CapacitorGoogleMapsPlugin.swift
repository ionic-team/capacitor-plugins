import Foundation
import Capacitor

@objc(CapacitorGoogleMapsPlugin)
public class CapacitorGoogleMapsPlugin: CAPPlugin {
    private var maps = Dictionary<String, Map>()
    
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
}
