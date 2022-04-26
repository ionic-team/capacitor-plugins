import UIKit

public struct SplashScreenConfig {
    var backgroundColor: UIColor?
    var spinnerStyle: UIActivityIndicatorView.Style?
    var spinnerColor: UIColor?
    var showSpinner = false
    var launchShowDuration = 500
    var launchAutoHide = true
    let launchFadeInDuration = 0
    // How long it should take to flick through all the images for an animation.
    var launchAnimationDuration = 3000
    // Whether a splash screen should be animated.
    var animated = false
}
