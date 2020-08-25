import Foundation
import Capacitor
import SafariServices

@objc(CAPBrowserPlugin)
public class CAPBrowserPlugin: CAPPlugin, SFSafariViewControllerDelegate {
    private let implementation = Browser()
    
    var vc: SFSafariViewController?
    
    @objc func open(_ call: CAPPluginCall) {
        // validate the URL
        guard let urlString = call.getString("url"), let url = URL(string: urlString) else {
            call.reject("Must provide a valid URL to open")
            return
        }
        // extract the optional parameters
        var color: UIColor? = nil
        if let toolbarColor = call.getString("toolbarColor") {
            color = UIColor.capacitor.color(fromHex: toolbarColor)
        }
        let style = self.presentationStyle(for: call.getString("presentationStyle"))
        // prepare for display
        guard implementation.prepare(for: url, withTint: color, modalPresentation: style), let viewController = implementation.viewController else {
            call.reject("Unable to display URL")
            return
        }
        implementation.browserEventDidOccur = { [weak self] (event) in
            self?.notifyListeners(event.listenerEvent, data: [:])
        }
        // display
        DispatchQueue.main.async { [weak self] in
            self?.bridge?.presentVC(viewController, animated: true, completion: {
                if style == .popover {
                    self?.setCenteredPopover(viewController)
                }
                call.resolve()
            })
        }
    }

    @objc func close(_ call: CAPPluginCall) {
        DispatchQueue.main.async { [weak self] in
            if self?.implementation.viewController != nil {
                self?.bridge?.dismissVC(animated: true) {
                    call.resolve()
                    self?.implementation.cleanup()
                }
            }
            else {
                call.resolve()
            }
        }
    }

    @objc func prefetch(_ call: CAPPluginCall) {
        call.unimplemented()
    }
    
    private func presentationStyle(for style: String?) -> UIModalPresentationStyle {
        if let style = style, style == "popover", supportsPopover() {
            return .popover
        }
        return .fullScreen
    }
}

private extension BrowserEvent {
    var listenerEvent: String {
        switch self {
        case .loaded:
            return "browserPageLoaded"
        case .finished:
            return "browserFinished"
        }
    }
}
