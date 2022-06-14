import Foundation
import Capacitor

@objc public class SplashScreen: NSObject {

    var parentView: UIView
    var viewController = UIViewController()
    // This image view / images are used to show the series of images for an animation.
    var imageView = UIImageView()
    var image: UIImage?
    var spinner = UIActivityIndicatorView()
    // Used for `updateProgress` function.
    // Progress bar will only be shown when `updateProgress` is called by web app.
    var progressBar = UIProgressView()
    var progressBarVisible = false
    var config: SplashScreenConfig = SplashScreenConfig()
    var hideTask: Any?
    var isVisible: Bool = false

    init(parentView: UIView, config: SplashScreenConfig) {
        self.parentView = parentView
        self.config = config
    }

    public func showOnLaunch() {
        buildViews()
        if self.config.launchShowDuration == 0 {
            return
        }
        var settings = SplashScreenSettings()
        settings.showDuration = config.launchShowDuration
        settings.fadeInDuration = config.launchFadeInDuration
        settings.autoHide = config.launchAutoHide
        showSplash(settings: settings, completion: {}, isLaunchSplash: true)
    }

    public func show(settings: SplashScreenSettings, completion: @escaping () -> Void) {
        self.showSplash(settings: settings, completion: completion, isLaunchSplash: false)
    }

    // This function when called will automatically add a progress bar to the splash screen
    // if it is not available yet, and update the progress bar's progress.
    public func updateProgress(percentage: Float) {
        // Updating UI from main thread would cause issues hence a DispatchQueue is used.
        // This is similar to the approach used by functions `showSplash` and `hideSplash`.
        DispatchQueue.main.async { [weak self] in
            guard let strongSelf = self else {
                return
            }
            
            // In the case the progress bar is not visible yet.
            if !strongSelf.progressBarVisible {
                // Make the progress bar's progress white.
                strongSelf.progressBar.tintColor = .white
                // Add it to the parent view so it can be shown.
                strongSelf.parentView.addSubview(strongSelf.progressBar)
                // Make the progress bar show in the middle of the screen (x) but 75% down (y) to allow for the logo to not be blocked.
                strongSelf.progressBar.frame = CGRect(x: strongSelf.parentView.frame.midX - (strongSelf.parentView.frame.midX / 2), y: strongSelf.parentView.frame.midY * 1.25, width: strongSelf.parentView.frame.midX, height: 0)
                strongSelf.progressBarVisible = true
            }
            
            // Update the progress.
            strongSelf.progressBar.setProgress(percentage / 100, animated: true)
        }
    }

    public func hide(settings: SplashScreenSettings) {
        hideSplash(fadeOutDuration: settings.fadeOutDuration, isLaunchSplash: false)
    }

    private func showSplash(settings: SplashScreenSettings, completion: @escaping () -> Void, isLaunchSplash: Bool) {
        DispatchQueue.main.async { [weak self] in
            guard let strongSelf = self else {
                return
            }
            if let backgroundColor = strongSelf.config.backgroundColor {
                strongSelf.viewController.view.backgroundColor = backgroundColor
            }

            if strongSelf.config.showSpinner {
                if let style = strongSelf.config.spinnerStyle {
                    strongSelf.spinner.style = style
                }

                if let spinnerColor = strongSelf.config.spinnerColor {
                    strongSelf.spinner.color = spinnerColor
                }
            }

            strongSelf.parentView.addSubview(strongSelf.viewController.view)

            // If the config says to animate.
            if strongSelf.config.animated {
                // Add the animated imageview across to the main view for viewing.
                // Done with the subview to ensure the fade is done evenly.
                strongSelf.viewController.view.addSubview(strongSelf.imageView)
            }

            if strongSelf.config.showSpinner {
                strongSelf.parentView.addSubview(strongSelf.spinner)
                strongSelf.spinner.centerXAnchor.constraint(equalTo: strongSelf.parentView.centerXAnchor).isActive = true
                strongSelf.spinner.centerYAnchor.constraint(equalTo: strongSelf.parentView.centerYAnchor).isActive = true
            }

            strongSelf.parentView.isUserInteractionEnabled = false

            UIView.transition(with: strongSelf.viewController.view, duration: TimeInterval(Double(settings.fadeInDuration) / 1000), options: .curveLinear, animations: {
                // The animated imageview (if any) should fade in evenly with this.
                strongSelf.viewController.view.alpha = 1

                if strongSelf.config.showSpinner {
                    strongSelf.spinner.alpha = 1
                }
            }) { (_: Bool) in
                strongSelf.isVisible = true

                if settings.autoHide {
                    strongSelf.hideTask = DispatchQueue.main.asyncAfter(
                        deadline: DispatchTime.now() + (Double(settings.showDuration) / 1000)
                    ) {
                        strongSelf.hideSplash(fadeOutDuration: settings.fadeOutDuration, isLaunchSplash: isLaunchSplash)
                        completion()
                    }
                } else {
                    completion()
                }
            }
        }
    }

