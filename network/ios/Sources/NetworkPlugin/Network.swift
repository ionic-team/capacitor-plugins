import Foundation
import Network

public typealias NetworkConnectionChangedObserver = (Network.Connection) -> Void

public class Network {
    public enum NetworkError: Error {
        case initializationFailed
    }
    public enum Connection {
        case unavailable, wifi, cellular
    }
    struct ConnectionDetails {
        var constrained = false
        var expensive = false
    }

    internal private(set) var reachability: Reachability?
    private let pathMonitor = NWPathMonitor()
    private let pathMonitorQueue = DispatchQueue(label: "capacitor.network.pathMonitor")
    private let connectionDetailsQueue = DispatchQueue(label: "capacitor.network.connectionDetails")
    private let observerQueue = DispatchQueue(label: "capacitor.network.observer")
    private var details = ConnectionDetails()
    private var observerNotificationPending = false
    var statusObserver: NetworkConnectionChangedObserver?
    var connectionDetails: ConnectionDetails {
        return connectionDetailsQueue.sync { details }
    }

    init() throws {
        reachability = try Reachability()
        if reachability == nil {
            throw NetworkError.initializationFailed
        }
        // setup our callback(s) and start notifications
        reachability?.whenReachable = { [weak self] _ in
            self?.notifyStatusObserver()
        }
        reachability?.whenUnreachable = { [weak self] _ in
            self?.notifyStatusObserver()
        }
        pathMonitor.pathUpdateHandler = { [weak self] path in
            if self?.updateConnectionDetails(path) == true {
                self?.notifyStatusObserver()
            }
        }
        pathMonitor.start(queue: pathMonitorQueue)
        _ = updateConnectionDetails(pathMonitor.currentPath)
        try reachability?.startNotifier()
    }

    deinit {
        pathMonitor.cancel()
    }

    func currentStatus() -> Network.Connection {
        return reachability?.connection.equivalentEnum ?? Connection.unavailable
    }

    private func updateConnectionDetails(_ path: NWPath) -> Bool {
        let nextDetails = ConnectionDetails(constrained: path.isConstrained, expensive: path.isExpensive)
        return connectionDetailsQueue.sync {
            guard details.constrained != nextDetails.constrained || details.expensive != nextDetails.expensive else {
                return false
            }
            details = nextDetails
            return true
        }
    }

    private func notifyStatusObserver() {
        var shouldNotify = false
        observerQueue.sync {
            if !observerNotificationPending {
                observerNotificationPending = true
                shouldNotify = true
            }
        }

        guard shouldNotify else {
            return
        }

        DispatchQueue.main.async { [weak self] in
            guard let self = self else {
                return
            }
            self.observerQueue.sync {
                self.observerNotificationPending = false
            }
            self.statusObserver?(self.currentStatus())
        }
    }
}

fileprivate extension Reachability.Connection {
    var equivalentEnum: Network.Connection {
        switch self {
        case .unavailable:
            return .unavailable
        case .wifi:
            return .wifi
        case .cellular:
            return .cellular
        }
    }
}
