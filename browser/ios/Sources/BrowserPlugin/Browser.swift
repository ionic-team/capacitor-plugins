import Foundation
import SafariServices

@objc public enum BrowserEvent: Int {
    case loaded
    case finished
}

@objc public class Browser: NSObject, SFSafariViewControllerDelegate, UIPopoverPresentationControllerDelegate {
    private var safariViewController: SFSafariViewController?
    public typealias BrowserEventCallback = (BrowserEvent) -> Void

    @objc public var browserEventDidOccur: BrowserEventCallback?
    @objc var viewController: UIViewController? {
        return safariViewController
    }

    @objc public func prepare(for url: URL, withTint tint: UIColor? = nil, modalPresentation style: UIModalPresentationStyle = .fullScreen) -> Bool {
        if safariViewController == nil, let scheme = url.scheme?.lowercased(), ["http", "https"].contains(scheme) {
            let safariVC = SFSafariViewController(url: url)
            safariVC.delegate = self
            if let color = tint {
                safariVC.preferredBarTintColor = color
            }
            safariVC.modalPresentationStyle = style
            if style == .popover {
                DispatchQueue.main.async {
                    safariVC.popoverPresentationController?.delegate = self
                }
            }
            safariViewController = safariVC
            return true
        }
        return false
    }

    @objc public func cleanup() {
        safariViewController = nil
    }

    public func safariViewControllerDidFinish(_ controller: SFSafariViewController) {
        browserEventDidOccur?(.finished)
        safariViewController = nil
    }

    public func safariViewController(_ controller: SFSafariViewController, didCompleteInitialLoad didLoadSuccessfully: Bool) {
        browserEventDidOccur?(.loaded)
    }

    public func presentationControllerDidDismiss(_ presentationController: UIPresentationController) {
        browserEventDidOccur?(.finished)
        safariViewController = nil
    }

    public func popoverPresentationControllerDidDismissPopover(_ popoverPresentationController: UIPopoverPresentationController) {
        browserEventDidOccur?(.finished)
        safariViewController = nil
    }
}