    // Creates an array of UIImage to play a sequence of images as an animation.
    // Ref: https://blog.devgenius.io/how-to-animate-your-images-in-swift-ios-swift-guide-64de30ea616b
    func animatedImages(for name: String) -> [UIImage] {
        var i = 0
        var images = [UIImage]()

        while let image = UIImage(named: "\(name)/\(name)_\(i)") {
            images.append(image)
            i += 1
        }
        return images
    }

    private func buildViews() {
        let storyboardName = Bundle.main.infoDictionary?["UILaunchStoryboardName"] as? String ?? "LaunchScreen"
        if let vc = UIStoryboard(name: storyboardName, bundle: nil).instantiateInitialViewController() {
            viewController = vc
        }

        // Observe for changes on frame and bounds to handle rotation resizing
        parentView.addObserver(self, forKeyPath: "frame", options: .new, context: nil)
        parentView.addObserver(self, forKeyPath: "bounds", options: .new, context: nil)

        updateSplashImageBounds()
        if config.showSpinner {
            spinner.translatesAutoresizingMaskIntoConstraints = false
            spinner.startAnimating()
        }

        // If the app config says to animate.
        if config.animated {
            // Use the first image of the image set as a placeholder until it animates.
            imageView.image = UIImage(named: "Splash/Splash_0")
            // Create the list of images to make it animated.
            imageView.animationImages = self.animatedImages(for: "Splash")
            // Set how long to play the images over. e.g. if it's set to 3 sec, then play all images over 3 sec.
            imageView.animationDuration = TimeInterval(Double(config.launchAnimationDuration) / 1000)
            // Start the animation.
            imageView.startAnimating()
        }
    }

    private func tearDown() {
        isVisible = false
        parentView.isUserInteractionEnabled = true
        viewController.view.removeFromSuperview()

        if config.showSpinner {
            spinner.removeFromSuperview()
        }
      
        // If the splash screen is animated.
        if config.animated {
            // Remove it from the view.
            imageView.removeFromSuperview()
        }
      
        // In the case that the progress bar has been activated.
        if self.progressBarVisible {
            // Remove the progress bar.
            progressBar.removeFromSuperview()
            self.progressBarVisible = false
        }
    }

    // Update the bounds for the splash image. This will also be called when
    // the parent view observers fire
    private func updateSplashImageBounds() {
        var window: UIWindow? = UIApplication.shared.delegate?.window ?? nil

        if #available(iOS 13, *), window == nil {
            let scene: UIWindowScene? = UIApplication.shared.connectedScenes.first as? UIWindowScene
            window = scene?.windows.filter({$0.isKeyWindow}).first
            if window == nil {
                window = scene?.windows.first
            }
        }

        if let unwrappedWindow = window {
            viewController.view.frame = CGRect(origin: CGPoint(x: 0, y: 0), size: unwrappedWindow.bounds.size)

            // If config says it's to be animated.
            if config.animated {
                // Fit the image view to the screen.
                imageView.frame = CGRect(origin: CGPoint(x: 0, y: 0), size: window!.bounds.size)
                // Ensure that the image fits the whole screen.
                imageView.contentMode = .scaleAspectFit
            }
        } else {
            CAPLog.print("Unable to find root window object for SplashScreen bounds. Please file an issue")
        }
    }

    override public func observeValue(forKeyPath keyPath: String?, of object: Any?, change _: [NSKeyValueChangeKey: Any]?, context: UnsafeMutableRawPointer?) {
        updateSplashImageBounds()
    }

    private func hideSplash(fadeOutDuration: Int, isLaunchSplash: Bool) {
        if isLaunchSplash, isVisible {
            CAPLog.print("SplashScreen.hideSplash: SplashScreen was automatically hidden after default timeout. " +
                            "You should call `SplashScreen.hide()` as soon as your web app is loaded (or increase the timeout). " +
                            "Read more at https://capacitorjs.com/docs/apis/splash-screen#hiding-the-splash-screen")
        }
        if !isVisible { return }
        DispatchQueue.main.async {
            UIView.transition(with: self.viewController.view, duration: TimeInterval(Double(fadeOutDuration) / 1000), options: .curveLinear, animations: {
                // ImageView for animated splash will fade with this.
                self.viewController.view.alpha = 0

                if self.config.showSpinner {
                    self.spinner.alpha = 0
                }
                
                // In the case the progress bar has been added.
                if self.progressBarVisible {
                    // Make the progress bar invisible.
                    self.progressBar.alpha = 0
                }
            }) { (_: Bool) in
                self.tearDown()
            }
        }
    }
}
