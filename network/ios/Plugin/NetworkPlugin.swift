import Foundation
import Capacitor

@objc(CAPNetworkPlugin)
public class NetworkPlugin: CAPPlugin {
    private var implementation: Network?

    override public func load() {
        CAPLog.print("Loading network plugin")
        do {
            implementation = try Network()
            implementation?.statusObserver = { [weak self] status in
                CAPLog.print(status.logMessage)
                self?.notifyListeners("networkStatusChange", data: [
                    "connected": status.isConnected,
                    "connectionType": status.jsStringValue
                ])
            }
        } catch let error {
            CAPLog.print("Unable to start network monitor: \(error)")
        }
    }

    @objc func getStatus(_ call: CAPPluginCall) {
        let status = implementation?.currentStatus() ?? Reachability.Connection.none
        call.resolve(["connected": status.isConnected, "connectionType": status.jsStringValue])
    }
}

extension Reachability.Connection {
    internal var jsStringValue: String {
        switch self {
        case .cellular:
            return "cellular"
        case .wifi:
            return "wifi"
        case .none:
            return ""
        }
    }
    internal var isConnected: Bool {
        switch self {
        case .cellular, .wifi:
            return true
        case .none:
            return false
        }
    }
    internal var logMessage: String {
        switch self {
        case .cellular:
            return "Reachable via Cellular"
        case .wifi:
            return "Reachable via WiFi"
        case .none:
            return "Not reachable"
        }
    }
}
