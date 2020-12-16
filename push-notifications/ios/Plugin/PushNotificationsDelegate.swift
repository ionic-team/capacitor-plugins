import Capacitor
import UserNotifications

public class PushNotificationsDelegate: NSObject, NotificationHandlerProtocol  {
    public var plugin: CAPPlugin?
    var notificationRequestLookup = [String: JSObject]()
    
    public func requestPermissions(with completion: ((Bool, Error?) -> Void)? = nil) {
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .sound, .badge]) { granted, error in
            completion?(granted, error)
        }
    }
    
    public func checkPermissions(with completion: ((UNAuthorizationStatus) -> Void)? = nil) {
        UNUserNotificationCenter.current().getNotificationSettings { settings in
            completion?(settings.authorizationStatus)
        }
    }
    
    public func willPresent(notification: UNNotification) -> UNNotificationPresentationOptions {
        let notificationData = makeNotificationRequestJSObject(notification.request)        
        self.plugin?.notifyListeners("received", data: notificationData)
        
        if let options = notificationRequestLookup[notification.request.identifier] {
            let silent = options["silent"] as? Bool ?? false
            
            if silent {
                return UNNotificationPresentationOptions.init(rawValue: 0)
            }
        }
        
        return [.badge, .sound, .alert]
    }
    
    public func didReceive(response: UNNotificationResponse) {
        print("did receive notification in the background")
        print("\(response)")
        var data = JSObject()
        
        let originalNotificationRequest = response.notification.request
        let actionId = response.actionIdentifier
        
        if actionId == UNNotificationDefaultActionIdentifier {
            data["actionId"] = "tap"
        } else if actionId == UNNotificationDismissActionIdentifier {
            data["actionId"] = "dismiss"
        } else {
            data["actionId"] = actionId
        }
        
        if let inputType = response as? UNTextInputNotificationResponse {
            data["inputValue"] = inputType.userText
        }
        
        data["notification"] = makeNotificationRequestJSObject(originalNotificationRequest)
        
        self.plugin?.notifyListeners("actionPerformed", data: data, retainUntilConsumed: true)
        
    }
    
    func makeNotificationRequestJSObject(_ request: UNNotificationRequest) -> JSObject {
        let notificationRequest = notificationRequestLookup[request.identifier] ?? [:]
        return [
            "id": request.identifier,
            "title": request.content.title,
            "sound": notificationRequest["sound"]  ?? "",
            "body": request.content.body,
            "extra": request.content.userInfo as? JSObject ?? [:],
            "actionTypeId": request.content.categoryIdentifier,
            "attachments": notificationRequest["attachments"]  ?? []
        ]
    }
}
