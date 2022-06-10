import Foundation

public typealias NetworkConnectionChangedObserver = (Network.Connection) -> Void

public class Network {
    public enum NetworkError: Error {
        case initializationFailed
    }
    public enum Connection {
        case unavailable, wifi, cellular
    }

    internal private(set) var reachability: Reachability?
    var statusObserver: NetworkConnectionChangedObserver?

    init() throws {
        reachability = try Reachability()
        if reachability == nil {
            throw NetworkError.initializationFailed
        }
        // setup our callback(s) and start notifications
        reachability?.whenReachable = { [weak self] reachable in
            self?.statusObserver?(reachable.connection.equivalentEnum)
        }
        reachability?.whenUnreachable = { [weak self] _ in
            self?.statusObserver?(Connection.unavailable)
        }
        try reachability?.startNotifier()
    }

    func currentStatus() -> Network.Connection {
        return reachability?.connection.equivalentEnum ?? Connection.unavailable
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
