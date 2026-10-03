import Foundation
import Capacitor

@objc(CAPNetworkPlugin)
public class NetworkPlugin: CAPPlugin, CAPBridgedPlugin {
    public let identifier = "CAPNetworkPlugin"
    public let jsName = "Network"
    public let pluginMethods: [CAPPluginMethod] = [
        CAPPluginMethod(name: "getStatus", returnType: CAPPluginReturnPromise)
    ]
    private var implementation: Network?

    override public func load() {
        CAPLog.print("Loading network plugin")
        do {
            implementation = try Network()
            implementation?.statusObserver = { [weak self] status in
                CAPLog.print(status.logMessage)
                self?.notifyListeners("networkStatusChange", data: self?.statusData(status) ?? [:])
            }
        } catch let error {
            CAPLog.print("Unable to start network monitor: \(error)")
        }
    }

    @objc func getStatus(_ call: CAPPluginCall) {
        let status = implementation?.currentStatus() ?? Network.Connection.unavailable
        call.resolve(statusData(status))
    }

    private func statusData(_ status: Network.Connection) -> [String: Any] {
        let details = status.isConnected ? (implementation?.connectionDetails ?? Network.ConnectionDetails()) : Network.ConnectionDetails()
        return [
            "connected": status.isConnected,
            "connectionType": status.jsStringValue,
            "constrained": details.constrained,
            "expensive": details.expensive
        ]
    }
}

extension Network.Connection {
    internal var jsStringValue: String {
        switch self {
        case .cellular:
            return "cellular"
        case .wifi:
            return "wifi"
        case .unavailable:
            return "none"
        }
    }
    internal var isConnected: Bool {
        switch self {
        case .cellular, .wifi:
            return true
        case .unavailable:
            return false
        }
    }
    internal var logMessage: String {
        switch self {
        case .cellular:
            return "Reachable via Cellular"
        case .wifi:
            return "Reachable via WiFi"
        case .unavailable:
            return "Not reachable"
        }
    }
}
