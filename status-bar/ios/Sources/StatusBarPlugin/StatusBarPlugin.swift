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
        CAPPluginMethod(name: "setOverlaysWebView", returnType: CAPPluginReturnPromise),
    ]
    private var statusBar: StatusBar?
    
    override public func load() {
        guard let bridge = bridge else { return }
        statusBar = StatusBar(bridge: bridge, config: statusBarConfig())
    }
    
    private func statusBarConfig() -> StatusBarConfig {
        var config = StatusBarConfig()
        config.overlaysWebView = getConfig().getBoolean("StatusBarOverlaysWebView", config.overlaysWebView)
        if let colorConfig = getConfig().getString("StatusBarBackgroundColor"), let color = UIColor.capacitor.color(fromHex: colorConfig)
        {
            config.backgroundColor = color
        }
        if let configStyle = getConfig().getString("StatusBarStyle") {
            config.style = style(fromString: configStyle)
        }
        return config
    }
    
    private func style(fromString: String) -> UIStatusBarStyle {
        switch fromString.lowercased() {
        case "dark", "lightcontent":
            return .lightContent
        case "light", "darkcontent":
            return .darkContent
        case "default":
            return .default
        default:
            return .default
        }
    }

    @objc func setStyle(_ call: CAPPluginCall) {
        let options = call.options!
        if let styleString = options["style"] as? String {
            statusBar?.setStyle(style(fromString: styleString))
        }
        call.resolve([:])
    }

    @objc func setBackgroundColor(_ call: CAPPluginCall) {
        guard
            let hexString = call.options["color"] as? String,
            let color = UIColor.capacitor.color(fromHex: hexString)
        else { return }
        DispatchQueue.main.async { [weak self] in
            self?.statusBar?.setBackgroundColor(color)
        }
        call.resolve()
    }
    
    @objc func hide(_ call: CAPPluginCall) {
        let animation = call.getString("animation", "FADE")
        DispatchQueue.main.async { [weak self] in
            self?.statusBar?.hide(animation: animation)
        }
        call.resolve()
    }

    @objc func show(_ call: CAPPluginCall) {
        let animation = call.getString("animation", "FADE")
        DispatchQueue.main.async { [weak self] in
            self?.statusBar?.show(animation: animation)
        }
        call.resolve()
    }
    
    @objc func getInfo(_ call: CAPPluginCall) {
        DispatchQueue.main.async { [weak self] in
            guard let info = self?.statusBar?.getInfo() else { return }
            call.resolve([
                "visible": info.visible!,
                "style": info.style!,
                "color": info.color!,
                "overlays": info.overlays!
            ])
        }
    }

    @objc func setOverlaysWebView(_ call: CAPPluginCall) {
        guard let overlay = call.options["overlay"] as? Bool else { return }
        DispatchQueue.main.async { [weak self] in
            self?.statusBar?.setOverlaysWebView(overlay)
        }
        call.resolve()
    }
}
