import Foundation
import Capacitor

/**
 * StatusBar plugin. Requires "View controller-based status bar appearance" to
 * be "YES" in Info.plist
 */
@objc(StatusBarPlugin)
public class StatusBarPlugin: CAPPlugin, CAPBridgedPlugin {
    public let identifier = "StatusBarPlugin"
    public let jsName = "StatusBar"
    public let pluginMethods: [CAPPluginMethod] = [
        CAPPluginMethod(name: "setStyle", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "setBackgroundColor", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "show", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "hide", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "getInfo", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "setOverlaysWebView", returnType: CAPPluginReturnPromise)
    ]
    private var observer: NSObjectProtocol?

    override public func load() {
        observer = NotificationCenter.default.addObserver(forName: Notification.Name.capacitorStatusBarTapped, object: .none, queue: .none) { [weak self] _ in
            self?.bridge?.triggerJSEvent(eventName: "statusTap", target: "window")
        }
    }

    deinit {
        if let observer = observer {
            NotificationCenter.default.removeObserver(observer)
        }
    }

    @objc func setStyle(_ call: CAPPluginCall) {
        let options = call.options!

        if let style = options["style"] as? String {
            if style == "DARK" {
                bridge?.statusBarStyle = .lightContent
            } else if style == "LIGHT" {
                bridge?.statusBarStyle = .darkContent
            } else if style == "DEFAULT" {
                bridge?.statusBarStyle = .default
            }
        }

        call.resolve([:])
    }

    @objc func setBackgroundColor(_ call: CAPPluginCall) {
        let options = call.options

        if let color = options?["color"] as? String {
            DispatchQueue.main.async {
                if let window = UIApplication.shared.windows.first {
                    let statusBarView = UIView(frame: window.windowScene?.statusBarManager?.statusBarFrame ?? CGRect.zero)
                    if let uiColor = UIColor(hexString: color) {
                        statusBarView.backgroundColor = uiColor
                        window.addSubview(statusBarView)
                        if uiColor.isLight {
                            self.bridge?.statusBarStyle = .darkContent
                         } else {
                            self.bridge?.statusBarStyle = .lightContent
                        }
                    }
                }
            }
        }

        call.resolve([:])
    }

    func setAnimation(_ call: CAPPluginCall) {
        let animation = call.getString("animation", "FADE")
        if animation == "SLIDE" {
            bridge?.statusBarAnimation = .slide
        } else if animation == "NONE" {
            bridge?.statusBarAnimation = .none
        } else {
            bridge?.statusBarAnimation = .fade
        }
    }

    @objc func hide(_ call: CAPPluginCall) {
        setAnimation(call)
        bridge?.statusBarVisible = false
        call.resolve()
    }

    @objc func show(_ call: CAPPluginCall) {
        setAnimation(call)
        bridge?.statusBarVisible = true
        call.resolve()
    }

    @objc func getInfo(_ call: CAPPluginCall) {
        DispatchQueue.main.async { [weak self] in
            guard let bridge = self?.bridge else {
                return
            }
            let style: String
            switch bridge.statusBarStyle {
            case .default:
                if bridge.userInterfaceStyle == UIUserInterfaceStyle.dark {
                    style = "DARK"
                } else {
                    style = "LIGHT"
                }
            case .lightContent:
                style = "DARK"
            default:
                style = "LIGHT"
            }

            call.resolve([
                "visible": bridge.statusBarVisible,
                "style": style
            ])
        }
    }

    @objc func setOverlaysWebView(_ call: CAPPluginCall) {
        call.unimplemented()
    }
}


extension UIColor {
    convenience init?(hexString: String) {
        
        var hexSanitized = hexString.trimmingCharacters(in: .whitespacesAndNewlines)
        hexSanitized = hexSanitized.replacingOccurrences(of: "#", with: "")
        print("Failed to scan hex value.",hexSanitized)
        var rgb: UInt64 = 0
        
        guard Scanner(string: hexSanitized).scanHexInt64(&rgb) else { return nil }
        
        self.init(red: CGFloat((rgb & 0xFF0000) >> 16) / 255.0,
                  green: CGFloat((rgb & 0x00FF00) >> 8) / 255.0,
                  blue: CGFloat(rgb & 0x0000FF) / 255.0,
                  alpha: 1.0)
    }
    var isLight: Bool {
        var white: CGFloat = 0
        getWhite(&white, alpha: nil)
        return white > 0.7
    }
}