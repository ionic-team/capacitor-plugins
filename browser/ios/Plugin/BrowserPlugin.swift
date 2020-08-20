import Foundation
import Capacitor
import SafariServices

//@objc(BrowserPlugin)
//public class BrowserPlugin: CAPPlugin {
//    private let implementation = Browser()
//
//    @objc func echo(_ call: CAPPluginCall) {
//        let value = call.getString("value") ?? ""
//        call.success([
//            "value": implementation.echo(value)
//        ])
//    }
//}

@objc(CAPBrowserPlugin)
public class CAPBrowserPlugin: CAPPlugin, SFSafariViewControllerDelegate {
    var vc: SFSafariViewController?

    @objc func open(_ call: CAPPluginCall) {
        guard let urlString = call.getString("url") else {
            call.reject("Must provide a URL to open")
            return
        }

        if urlString.isEmpty {
            call.reject("URL must not be empty")
            return
        }

        let toolbarColor = call.getString("toolbarColor")
        let url = URL(string: urlString)
        if let scheme = url?.scheme, ["http", "https"].contains(scheme.lowercased()) {
            DispatchQueue.main.async { [weak self] in
                let safariVC = SFSafariViewController.init(url: url!)
                safariVC.delegate = self
                let presentationStyle = call.getString("presentationStyle")
                if presentationStyle != nil && presentationStyle == "popover" && (self?.supportsPopover() ?? false) {
                    safariVC.modalPresentationStyle = .popover
                    self?.setCenteredPopover(safariVC)
                } else {
                    safariVC.modalPresentationStyle = .fullScreen
                }

                if let toolbarColor = toolbarColor {
                    safariVC.preferredBarTintColor = UIColor.capacitor.color(fromHex: toolbarColor)
                }
                
                self?.bridge?.presentVC(safariVC, animated: true, completion: {
                    call.resolve()
                })
                self?.vc = safariVC
            }
        } else {
            call.reject("Invalid URL")
        }
    }

    @objc func close(_ call: CAPPluginCall) {
        if vc == nil {
            call.resolve()
        }
        DispatchQueue.main.async {
            self.bridge?.dismissVC(animated: true) {
                call.resolve()
            }
        }
    }

    @objc func prefetch(_ call: CAPPluginCall) {
        call.unimplemented()
    }

    public func safariViewControllerDidFinish(_ controller: SFSafariViewController) {
        self.notifyListeners("browserFinished", data: [:])
        vc = nil
    }

    public func safariViewController(_ controller: SFSafariViewController, didCompleteInitialLoad didLoadSuccessfully: Bool) {
        self.notifyListeners("browserPageLoaded", data: [:])
    }
}
