/*
 Licensed to the Apache Software Foundation (ASF) under one
 or more contributor license agreements.  See the NOTICE file
 distributed with this work for additional information
 regarding copyright ownership.  The ASF licenses this file
 to you under the Apache License, Version 2.0 (the
 "License"); you may not use this file except in compliance
 with the License.  You may obtain a copy of the License at
 http://www.apache.org/licenses/LICENSE-2.0
 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 KIND, either express or implied.  See the License for the
 specific language governing permissions and limitations
 under the License.
 */

import Foundation
import UIKit
import Capacitor

enum ResizePolicy: String {
    case resizeNone = "none"
    case resizeNative = "native"
    case resizeBody = "body"
    case resizeIonic = "ionic"
}

// swiftlint:disable type_body_length
@objc(KeyboardPlugin)
class KeyboardPlugin: CAPPlugin, UIScrollViewDelegate {
    private var hideTimer: Timer?
    private let uiClassString = ["UI", "Web", "Browser", "View"].joined()
    private let wkClassString = ["WK", "Content", "View"].joined()
    private let uiTraitsClassString = ["UI", "Text", "Input", "Traits"].joined()

    private var keyboardIsVisible = false
    private var keyboardResizes: ResizePolicy = .resizeNative
    private var keyboardStyle: String?
    private var paddingBottom = Double.zero
    private var disableScroll = false {
        didSet {
            if disableScroll == oldValue {
                return
            }

            DispatchQueue.main.async {
                if self.disableScroll {
                    self.webView?.scrollView.isScrollEnabled = false
                    self.webView?.scrollView.delegate = self
                } else {
                    self.webView?.scrollView.isScrollEnabled = true
                    self.webView?.scrollView.delegate = nil
                }
            }
        }
    }

