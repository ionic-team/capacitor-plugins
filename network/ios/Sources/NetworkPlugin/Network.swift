public typealias NetworkStatusChangeObserver = (NetworkStatus) -> Void

public struct NetworkStatus {
    let connection: Network.Connection
    let internetReachable: Bool
    let state: String
    var connected: Bool { connection.isConnected }
    var connectionType: String { connection.jsStringValue }
}

public class Network {
    public enum NetworkError: Error {
        case initializationFailed
    }

    public enum Connection {
        case unavailable, wifi, cellular
    }
    
    internal private(set) var reachability: Reachability?
    var statusObserver: NetworkStatusChangeObserver?
    
    private let probeURLs: [URL] = [
        URL(string: "https://www.google.com/generate_204")!,
        URL(string: "https://captive.apple.com")!,
        URL(string: "https://one.one.one.one")!,
        URL(string: "https://www.msftconnecttest.com/connecttest.txt")!
    ]
    
    private lazy var probeSession: URLSession = {
        let config = URLSessionConfiguration.ephemeral
        config.timeoutIntervalForRequest = 5
        config.timeoutIntervalForResource = 5
        config.waitsForConnectivity = false
        config.requestCachePolicy = .reloadIgnoringLocalAndRemoteCacheData
        return URLSession(configuration: config)
    }()
    
    private var lastEmitted: NetworkStatus?
    
    init() throws {
        reachability = try Reachability()
        if reachability == nil {
            throw NetworkError.initializationFailed
        }
        // setup our callback(s) and start notifications
        reachability?.whenReachable = { [weak self] reachable in
            self?.emitStatus(for: reachable.connection.equivalentEnum)
        }
        reachability?.whenUnreachable = { [weak self] _ in
            self?.emitImmediate(.unavailable, internetReachable: false)
        }
        try reachability?.startNotifier()
    }
    
    func getStatus(completion: @escaping (NetworkStatus) -> Void) {
        let connection = currentConnection()
        if !connection.isConnected {
            completion(buildStatus(connection: connection, internetReachable: false))
            return
        }

        probeInternet { [weak self] reachable in
            guard let self = self else { return }
            completion(self.buildStatus(connection: connection, internetReachable: reachable))
        }
    }
    
    func currentConnection() -> Connection {
        reachability?.connection.equivalentEnum ?? .unavailable
    }
    
    private func emitStatus(for connection: Connection) {
        if !connection.isConnected {
            emitImmediate(connection, internetReachable: false)
            return
        }

        probeInternet { [weak self] reachable in
            guard let self = self else { return }
            self.emitIfChanged(self.buildStatus(connection: connection, internetReachable: reachable))
        }
    }
    
    private func emitImmediate(_ connection: Connection, internetReachable: Bool) {
        emitIfChanged(buildStatus(connection: connection, internetReachable: internetReachable))
    }
    
    private func emitIfChanged(_ status: NetworkStatus) {
        guard status.connection != lastEmitted?.connection
            || status.internetReachable != lastEmitted?.internetReachable
            || status.state != lastEmitted?.state else { return }
        lastEmitted = status
        statusObserver?(status)
    }
    
    private func probeInternet(completion: @escaping (Bool) -> Void) {
        let group = DispatchGroup()
        var reachable = false
        let lock = NSLock()
        for url in probeURLs {
            group.enter()
            var request = URLRequest(url: url)
            request.httpMethod = "HEAD"
            probeSession.dataTask(with: request) { _, response, _ in
                if let http = response as? HTTPURLResponse, (200..<400).contains(http.statusCode) {
                    lock.lock()
                    reachable = true
                    lock.unlock()
                }
                group.leave()
            }.resume()
        }

        group.notify(queue: .global(qos: .utility)) {
            completion(reachable)
        }
    }

    private func buildStatus(connection: Connection, internetReachable: Bool) -> NetworkStatus {
        let state: String
        if !connection.isConnected {
            state = "offline"
        } else if internetReachable {
            state = "online"
        } else {
            state = "limited"
        }
        return NetworkStatus(connection: connection, internetReachable: internetReachable, state: state)
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
