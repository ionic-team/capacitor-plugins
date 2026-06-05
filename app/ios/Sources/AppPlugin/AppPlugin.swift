import Foundation
import Capacitor

@objc(AppPlugin)
public class AppPlugin: CAPPlugin, CAPBridgedPlugin {
    public let identifier = "AppPlugin"
    public let jsName = "App"
    public let pluginMethods: [CAPPluginMethod] = [
        CAPPluginMethod(name: "exitApp", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "getInfo", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "getAppLanguage", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "getLaunchUrl", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "getState", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "minimizeApp", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "toggleBackButtonHandler", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "setBackGestureEnabled", returnType: CAPPluginReturnPromise)
    ]
    private var observers: [NSObjectProtocol] = []
    private var leftEdgePanRecognizer: UIScreenEdgePanGestureRecognizer? = nil
    private var rightEdgePanRecognizer: UIScreenEdgePanGestureRecognizer? = nil
  
    override public func load() {
        if getConfig().getBoolean("enableBackGestureHandler", false) {
          loadGestureRecognizers()
        }

        NotificationCenter.default.addObserver(self, selector: #selector(self.handleUrlOpened(notification:)), name: Notification.Name.capacitorOpenURL, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(self.handleUniversalLink(notification:)), name: Notification.Name.capacitorOpenUniversalLink, object: nil)
        observers.append(NotificationCenter.default.addObserver(forName: UIApplication.didBecomeActiveNotification, object: nil, queue: OperationQueue.main) { [weak self] (_) in
            self?.notifyListeners("appStateChange", data: [
                "isActive": true
            ])
        })
        observers.append(NotificationCenter.default.addObserver(forName: UIApplication.willResignActiveNotification, object: nil, queue: OperationQueue.main) { [weak self] (_) in
            self?.notifyListeners("appStateChange", data: [
                "isActive": false
            ])
        })

        observers.append(NotificationCenter.default.addObserver(forName: UIApplication.didEnterBackgroundNotification, object: nil, queue: OperationQueue.main) { [weak self] (_) in
            self?.notifyListeners("pause", data: nil)
        })

        observers.append(NotificationCenter.default.addObserver(forName: UIApplication.willEnterForegroundNotification, object: nil, queue: OperationQueue.main) { [weak self] (_) in
            self?.notifyListeners("resume", data: nil)
        })

    }

    deinit {
        NotificationCenter.default.removeObserver(self)
        for observer in observers {
            NotificationCenter.default.removeObserver(observer)
        }
    }

    @objc func handleUrlOpened(notification: NSNotification) {
        guard let object = notification.object as? [String: Any?] else {
            return
        }

        notifyListeners("appUrlOpen", data: makeUrlOpenObject(object), retainUntilConsumed: true)
    }

    @objc func handleUniversalLink(notification: NSNotification) {
        guard let object = notification.object as? [String: Any?] else {
            return
        }

        notifyListeners("appUrlOpen", data: makeUrlOpenObject(object), retainUntilConsumed: true)
    }

    func makeUrlOpenObject(_ object: [String: Any?]) -> JSObject {
        guard let url = object["url"] as? NSURL else {
            return [:]
        }

        let options = object["options"] as? [String: Any?] ?? [:]
        return [
            "url": url.absoluteString ?? "",
            "iosSourceApplication": options[UIApplication.OpenURLOptionsKey.sourceApplication.rawValue] as? String ?? "",
            "iosOpenInPlace": options[UIApplication.OpenURLOptionsKey.openInPlace.rawValue] as? String ?? ""
        ]
    }

    @objc func exitApp(_ call: CAPPluginCall) {
        call.unimplemented()
    }

    @objc func getInfo(_ call: CAPPluginCall) {
        if let info = Bundle.main.infoDictionary {
            call.resolve([
                "name": info["CFBundleDisplayName"] as? String ?? "",
                "id": info["CFBundleIdentifier"] as? String ?? "",
                "build": info["CFBundleVersion"] as? String ?? "",
                "version": info["CFBundleShortVersionString"] as? String ?? ""
            ])
        } else {
            call.reject("Unable to get App Info")
        }

    }

    @objc func getLaunchUrl(_ call: CAPPluginCall) {
        if let lastUrl = ApplicationDelegateProxy.shared.lastURL {
            let urlValue = lastUrl.absoluteString
            call.resolve([
                "url": urlValue
            ])
        }
        call.resolve()
    }

    @objc func getState(_ call: CAPPluginCall) {
        DispatchQueue.main.async {
            call.resolve([
                "isActive": UIApplication.shared.applicationState == UIApplication.State.active
            ])
        }
    }

    @objc func minimizeApp(_ call: CAPPluginCall) {
        call.unimplemented()
    }

    @objc func getAppLanguage(_ call: CAPPluginCall) {
        call.resolve([
            "value": Bundle.main.preferredLocalizations.first
        ])
    }

    @objc func toggleBackButtonHandler(_ call: CAPPluginCall) {
        call.unimplemented()
    }
  
    @objc func setBackGestureEnabled(_ call: CAPPluginCall) {
      if (call.getBool("enabled", false)) {
        loadGestureRecognizers()
      } else {
        destroyGestureRecognizers()
      }
      
      call.resolve()
    }

    @objc func handleEdgePan(_ recognizer: UIScreenEdgePanGestureRecognizer) {
        guard let view = bridge?.webView else { return }

        let translation = recognizer.translation(in: webView)
        let viewWidth = view.bounds.width
        let viewHeight = view.bounds.height

        var data: [String: Any] = [:]

        switch recognizer.edges {
        case .left:
            let progress = translation.x / viewWidth
            data["swipeEdge"] = "left"
            data["progress"] = max(0, min(1, progress))

        case .right:
            let progress = -translation.x / viewWidth
            data["swipeEdge"] = "right"
            data["progress"] = max(0, min(1, progress))
        case .top:
            let progress = translation.y / viewHeight
            data["swipeEdge"] = "top"
            data["progress"] = max(0, min(1, progress))
        case .bottom:
            let progress = -translation.y / viewHeight
            data["swipeEdge"] = "bottom"
            data["progress"] = max(0, min(1, progress))
        case .all:
            break
        default:
            break
        }

        switch recognizer.state {
        case .began:
            data["phase"] = "start"
        case .changed:
            data["phase"] = "progress"
        case .ended:
            data["phase"] = "commit"
        case .cancelled:
            data["phase"] = "cancel"
        case .failed:
            data["phase"] = "cancel"
        case .possible:
            break
        @unknown default:
            break
        }

        notifyListeners("backGesture", data: data)
    }

    private func loadGestureRecognizers() {
        guard let window = UIApplication.shared.delegate?.window ?? nil else { return }

        let leftEdgePanRecognizer = UIScreenEdgePanGestureRecognizer(target: self, action: #selector(self.handleEdgePan(_:)))
        leftEdgePanRecognizer.delegate = self
        leftEdgePanRecognizer.edges = .left

        let rightEdgePanRecognizer = UIScreenEdgePanGestureRecognizer(target: self, action: #selector(self.handleEdgePan(_:)))
        rightEdgePanRecognizer.delegate = self
        rightEdgePanRecognizer.edges = .right

        window.addGestureRecognizer(leftEdgePanRecognizer)
        window.addGestureRecognizer(rightEdgePanRecognizer)
      
        self.leftEdgePanRecognizer = leftEdgePanRecognizer
        self.rightEdgePanRecognizer = rightEdgePanRecognizer
    }
  
    private func destroyGestureRecognizers() {
      guard let window = UIApplication.shared.delegate?.window ?? nil,
            let leftEdgePanRecognizer = self.leftEdgePanRecognizer,
            let rightEdgePanRecognizer = self.rightEdgePanRecognizer
        else { return }
      
      window.removeGestureRecognizer(leftEdgePanRecognizer)
      window.removeGestureRecognizer(rightEdgePanRecognizer)
    }
}

extension AppPlugin: UIGestureRecognizerDelegate {
    public func gestureRecognizer(_ gestureRecognizer: UIGestureRecognizer, shouldRecognizeSimultaneouslyWith otherGestureRecognizer: UIGestureRecognizer) -> Bool {
        return true
    }

    public func gestureRecognizer(_ gestureRecognizer: UIGestureRecognizer, shouldBeRequiredToFailBy otherGestureRecognizer: UIGestureRecognizer) -> Bool {
        return false
    }

    public func gestureRecognizer(_ gestureRecognizer: UIGestureRecognizer, shouldRequireFailureOf otherGestureRecognizer: UIGestureRecognizer) -> Bool {
        return false
    }
}