    private static var uiOriginalImp: IMP?
    private static var wkOriginalImp: IMP?
    private var hideFormAccessoryBar = false {
        didSet {
            if hideFormAccessoryBar == oldValue {
                return
            }

            guard let uiMethod = class_getInstanceMethod(NSClassFromString(uiClassString),
                                                         #selector(getter: UITextField.inputAccessoryView)),
                  let wkMethod = class_getInstanceMethod(NSClassFromString(wkClassString),
                                                         #selector(getter: UITextField.inputAccessoryView)) else {
                return
            }

            if hideFormAccessoryBar {
                Self.uiOriginalImp = method_getImplementation(uiMethod)
                Self.wkOriginalImp = method_getImplementation(wkMethod)
                let block: @convention(block) (AnyObject) -> UIView? = { (_: AnyObject) in
                    return nil
                }
                let newImp: IMP = imp_implementationWithBlock(unsafeBitCast(block, to: AnyObject.self))
                method_setImplementation(uiMethod, newImp)
                method_setImplementation(wkMethod, newImp)
            } else {
                method_setImplementation(uiMethod, Self.uiOriginalImp!)
                method_setImplementation(wkMethod, Self.wkOriginalImp!)
            }
        }
    }

    public override func load() {
        disableScroll = self.bridge?.config.scrollingEnabled != true

        let config = self.getConfig()
        let style = config.getString("style")
        changeKeyboardStyle(style: style?.uppercased())

        let resizeMode = config.getString("resize", "native")!
        let keyboardResizes = ResizePolicy(rawValue: resizeMode) ?? .resizeNative
        CAPLog.print(self.pluginId, "-", "resize mode - \(keyboardResizes.rawValue)")

        hideFormAccessoryBar = true

        let notificationCenter = NotificationCenter.default

        notificationCenter.addObserver(self,
                                       selector: #selector(onKeyboardDidHide(_:)),
                                       name: UIResponder.keyboardDidHideNotification,
                                       object: nil)
        notificationCenter.addObserver(self,
                                       selector: #selector(onKeyboardDidShow(_:)),
                                       name: UIResponder.keyboardDidShowNotification,
                                       object: nil)
        notificationCenter.addObserver(self,
                                       selector: #selector(onKeyboardWillHide(_:)),
                                       name: UIResponder.keyboardWillHideNotification,
                                       object: nil)
        notificationCenter.addObserver(self,
                                       selector: #selector(onKeyboardWillShow(_:)),
                                       name: UIResponder.keyboardWillShowNotification,
                                       object: nil)

        if let webView {
            notificationCenter.removeObserver(webView,
                                              name: UIResponder.keyboardWillHideNotification,
                                              object: nil)
            notificationCenter.removeObserver(webView,
                                              name: UIResponder.keyboardWillShowNotification,
                                              object: nil)
            notificationCenter.removeObserver(webView,
                                              name: UIResponder.keyboardWillChangeFrameNotification,
                                              object: nil)
            notificationCenter.removeObserver(webView,
                                              name: UIResponder.keyboardDidChangeFrameNotification,
                                              object: nil)
        }
    }

    deinit {
        NotificationCenter.default.removeObserver(self)
    }

    // MARK: Keyboard events

    @objc func onKeyboardWillHide(_ notification: Notification) {
        guard let height = computeKeyboardHeight(from: notification) else {
            return
        }
        updateKeyboardHeight(height, duration: .zero)

        let timer = Timer.scheduledTimer(withTimeInterval: .zero, repeats: false) { _ in
            self.bridge?.triggerWindowJSEvent(eventName: "keyboardWillHide")
            self.notifyListeners("keyboardWillHide", data: nil)
        }
        hideTimer = timer
        RunLoop.current.add(timer, forMode: .common)
    }

    @objc func onKeyboardWillShow(_ notification: Notification) {
        hideTimer?.invalidate()
        hideTimer = nil

        guard let keyboardHeight = computeKeyboardHeight(from: notification),
              let duration = duration(from: notification) else {
            return
        }
        updateKeyboardHeight(keyboardHeight, duration: duration + 0.2)
        changeKeyboardStyle(style: keyboardStyle)

        let jsonData = "{ 'keyboardHeight': \(keyboardHeight) }"
        bridge?.triggerWindowJSEvent(eventName: "keyboardWillShow", data: jsonData)
        let dictData = ["keyboardHeight": keyboardHeight]
        notifyListeners("keyboardWillShow", data: dictData)
    }

    @objc func onKeyboardDidShow(_ notification: Notification) {
        resetScrollView()

        guard let keyboardHeight = computeKeyboardHeight(from: notification) else {
            return
        }
        let jsonData = "{ 'keyboardHeight': \(keyboardHeight) }"
        bridge?.triggerWindowJSEvent(eventName: "keyboardDidShow", data: jsonData)
        let dictData = ["keyboardHeight": keyboardHeight]
        notifyListeners("keyboardDidShow", data: dictData)
    }

    @objc func onKeyboardDidHide(_ notification: Notification) {
        resetScrollView()

        bridge?.triggerWindowJSEvent(eventName: "keyboardDidHide")
        notifyListeners("keyboardDidHide", data: nil)
    }

    // MARK: Helpers

    private func resetScrollView() {
        webView?.scrollView.contentInset = .zero
    }

    @objc func resize() {
        if keyboardResizes == .resizeNative {
            resizeWebView()
        }
        resizeDocument()
    }

    private func resizeWebView() {
        let bounds = computeWindowBounds()
        guard let webViewFrame = webView?.frame else {
            return
        }

        webView?.frame = CGRect(x: webViewFrame.origin.x,
                                y: webViewFrame.origin.y,
                                width: bounds.size.width - webViewFrame.origin.x,
                                height: bounds.size.height - webViewFrame.origin.y - CGFloat(paddingBottom))
    }

    private func resizeDocument() {
        let bounds = computeWindowBounds()
        switch keyboardResizes {
        case .resizeBody:
            resizeElement(element: "document.body",
                          withPaddingBottom: paddingBottom,
                          withScreenHeight: bounds.size.height)
        case .resizeIonic:
            resizeElement(element: "document.querySelector('ion-app')",
                          withPaddingBottom: paddingBottom,
                          withScreenHeight: bounds.size.height)
        default:
            break
        }
        resetScrollView()
    }

    // swiftlint:disable line_length
    private func resizeElement(element: String,
                               withPaddingBottom paddingBottom: Double,
                               withScreenHeight screenHeight: Double) {
        var height = Double(-1)
        if paddingBottom > 0 {
            height = screenHeight - paddingBottom
        }

        bridge?.eval(js: "(function() { var el = \(element); var height = \(height); if (el) { el.style.height = height > -1 ? height + 'px' : null; } })()")
    }
    // swiftlint:enable line_length

    private func computeWindowBounds() -> CGRect {
        var window: UIWindow?

        if let delegate = UIApplication.shared.delegate,
            delegate.responds(to: #selector(getter: UIApplicationDelegate.window)) {
            window = delegate.window!
        }

        if window == nil, #available(iOS 13.0, *) {
            let scene = UIApplication.shared.connectedScenes.first
            window = (scene as? UIWindowScene)?.windows.first(where: { $0.isKeyWindow })
        }
        if let window {
            return window.bounds
        } else {
            return CGRect.zero
        }
    }

