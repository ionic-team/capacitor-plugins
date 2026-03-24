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
        var notification = makePendingNotificationRequestJSObject(request)
        notification["sound"] = notificationRequest["sound"]  ?? ""
        notification["actionTypeId"] = request.content.categoryIdentifier
        notification["attachments"] = notificationRequest["attachments"]  ?? []
        return notification

    }

    func makePendingNotificationRequestJSObject(_ request: UNNotificationRequest) -> JSObject {
        var notification: JSObject = [
            "id": Int(request.identifier) ?? -1,
            "title": request.content.title,
            "body": request.content.body
        ]

        if let userInfo = JSTypes.coerceDictionaryToJSObject(request.content.userInfo) {
            var extra = userInfo["cap_extra"] as? JSObject ?? userInfo

            // check for any dates and convert them to strings
            for(key, value) in extra {
                if let date = value as? Date {
                    let dateString = ISO8601DateFormatter().string(from: date)
                    extra[key] = dateString
                }
            }

            notification["extra"] = extra

            if var schedule = userInfo["cap_schedule"] as? JSObject {
                // convert schedule at date to string
                if let date = schedule["at"] as? Date {
                    let dateString = ISO8601DateFormatter().string(from: date)
                    schedule["at"] = dateString
                }

                notification["schedule"] = schedule
            }
        }

        return notification

    }
}
