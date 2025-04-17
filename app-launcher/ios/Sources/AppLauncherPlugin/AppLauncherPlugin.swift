import Foundation
import Capacitor

@objc(AppLauncherPlugin)
public class AppLauncherPlugin: CAPPlugin, CAPBridgedPlugin {
    public let identifier = "AppLauncherPlugin"
    public let jsName = "AppLauncher"
    public let pluginMethods: [CAPPluginMethod] = [
        CAPPluginMethod(name: "canOpenUrl", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "openUrl", returnType: CAPPluginReturnPromise)
    ]

    @objc func canOpenUrl(_ call: CAPPluginCall) {
        guard let urlString = call.getString("url") else {
            call.reject("Must supply a URL")
            return
        }

        guard let url = URL.init(string: urlString) else {
            call.reject("Invalid URL")
            return
        }

        DispatchQueue.main.async {
            let canOpen = UIApplication.shared.canOpenURL(url)

            call.resolve([
                "value": canOpen
            ])
        }
    }

    @objc func openUrl(_ call: CAPPluginCall) {
        guard let urlString = call.getString("url") else {
            call.reject("Must supply a URL")
            return
        }

        guard let url = URL.init(string: urlString) else {
            call.reject("Invalid URL")
            return
        }

        DispatchQueue.main.async {
            UIApplication.shared.open(url, options: [:]) { (completed) in
                call.resolve([
                    "completed": completed
                ])
            }
        }
    }
}
