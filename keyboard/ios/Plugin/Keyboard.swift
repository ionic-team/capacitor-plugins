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

@objc(KeyboardPlugin)
class KeyboardPlugin: CAPPlugin, UIScrollViewDelegate {
    private var hideTimer: Timer?
    private let uiClassString = ["UI", "Web", "Browser", "View"].joined()
    private let wkClassString = ["WK", "Content", "View"].joined()
    private let uiTraitsClassString = ["UI", "Text", "Input", "Traits"].joined()

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

            guard let uiMethod: Method = class_getInstanceMethod(NSClassFromString(uiClassString),
                                                                 #selector(getter: UITextField.inputAccessoryView)) else {
                return
            }
            guard let wkMethod: Method = class_getInstanceMethod(NSClassFromString(wkClassString),
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
    private var keyboardIsVisible = false
    private var keyboardResizes: ResizePolicy = .resizeNative
    private var keyboardStyle: String?
    private var paddingBottom = 0

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
            notificationCenter.removeObserver(webView, name: UIResponder.keyboardWillHideNotification, object: nil)
            notificationCenter.removeObserver(webView, name: UIResponder.keyboardWillShowNotification, object: nil)
            notificationCenter.removeObserver(webView, name: UIResponder.keyboardWillChangeFrameNotification, object: nil)
            notificationCenter.removeObserver(webView, name: UIResponder.keyboardDidChangeFrameNotification, object: nil)
        }
    }
    
    deinit {
        NotificationCenter.default.removeObserver(self)
    }

    // MARK: Keyboard events

    private func resetScrollView() {
        webView?.scrollView.contentInset = .zero
    }

    @objc func onKeyboardWillHide(_ notification: Notification) {
        setKeyboardHeight(height: 0, delay: TimeInterval(0.01))
        resetScrollView()
        let timer = Timer.scheduledTimer(withTimeInterval: .zero, repeats: false) { _ in
            self.bridge?.triggerWindowJSEvent(eventName: "keyboardWillHide")
            self.notifyListeners("keyboardWillHide", data: nil)
        }
        hideTimer = timer
        RunLoop.current.add(timer, forMode: .common)
    }

    @objc func onKeyboardWillShow(_ notification: Notification) {
        changeKeyboardStyle(style: keyboardStyle)
        hideTimer?.invalidate()
        hideTimer = nil
        guard let rect = notification.userInfo?[UIResponder.keyboardFrameEndUserInfoKey] as? CGRect else {
            return
        }
        let webViewAbsolute = webView!.convert(webView!.frame, to: webView!.window!.screen.coordinateSpace)
        var height = webViewAbsolute.size.height + webViewAbsolute.origin.y - (UIScreen.main.bounds.size.height - rect.size.height)
        if height < 0 {
            height = 0
        }
        
        guard let durationValue = notification.userInfo?[UIResponder.keyboardAnimationDurationUserInfoKey] as? NSNumber else {
            return
        }
        let duration = durationValue.doubleValue + 0.2
        setKeyboardHeight(height: Int(height), delay: duration)
        resetScrollView()

        let data = "{ 'keyboardHeight': \(height) }"
        bridge?.triggerWindowJSEvent(eventName: "keyboardWillShow", data: data)
        let kbData = ["keyboardHeight": height]
        notifyListeners("keyboardWillShow", data: kbData)
    }

    @objc func onKeyboardDidShow(_ notification: Notification) {
        guard let rect = notification.userInfo?[UIResponder.keyboardFrameEndUserInfoKey] as? CGRect else {
            return
        }
        let height = rect.size.height

        resetScrollView()

        let data = "{ 'keyboardHeight': \(height) }"
        bridge?.triggerWindowJSEvent(eventName: "keyboardDidShow", data: data)
        let kbData = ["keyboardHeight": height]
        notifyListeners("keyboardDidShow", data: kbData)
    }

    @objc func onKeyboardDidHide(_ notification: Notification) {
        bridge?.triggerWindowJSEvent(eventName: "keyboardDidHide")
        notifyListeners("keyboardDidHide", data: nil)
        resetScrollView()
    }

    private func setKeyboardHeight(height: Int, delay: TimeInterval) {
        if height == paddingBottom {
            return
        }

        paddingBottom = height
        Self.cancelPreviousPerformRequests(withTarget: self, selector: #selector(updateFrame), object: nil)
        if delay == 0 {
            updateFrame()
        } else {
            perform(#selector(updateFrame), with: nil, afterDelay: delay, inModes: [.common])
        }
    }

    private func resizeElement(element: String,
                               withPaddingBottom paddingBottom: Int,
                               withScreenHeight screenHeight: Int) {
        var height = -1
        if paddingBottom > 0 {
            height = screenHeight - paddingBottom
        }

        bridge?.eval(js: "(function() { var el = \(element); var height = \(height); if (el) { el.style.height = height > -1 ? height + 'px' : null; } })()")
    }

    @objc func updateFrame() {
        var f = CGRect.zero
        var wf = CGRect.zero
        var window: UIWindow?

        if let delegate = UIApplication.shared.delegate, delegate.responds(to: #selector(getter: UIApplicationDelegate.window)) {
            window = delegate.window!
        }

        if window == nil {
            if #available(iOS 13.0, *) {
                let scene = UIApplication.shared.connectedScenes.first
                window = (scene as? UIWindowScene)?.windows.first(where: { $0.isKeyWindow })
            }
        }
        if let window {
            f = window.bounds
        }
        if let webView {
            wf = webView.frame
        }
        switch keyboardResizes {
        case .resizeBody:
            resizeElement(element: "document.body", withPaddingBottom: paddingBottom, withScreenHeight: Int(f.size.height))
        case .resizeIonic:
            resizeElement(element: "document.querySelector('ion-app')", withPaddingBottom: paddingBottom, withScreenHeight: Int(f.size.height))
        case .resizeNative:
            webView?.frame = CGRect(x: wf.origin.x, y: wf.origin.y, width: f.size.width - wf.origin.x, height: f.size.height - wf.origin.y - CGFloat(paddingBottom))
        default:
            break
        }
        resetScrollView()
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
            let c: AnyClass? = NSClassFromString(classString)
            let m = class_getInstanceMethod(c, #selector(getter: UITextField.keyboardAppearance))
            if let m {
                method_setImplementation(m, newImp!)
            } else {
                class_addMethod(c, #selector(getter: UITextField.keyboardAppearance), newImp!, "l@:")
            }
        }
        keyboardStyle = style
    }
}
