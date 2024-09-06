import Foundation
import Capacitor

public class StatusBar {
    
    private var bridge: CAPBridgeProtocol
    private var isOverlayingWebview = true
    private var backgroundColor = UIColor.black
    private var backgroundView: UIView?
    private var observers: [NSObjectProtocol] = []

    init(bridge: CAPBridgeProtocol, config: StatusBarConfig) {
        self.bridge = bridge
        setupObservers(with: config)
    }
    
    deinit {
        observers.forEach { NotificationCenter.default.removeObserver($0) }
    }
    
    private func setupObservers(with config: StatusBarConfig) {
        observers.append(NotificationCenter.default.addObserver(forName: .capacitorViewDidAppear, object: .none, queue: .none) { [weak self] _ in
            self?.handleViewDidAppear(config: config)
        })
        observers.append(NotificationCenter.default.addObserver(forName: .capacitorStatusBarTapped, object: .none, queue: .none) { [weak self] _ in
            self?.bridge.triggerJSEvent(eventName: "statusTap", target: "window")
        })
        observers.append(NotificationCenter.default.addObserver(forName: .capacitorViewWillTransition, object: .none, queue: .none) { [weak self] _ in
            self?.handleViewWillTransition()
        })
    }
    
    private func handleViewDidAppear(config: StatusBarConfig) {
        setStyle(config.style)
        setBackgroundColor(config.backgroundColor)
        setOverlaysWebView(config.overlaysWebView)
    }
    
    private func handleViewWillTransition() {
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) { [weak self] in
            self?.resizeStatusBarBackgroundView()
            self?.resizeWebView()
        }
    }
    
    func setStyle(_ style: UIStatusBarStyle) {
        bridge.statusBarStyle = style
    }
    
    func setBackgroundColor(_ color : UIColor) {
        backgroundColor = color
        backgroundView?.backgroundColor = color
    }
    
    func setAnimation(_ animation: String) {
        if animation == "SLIDE" {
            bridge.statusBarAnimation = .slide
        } else if animation == "NONE" {
            bridge.statusBarAnimation = .none
        } else {
            bridge.statusBarAnimation = .fade
        }
    }

    func hide(animation: String) {
        setAnimation(animation)
        if bridge.statusBarVisible {
            bridge.statusBarVisible = false
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) { [weak self] in
                self?.resizeWebView()
                self?.backgroundView?.removeFromSuperview()
                self?.backgroundView?.isHidden = true
            }
        }
    }

    func show(animation: String) {
        setAnimation(animation)
        if !bridge.statusBarVisible {
            bridge.statusBarVisible = true
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) { [self] in
                resizeWebView()
                if !isOverlayingWebview {
                    resizeStatusBarBackgroundView()
                    bridge.webView?.superview?.addSubview(backgroundView!)
                }
                backgroundView?.isHidden = false
            }
        }
    }

    func getInfo() -> StatusBarInfo {
        let style: String
        switch bridge.statusBarStyle {
        case .default:
            style = "DEFAULT"
        case .lightContent:
            style = "DARK"
        case .darkContent:
            style = "LIGHT"
        @unknown default:
            style = "DEFAULT"
        }

        return StatusBarInfo(
            overlays: isOverlayingWebview,
            visible: bridge.statusBarVisible,
            style: style,
            color: UIColor.capacitor.hex(fromColor: backgroundColor),
            height: getStatusBarFrame().size.height
        )
    }
    
    func setOverlaysWebView(_ overlay: Bool) {
        if overlay == isOverlayingWebview { return }
        isOverlayingWebview = overlay
        if overlay {
            backgroundView?.removeFromSuperview()
        } else {
            initializeBackgroundViewIfNeeded()
            bridge.webView?.superview?.addSubview(backgroundView!)
        }
        resizeWebView()
    }
    
    private func resizeWebView() {
        guard
            let webView = bridge.webView,
            let bounds = bridge.viewController?.view.window?.windowScene?.screen.bounds
        else { return }
        bridge.viewController?.view.frame = bounds
        webView.frame = bounds
        let statusBarHeight = getStatusBarFrame().size.height;
        var webViewFrame = webView.frame;
        
        if isOverlayingWebview {
            let safeAreaTop = webView.safeAreaInsets.top;
            if (statusBarHeight >= safeAreaTop && safeAreaTop > 0) {
                webViewFrame.origin.y = safeAreaTop == 40 ? 20 : statusBarHeight - safeAreaTop
            } else {
                webViewFrame.origin.y = 0
            }
        } else {
            webViewFrame.origin.y = statusBarHeight;
        }
        webViewFrame.size.height -= webViewFrame.origin.y
        webView.frame = webViewFrame
    }
    
    private func resizeStatusBarBackgroundView() {
        backgroundView?.frame = getStatusBarFrame()
    }
    
    private func getStatusBarFrame() -> CGRect {
        return UIApplication.shared.windows.first(where: { $0.isKeyWindow })?.windowScene?.statusBarManager?.statusBarFrame ?? .zero
    }
    
    private func initializeBackgroundViewIfNeeded() {
        if backgroundView == nil {
            backgroundView = UIView(frame: getStatusBarFrame())
            backgroundView!.backgroundColor = backgroundColor
            backgroundView!.autoresizingMask = [.flexibleWidth, .flexibleBottomMargin]
            backgroundView!.isHidden = !bridge.statusBarVisible
        }
    }
}
