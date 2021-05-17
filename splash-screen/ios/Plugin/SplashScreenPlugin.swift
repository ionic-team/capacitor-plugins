import Foundation
import Capacitor

@objc(SplashScreenPlugin)
public class SplashScreenPlugin: CAPPlugin {
    private var splashScreen: SplashScreen?

    override public func load() {
        if let view = bridge?.viewController?.view {
            splashScreen = SplashScreen(parentView: view, config: splashScreenConfig())
            splashScreen?.showOnLaunch()
        }
    }

    // Show the splash screen
    @objc public func show(_ call: CAPPluginCall) {
        if let splash = splashScreen {
            let settings = splashScreenSettings(from: call)
            splash.show(settings: settings,
                        completion: {
                            call.resolve()
                        })
        } else {
            call.reject("Unable to show Splash Screen")
        }

    }

    // Hide the splash screen
    @objc public func hide(_ call: CAPPluginCall) {
        if let splash = splashScreen {
            let settings = splashScreenSettings(from: call)
            splash.hide(settings: settings)
            call.resolve()
        } else {
            call.reject("Unable to hide Splash Screen")
        }
    }

    private func splashScreenSettings(from call: CAPPluginCall) -> SplashScreenSettings {
        var settings = SplashScreenSettings()

        if let showDuration = call.getInt("showDuration") {
            settings.showDuration = showDuration
        }
        if let fadeInDuration = call.getInt("fadeInDuration") {
            settings.fadeInDuration = fadeInDuration
        }
        if let fadeOutDuration = call.getInt("fadeOutDuration") {
            settings.fadeOutDuration = fadeOutDuration
        }
        if let autoHide = call.getBool("autoHide") {
            settings.autoHide = autoHide
        }
        return settings
    }

    private func splashScreenConfig() -> SplashScreenConfig {
        var config = SplashScreenConfig()

        if let backgroundColor = getConfigValue("backgroundColor") as? String {
            config.backgroundColor = UIColor.capacitor.color(fromHex: backgroundColor)
        }
        if let spinnerStyle = getConfigValue("iosSpinnerStyle") as? String {
            switch spinnerStyle.lowercased() {
            case "small":
                config.spinnerStyle = .white
            default:
                config.spinnerStyle = .whiteLarge
            }
        }
        if let spinnerColor = getConfigValue("spinnerColor") as? String {
            config.spinnerColor = UIColor.capacitor.color(fromHex: spinnerColor)
        }
        if let showSpinner = getConfigValue("showSpinner") as? Bool {
            config.showSpinner = showSpinner
        }

        if let launchShowDuration = getConfigValue("launchShowDuration") as? Int {
            config.launchShowDuration = launchShowDuration
        }
        if let launchAutoHide = getConfigValue("launchAutoHide") as? Bool {
            config.launchAutoHide = launchAutoHide
        }
        return config
    }

}
