import Foundation
import Capacitor
import UserNotifications

enum PushNotificationError: Error {
    case tokenParsingFailed
    case tokenRegistrationFailed
}

enum PushNotificationsPermissions: String {
    case prompt
    case denied
    case granted
}

@objc(PushNotificationsPlugin)
public class PushNotificationsPlugin: CAPPlugin, CAPBridgedPlugin {
    public let identifier = "PushNotificationsPlugin"
    public let jsName = "PushNotifications"
    public let pluginMethods: [CAPPluginMethod] = [
        CAPPluginMethod(name: "register", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "unregister", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "checkPermissions", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "requestPermissions", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "getDeliveredNotifications", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "removeAllDeliveredNotifications", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "removeDeliveredNotifications", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "createChannel", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "listChannels", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "deleteChannel", returnType: CAPPluginReturnPromise)
    ]
    private let notificationDelegateHandler = PushNotificationsHandler()
    private var appDelegateRegistrationCalled: Bool = false

    override public func load() {
        self.bridge?.notificationRouter.pushNotificationHandler = self.notificationDelegateHandler
        self.notificationDelegateHandler.plugin = self

        NotificationCenter.default.addObserver(self,
                                               selector: #selector(self.didRegisterForRemoteNotificationsWithDeviceToken(notification:)),
                                               name: .capacitorDidRegisterForRemoteNotifications,
                                               object: nil)

        NotificationCenter.default.addObserver(self,
                                               selector: #selector(self.didFailToRegisterForRemoteNotificationsWithError(notification:)),
                                               name: .capacitorDidFailToRegisterForRemoteNotifications,
                                               object: nil)
    }

    deinit {
        NotificationCenter.default.removeObserver(self)
    }

    /**
     * Register for push notifications
     */
    @objc func register(_ call: CAPPluginCall) {
        DispatchQueue.main.async {
            UIApplication.shared.registerForRemoteNotifications()
        }
        call.resolve()
    }

    /**
     * Unregister for remote notifications
     */
    @objc func unregister(_ call: CAPPluginCall) {
        DispatchQueue.main.async {
            UIApplication.shared.unregisterForRemoteNotifications()
            call.resolve()
        }
    }

    /**
     * Request notification permission
     */
    @objc override public func requestPermissions(_ call: CAPPluginCall) {
        self.notificationDelegateHandler.requestPermissions { granted, error in
            guard error == nil else {
                if let err = error {
                    call.reject(err.localizedDescription)
                    return
                }

                call.reject("unknown error in permissions request")
                return
            }

            var result: PushNotificationsPermissions = .denied

            if granted {
                result = .granted
            }

            call.resolve(["receive": result.rawValue])
        }
    }

    /**
     * Check notification permission
     */
    @objc override public func checkPermissions(_ call: CAPPluginCall) {
        self.notificationDelegateHandler.checkPermissions { status in
            var result: PushNotificationsPermissions = .prompt

            switch status {
            case .notDetermined:
                result = .prompt
            case .denied:
                result = .denied
            case .ephemeral, .authorized, .provisional:
                result = .granted
            @unknown default:
                result = .prompt
            }

            call.resolve(["receive": result.rawValue])
        }
    }

    /**
     * Get notifications in Notification Center
     */
    @objc func getDeliveredNotifications(_ call: CAPPluginCall) {
        if !appDelegateRegistrationCalled {
            call.reject("event capacitorDidRegisterForRemoteNotifications not called.  Visit https://capacitorjs.com/docs/apis/push-notifications for more information")
            return
        }
        UNUserNotificationCenter.current().getDeliveredNotifications(completionHandler: { (notifications) in
            let ret = notifications.map({ (notification) -> [String: Any] in
                return self.notificationDelegateHandler.makeNotificationRequestJSObject(notification.request)
            })
            call.resolve([
                "notifications": ret
            ])
        })
    }

    /**
     * Remove specified notifications from Notification Center
     */
    @objc func removeDeliveredNotifications(_ call: CAPPluginCall) {
        if !appDelegateRegistrationCalled {
            call.reject("event capacitorDidRegisterForRemoteNotifications not called.  Visit https://capacitorjs.com/docs/apis/push-notifications for more information")
            return
        }
        guard let notifications = call.getArray("notifications", JSObject.self) else {
            call.reject("Must supply notifications to remove")
            return
        }

        let ids = notifications.map { $0["id"] as? String ?? "" }
        UNUserNotificationCenter.current().removeDeliveredNotifications(withIdentifiers: ids)
        call.resolve()
    }

    /**
     * Remove all notifications from Notification Center
     */
    @objc func removeAllDeliveredNotifications(_ call: CAPPluginCall) {
        if !appDelegateRegistrationCalled {
            call.reject("event capacitorDidRegisterForRemoteNotifications not called.  Visit https://capacitorjs.com/docs/apis/push-notifications for more information")
            return
        }
        UNUserNotificationCenter.current().removeAllDeliveredNotifications()
        DispatchQueue.main.async(execute: {
            UIApplication.shared.applicationIconBadgeNumber = 0
        })
        call.resolve()
    }

    @objc func createChannel(_ call: CAPPluginCall) {
        call.unimplemented("Not available on iOS")
    }

    @objc func deleteChannel(_ call: CAPPluginCall) {
        call.unimplemented("Not available on iOS")
    }

    @objc func listChannels(_ call: CAPPluginCall) {
        call.unimplemented("Not available on iOS")
    }

    @objc public func didRegisterForRemoteNotificationsWithDeviceToken(notification: NSNotification) {
        appDelegateRegistrationCalled = true
        if let deviceToken = notification.object as? Data {
            let deviceTokenString = deviceToken.reduce("", {$0 + String(format: "%02X", $1)})
            notifyListeners("registration", data: [
                "value": deviceTokenString
            ])
        } else if let stringToken = notification.object as? String {
            notifyListeners("registration", data: [
                "value": stringToken
            ])
        } else {
            notifyListeners("registrationError", data: [
                "error": PushNotificationError.tokenParsingFailed.localizedDescription
            ])
        }
    }

    @objc public func didFailToRegisterForRemoteNotificationsWithError(notification: NSNotification) {
        appDelegateRegistrationCalled = true
        guard let error = notification.object as? Error else {
            return
        }
        notifyListeners("registrationError", data: [
            "error": error.localizedDescription
        ])
    }
}
