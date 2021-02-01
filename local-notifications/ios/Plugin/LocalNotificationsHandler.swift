import Capacitor
import UserNotifications

public class LocalNotificationsHandler: NSObject, NotificationHandlerProtocol {

    public weak var plugin: CAPPlugin?

    // Local list of notification id -> JSObject for storing options
    // between notification requets
    var notificationRequestLookup = [String: JSObject]()

    public func requestPermissions(with completion: ((Bool, Error?) -> Void)? = nil) {
        let center = UNUserNotificationCenter.current()
        center.requestAuthorization(options: [.badge, .alert, .sound]) { (granted, error) in
            completion?(granted, error)
        }
    }

    public func checkPermissions(with completion: ((UNAuthorizationStatus) -> Void)? = nil) {
        let center = UNUserNotificationCenter.current()
        center.getNotificationSettings { settings in
            completion?(settings.authorizationStatus)
        }
    }

    public func willPresent(notification: UNNotification) -> UNNotificationPresentationOptions {
        let notificationData = makeNotificationRequestJSObject(notification.request)

        self.plugin?.notifyListeners("localNotificationReceived", data: notificationData)

        if let options = notificationRequestLookup[notification.request.identifier] {
            let silent = options["silent"] as? Bool ?? false
            if silent {
                return UNNotificationPresentationOptions.init(rawValue: 0)
            }
        }

        return [
            .badge,
            .sound,
            .alert
        ]
    }

    public func didReceive(response: UNNotificationResponse) {
        var data = JSObject()

        // Get the info for the original notification request
        let originalNotificationRequest = response.notification.request

        let actionId = response.actionIdentifier

        // We turn the two default actions (open/dismiss) into generic strings
        if actionId == UNNotificationDefaultActionIdentifier {
            data["actionId"] = "tap"
        } else if actionId == UNNotificationDismissActionIdentifier {
            data["actionId"] = "dismiss"
        } else {
            data["actionId"] = actionId
        }

        // If the type of action was for an input type, get the value
        if let inputType = response as? UNTextInputNotificationResponse {
            data["inputValue"] = inputType.userText
        }

        data["notification"] = makeNotificationRequestJSObject(originalNotificationRequest)

        self.plugin?.notifyListeners("localNotificationActionPerformed", data: data, retainUntilConsumed: true)
    }

    /**
     * Turn a UNNotificationRequest into a JSObject to return back to the client.
     */
    func makeNotificationRequestJSObject(_ request: UNNotificationRequest) -> JSObject {
        let notificationRequest = notificationRequestLookup[request.identifier] ?? [:]
        return [
            "id": Int(request.identifier) ?? -1,
            "title": request.content.title,
            "sound": notificationRequest["sound"]  ?? "",
            "body": request.content.body,
            "extra": request.content.userInfo as? JSObject ?? [:],
            "actionTypeId": request.content.categoryIdentifier,
            "attachments": notificationRequest["attachments"]  ?? []
        ]
    }
}
