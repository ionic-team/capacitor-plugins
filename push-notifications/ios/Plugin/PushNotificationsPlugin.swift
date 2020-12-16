import Foundation
import Capacitor
import FirebaseCore
import FirebaseMessaging
import UserNotifications


enum PushNotificationError: Error {
    case tokenParsingFailed
}

enum LocationPermissions: String {
    case prompt = "prompt"
    case denied = "denied"
    case granted = "granted"
}

@objc(PushNotificationsPlugin)
public class PushNotificationsPlugin: CAPPlugin {
    private let notificationDelegateHandler = PushNotificationsDelegate()
    
    // Local list of notification id -> JSObject for storing options
    // between notification requets
    var notificationRequestLookup = [String:JSObject]()
    
    public override func load() {
        FirebaseApp.configure()
        self.bridge?.notificationRouter.pushNotificationHandler = self.notificationDelegateHandler
        self.notificationDelegateHandler.plugin = self
        
        NotificationCenter.default.addObserver(self, selector: #selector(self.didRegisterForRemoteNotificationsWithDeviceToken(notification:)), name: Notification.Name(Notification.Name.capacitorDidRegisterForRemoteNotifications.self.rawValue), object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(self.didFailToRegisterForRemoteNotificationsWithError(notification:)), name: Notification.Name(Notification.Name.capacitorDidFailToRegisterForRemoteNotifications.self.rawValue), object: nil)
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
     * Request notification permission
     */
    @objc public override func requestPermissions(_ call: CAPPluginCall) {
        self.notificationDelegateHandler.requestPermissions { granted, error in
            guard error == nil else {
                call.reject(error!.localizedDescription)
                return
            }
            
            var result: LocationPermissions = .denied
            
            if granted {
                result = .granted
            }
            
            call.resolve(["receive": result.rawValue])
        }
    }
    
    @objc override public func checkPermissions(_ call: CAPPluginCall) {
        self.notificationDelegateHandler.checkPermissions { status in
            var result: LocationPermissions = .prompt
            
            switch status {
            case .notDetermined:
                result = .prompt
            case .denied:
                result = .denied
            case .ephemeral, .authorized, .provisional:
                result = .granted
            default:
                result = .prompt
            }
            
            call.resolve(["receive": result.rawValue])
        }
    }
    
    /**
     * Get notifications in Notification Center
     */
    @objc func getDeliveredNotifications(_ call: CAPPluginCall) {
       
        
    }
    
    /**
     * Remove specified notifications from Notification Center
     */
    @objc func removeDeliveredNotifications(_ call: CAPPluginCall) {
        // TODO
        call.unimplemented("not implemented in swift")
    }
    
    /**
     * Remove all notifications from Notification Center
     */
    @objc func removeAllDeliveredNotifications(_ call: CAPPluginCall) {
        UNUserNotificationCenter.current().removeAllDeliveredNotifications()
        DispatchQueue.main.async(execute: {
            UIApplication.shared.applicationIconBadgeNumber = 0
        })
        call.resolve()
    }

    
    @objc func createChannel(_ call: CAPPluginCall) {
        call.unimplemented()
    }
    
    @objc func deleteChannel(_ call: CAPPluginCall) {
        call.unimplemented()
    }
    
    @objc func listChannels(_ call: CAPPluginCall) {
        call.unimplemented()
    }
    
    @objc public func didRegisterForRemoteNotificationsWithDeviceToken(notification: NSNotification){
        print("calling did register for notifications")
        if let deviceToken = notification.object as? Data {
          let deviceTokenString = deviceToken.reduce("", {$0 + String(format: "%02X", $1)})
            Messaging.messaging().apnsToken = deviceToken
          notifyListeners("registration", data:[
            "value": deviceTokenString
          ])
        } else if let stringToken = notification.object as? String {
            Messaging.messaging().apnsToken = stringToken.data(using: .utf8)
          notifyListeners("registration", data:[
            "value": stringToken
          ])
        } else {
          notifyListeners("registrationError", data: [
            "error": PushNotificationError.tokenParsingFailed.localizedDescription
          ])
        }
    }
    
    @objc public func didFailToRegisterForRemoteNotificationsWithError(notification: NSNotification){
        print("calling FAILED register for notifications")
        guard let error = notification.object as? Error else {
          return
        }
        notifyListeners("registrationError", data:[
          "error": error.localizedDescription
        ])
    }
}