    private func computeKeyboardHeight(from notification: Notification) -> Double? {
        guard let keyboardEndFrame = notification.userInfo?[UIResponder.keyboardFrameEndUserInfoKey] as? CGRect else {
            return nil
        }

        guard let webView else {
            return nil
        }
        return webView.frame.origin.y + webView.frame.size.height - keyboardEndFrame.origin.y
    }

    private func duration(from notification: Notification) -> Double? {
        let durationValue = notification.userInfo?[UIResponder.keyboardAnimationDurationUserInfoKey]
        guard let durationValueAsNumber = durationValue as? NSNumber else {
            return nil
        }
        return durationValueAsNumber.doubleValue
    }

    private func updateKeyboardHeight(_ height: Double, duration: Double) {
        if height == paddingBottom {
            return
        }
        paddingBottom = height
        Self.cancelPreviousPerformRequests(withTarget: self, selector: #selector(resize), object: nil)
        perform(#selector(resize), with: nil, afterDelay: duration, inModes: [.common])
    }

    private func changeKeyboardStyle(style: String?) {
        var appearance = UIKeyboardAppearance.default
        if style == "DARK" {
            appearance = .dark
        } else if style == "LIGHT" {
            appearance = .light
        }

        let block: @convention(block) (AnyObject) -> UIKeyboardAppearance = { (_: AnyObject) in
            appearance
        }
        let newImp: IMP? = imp_implementationWithBlock(unsafeBitCast(block, to: AnyObject.self))
        for classString in [wkClassString, uiTraitsClassString] {
            let `class`: AnyClass? = NSClassFromString(classString)
            let method = class_getInstanceMethod(`class`, #selector(getter: UITextField.keyboardAppearance))
            if let method {
                method_setImplementation(method, newImp!)
            } else {
                class_addMethod(`class`, #selector(getter: UITextField.keyboardAppearance), newImp!, "l@:")
            }
        }
        keyboardStyle = style
    }

    // MARK: Plugin interface

    @objc func setAccessoryBarVisible(_ call: CAPPluginCall) {
        let value = call.getBool("isVisible", false)
        CAPLog.print("Accessory bar visible change \(value)")
        hideFormAccessoryBar = !value
        call.resolve()
    }

    @objc func hide(_ call: CAPPluginCall) {
        DispatchQueue.main.async {
            self.webView?.endEditing(true)
        }
        call.resolve()
    }

    @objc func show(_ call: CAPPluginCall) {
        call.unimplemented()
    }

    @objc func setStyle(_ call: CAPPluginCall) {
        let style = call.getString("style", "LIGHT")
        keyboardStyle = style
        call.resolve()
    }

    @objc func setResizeMode(_ call: CAPPluginCall) {
        let modeString = call.getString("mode", "none")
        keyboardResizes = ResizePolicy(rawValue: modeString) ?? .resizeNone
        call.resolve()
    }

    @objc func getResizeMode(_ call: CAPPluginCall) {
        let response = ["mode": keyboardResizes.rawValue]
        call.resolve(response)
    }

    @objc func setScroll(_ call: CAPPluginCall) {
        let disabled = call.getBool("isDisabled", false)
        disableScroll = disabled
        call.resolve()
    }
}
// swiftlint:enable type_body_length
