import Foundation
import Reachability

public typealias NetworkConnectionChangedObserver = (Reachability.Connection) -> Void

public class Network {
    public enum NetworkError: Error {
        case initializationFailed
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
            self?.statusObserver?(reachable.connection)
        }
        reachability?.whenUnreachable = { [weak self] _ in
            self?.statusObserver?(Reachability.Connection.unavailable)
        }
        try reachability?.startNotifier()
    }

    func currentStatus() -> Reachability.Connection {
        return reachability?.connection ?? Reachability.Connection.unavailable
    }
}
