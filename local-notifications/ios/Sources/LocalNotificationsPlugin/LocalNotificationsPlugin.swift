import Foundation
import Capacitor
import UserNotifications

enum LocalNotificationError: LocalizedError {
    case contentNoId
    case contentNoTitle
    case contentNoBody
    case triggerConstructionFailed
    case triggerRepeatIntervalTooShort
    case attachmentNoId
    case attachmentNoUrl
    case attachmentFileNotFound(path: String)
    case attachmentUnableToCreate(String)

    var errorDescription: String? {
        switch self {
        case .attachmentFileNotFound(path: let path):
            return "Unable to find file \(path) for attachment"
        default:
            return ""
        }
    }
}

// swiftlint:disable type_body_length
@objc(LocalNotificationsPlugin)
public class LocalNotificationsPlugin: CAPPlugin, CAPBridgedPlugin {
    public let identifier = "LocalNotificationsPlugin"
    public let jsName = "LocalNotifications"
    public let pluginMethods: [CAPPluginMethod] = [
        CAPPluginMethod(name: "schedule", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "requestPermissions", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "checkPermissions", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "checkExactNotificationSetting", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "changeExactNotificationSetting", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "cancel", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "getPending", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "registerActionTypes", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "areEnabled", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "getDeliveredNotifications", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "removeAllDeliveredNotifications", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "removeDeliveredNotifications", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "createChannel", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "deleteChannel", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "listChannels", returnType: CAPPluginReturnPromise)
    ]
    private let notificationDelegationHandler = LocalNotificationsHandler()

    override public func load() {
        self.bridge?.notificationRouter.localNotificationHandler = self.notificationDelegationHandler
        self.notificationDelegationHandler.plugin = self
        self.shouldStringifyDatesInCalls = false
    }

    /**
     * Schedule a notification.
     */
    @objc func schedule(_ call: CAPPluginCall) {
        guard let notifications = call.getArray("notifications", JSObject.self) else {
            call.reject("Must provide notifications array as notifications option")
            return
        }
        var ids = [String]()

        for notification in notifications {
            guard let identifier = notification["id"] as? Int else {
                call.reject("Notification missing identifier")
                return
            }

            // let extra = notification["options"] as? JSObject ?? [:]

            var content: UNNotificationContent
            do {
                content = try makeNotificationContent(notification)
            } catch {
                CAPLog.print(error.localizedDescription)
                call.reject("Unable to make notification", nil, error)
                return
            }

            var trigger: UNNotificationTrigger?

            do {
                if let schedule = notification["schedule"] as? JSObject {
                    try trigger = handleScheduledNotification(call, schedule)
                }
            } catch {
                call.reject("Unable to create notification, trigger failed", nil, error)
                return
            }

            // Schedule the request.
            let request = UNNotificationRequest(identifier: "\(identifier)", content: content, trigger: trigger)

            self.notificationDelegationHandler.notificationRequestLookup[request.identifier] = notification

            let center = UNUserNotificationCenter.current()
            center.add(request) { (error: Error?) in
                if let theError = error {
                    CAPLog.print(theError.localizedDescription)
                    call.reject(theError.localizedDescription)
                }
            }

            ids.append(request.identifier)
        }

        let ret = ids.map({ (id) -> JSObject in
            return [
                "id": Int(id) ?? -1
            ]
        })
        call.resolve([
            "notifications": ret
        ])
    }

    /**
     * Request notification permission
     */
    @objc override public func requestPermissions(_ call: CAPPluginCall) {
        self.notificationDelegationHandler.requestPermissions { granted, error in
            guard error == nil else {
                call.reject(error!.localizedDescription)
                return
            }
            call.resolve(["display": granted ? "granted" : "denied"])
        }
    }

    @objc override public func checkPermissions(_ call: CAPPluginCall) {
        self.notificationDelegationHandler.checkPermissions { status in
            let permission: String

            switch status {
            case .authorized, .ephemeral, .provisional:
                permission = "granted"
            case .denied:
                permission = "denied"
            case .notDetermined:
                permission = "prompt"
            @unknown default:
                permission = "prompt"
            }

            call.resolve(["display": permission])
        }
    }

    @objc public func checkExactNotificationSetting(_ call: CAPPluginCall) {
        call.unimplemented()
    }

    @objc public func changeExactNotificationSetting(_ call: CAPPluginCall) {
        call.unimplemented()
    }

    /**
     * Cancel notifications by id
     */
    @objc func cancel(_ call: CAPPluginCall) {
        guard let notifications = call.getArray("notifications", JSObject.self), notifications.count > 0 else {
            call.reject("Must supply notifications to cancel")
            return
        }

        let ids = notifications.map({ (value: JSObject) -> String in
            if let idString = value["id"] as? String {
                return idString
            } else if let idNum = value["id"] as? NSNumber {
                return idNum.stringValue
            }
            return ""
        })

        UNUserNotificationCenter.current().removePendingNotificationRequests(withIdentifiers: ids)
        call.resolve()
    }

    /**
     * Get all pending notifications.
     */
    @objc func getPending(_ call: CAPPluginCall) {
        UNUserNotificationCenter.current().getPendingNotificationRequests(completionHandler: { (notifications) in
            CAPLog.print("num of pending notifications \(notifications.count)")
            CAPLog.print(notifications)

            let ret = notifications.compactMap({ [weak self] (notification) -> JSObject? in
                return self?.notificationDelegationHandler.makePendingNotificationRequestJSObject(notification)
            })

            call.resolve([
                "notifications": ret
            ])
        })
    }

    /**
     * Register allowed action types that a notification may present.
     */
    @objc func registerActionTypes(_ call: CAPPluginCall) {
        guard let types = call.getArray("types", JSObject.self) else {
            return
        }

        makeActionTypes(types)

        call.resolve()
    }

    /**
     * Check if Local Notifications are authorized and enabled
     */
    @objc func areEnabled(_ call: CAPPluginCall) {
        let center = UNUserNotificationCenter.current()
        center.getNotificationSettings { (settings) in
            let authorized = settings.authorizationStatus == UNAuthorizationStatus.authorized
            let enabled = settings.notificationCenterSetting == UNNotificationSetting.enabled
            call.resolve([
                "value": enabled && authorized
            ])
        }
    }

    /**
     * Build the content for a notification.
     */
    func makeNotificationContent(_ notification: JSObject) throws -> UNNotificationContent {
        guard let title = notification["title"] as? String else {
            throw LocalNotificationError.contentNoTitle
        }
        guard let body = notification["body"] as? String else {
            throw LocalNotificationError.contentNoBody
        }

        let extra = notification["extra"] as? JSObject ?? [:]
        let schedule = notification["schedule"] as? JSObject ?? [:]
        let content = UNMutableNotificationContent()
        content.title = NSString.localizedUserNotificationString(forKey: title, arguments: nil)
        content.body = NSString.localizedUserNotificationString(forKey: body,
                                                                arguments: nil)

        content.userInfo = [
            "cap_extra": extra,
            "cap_schedule": schedule
        ]

        if let actionTypeId = notification["actionTypeId"] as? String {
            content.categoryIdentifier = actionTypeId
        }

        if let threadIdentifier = notification["threadIdentifier"] as? String {
            content.threadIdentifier = threadIdentifier
        }

        if let summaryArgument = notification["summaryArgument"] as? String {
            content.summaryArgument = summaryArgument
        }

        if let sound = notification["sound"] as? String {
            content.sound = UNNotificationSound(named: UNNotificationSoundName(sound))
        }

        if let attachments = notification["attachments"] as? [JSObject] {
            content.attachments = try makeAttachments(attachments)
        }

        return content
    }

    /**
     * Build a notification trigger, such as triggering each N seconds, or
     * on a certain date "shape" (such as every first of the month)
     */
    func handleScheduledNotification(_ call: CAPPluginCall, _ schedule: JSObject) throws -> UNNotificationTrigger? {
        var at: Date?
        if let scheduleDate = schedule["at"] as? NSDate {
            at = scheduleDate as Date
        }
        let every = schedule["every"] as? String
        let count = schedule["count"] as? Int ?? 1
        let on = schedule["on"] as? JSObject
        let repeats = schedule["repeats"] as? Bool ?? false

        // If there's a specific date for this notificiation
        if let at = at {
            let dateInfo = Calendar.current.dateComponents(in: TimeZone.current, from: at)

            if dateInfo.date! < Date() {
                call.reject("Scheduled time must be *after* current time")
                return nil
            }

            let dateInterval = DateInterval(start: Date(), end: dateInfo.date!)

            // Notifications that repeat have to be at least a minute between each other
            if repeats && dateInterval.duration < 60 {
                throw LocalNotificationError.triggerRepeatIntervalTooShort
            }

            return UNTimeIntervalNotificationTrigger(timeInterval: dateInterval.duration, repeats: repeats)
        }

        // If this notification should repeat every count of day/month/week/etc. or on a certain
        // matching set of date components
        if let on = on {
            let dateComponents = getDateComponents(on)
            return UNCalendarNotificationTrigger(dateMatching: dateComponents, repeats: true)
        }

        if let every = every {
            if let repeatDateInterval = getRepeatDateInterval(every, count) {
                return UNTimeIntervalNotificationTrigger(timeInterval: repeatDateInterval.duration, repeats: true)
            }
        }

        return nil
    }

    /**
     * Given our schedule format, return a DateComponents object
     * that only contains the components passed in.
     */
    func getDateComponents(_ at: JSObject) -> DateComponents {
        // var dateInfo = Calendar.current.dateComponents(in: TimeZone.current, from: Date())
        // dateInfo.calendar = Calendar.current
        var dateInfo = DateComponents()

        if let year = at["year"] as? Int {
            dateInfo.year = year
        }
        if let month = at["month"] as? Int {
            dateInfo.month = month
        }
        if let day = at["day"] as? Int {
            dateInfo.day = day
        }
        if let hour = at["hour"] as? Int {
            dateInfo.hour = hour
        }
        if let minute = at["minute"] as? Int {
            dateInfo.minute = minute
        }
        if let second = at["second"] as? Int {
            dateInfo.second = second
        }
        if let weekday = at["weekday"] as? Int {
            dateInfo.weekday = weekday
        }
        return dateInfo
    }

    /**
     * Compute the difference between the string representation of a date
     * interval and today. For example, if every is "month", then we
     * return the interval between today and a month from today.
     */
    func getRepeatDateInterval(_ every: String, _ count: Int) -> DateInterval? {
        let cal = Calendar.current
        let now = Date()
        switch every {
        case "year":
            let newDate = cal.date(byAdding: .year, value: count, to: now)!
            return DateInterval(start: now, end: newDate)
        case "month":
            let newDate = cal.date(byAdding: .month, value: count, to: now)!
            return DateInterval(start: now, end: newDate)
        case "two-weeks":
            let newDate = cal.date(byAdding: .weekOfYear, value: 2 * count, to: now)!
            return DateInterval(start: now, end: newDate)
        case "week":
            let newDate = cal.date(byAdding: .weekOfYear, value: count, to: now)!
            return DateInterval(start: now, end: newDate)
        case "day":
            let newDate = cal.date(byAdding: .day, value: count, to: now)!
            return DateInterval(start: now, end: newDate)
        case "hour":
            let newDate = cal.date(byAdding: .hour, value: count, to: now)!
            return DateInterval(start: now, end: newDate)
        case "minute":
            let newDate = cal.date(byAdding: .minute, value: count, to: now)!
            return DateInterval(start: now, end: newDate)
        case "second":
            let newDate = cal.date(byAdding: .second, value: count, to: now)!
            return DateInterval(start: now, end: newDate)
        default:
            return nil
        }
    }

    /**
     * Make required UNNotificationCategory entries for action types
     */
    func makeActionTypes(_ actionTypes: [JSObject]) {
        var createdCategories = [UNNotificationCategory]()

        let generalCategory = UNNotificationCategory(identifier: "GENERAL",
                                                     actions: [],
                                                     intentIdentifiers: [],
                                                     options: .customDismissAction)

        createdCategories.append(generalCategory)
        for type in actionTypes {
            guard let id = type["id"] as? String else {
                CAPLog.print("⚡️ ", self.pluginId, "-", "Action type must have an id field")
                continue
            }
            let hiddenBodyPlaceholder = type["iosHiddenPreviewsBodyPlaceholder"] as? String ?? ""
            let actions = type["actions"] as? [JSObject] ?? []

            let newActions = makeActions(actions)

            // Create the custom actions for the TIMER_EXPIRED category.
            var newCategory: UNNotificationCategory?

            newCategory = UNNotificationCategory(identifier: id,
                                                 actions: newActions,
                                                 intentIdentifiers: [],
                                                 hiddenPreviewsBodyPlaceholder: hiddenBodyPlaceholder,
                                                 options: makeCategoryOptions(type))

            createdCategories.append(newCategory!)
        }

        let center = UNUserNotificationCenter.current()
        center.setNotificationCategories(Set(createdCategories))
    }

    /**
     * Build the required UNNotificationAction objects for each action type registered.
     */
    func makeActions(_ actions: [JSObject]) -> [UNNotificationAction] {
        var createdActions = [UNNotificationAction]()

        for action in actions {
            guard let id = action["id"] as? String else {
                CAPLog.print("⚡️ ", self.pluginId, "-", "Action must have an id field")
                continue
            }
            let title = action["title"] as? String ?? ""
            let input = action["input"] as? Bool ?? false

            var newAction: UNNotificationAction
            if input {
                let inputButtonTitle = action["inputButtonTitle"] as? String
                let inputPlaceholder = action["inputPlaceholder"] as? String ?? ""

                if inputButtonTitle != nil {
                    newAction = UNTextInputNotificationAction(identifier: id,
                                                              title: title,
                                                              options: makeActionOptions(action),
                                                              textInputButtonTitle: inputButtonTitle!,
                                                              textInputPlaceholder: inputPlaceholder)
                } else {
                    newAction = UNTextInputNotificationAction(identifier: id, title: title, options: makeActionOptions(action))
                }
            } else {
                // Create the custom actions for the TIMER_EXPIRED category.
                newAction = UNNotificationAction(identifier: id,
                                                 title: title,
                                                 options: makeActionOptions(action))
            }
            createdActions.append(newAction)
        }

        return createdActions
    }

    /**
     * Make options for UNNotificationActions
     */
    func makeActionOptions(_ action: JSObject) -> UNNotificationActionOptions {
        let foreground = action["foreground"] as? Bool ?? false
        let destructive = action["destructive"] as? Bool ?? false
        let requiresAuthentication = action["requiresAuthentication"] as? Bool ?? false

        if foreground {
            return .foreground
        }
        if destructive {
            return .destructive
        }
        if requiresAuthentication {
            return .authenticationRequired
        }
        return UNNotificationActionOptions(rawValue: 0)
    }

    /**
     * Make options for UNNotificationCategoryActions
     */
    func makeCategoryOptions(_ type: JSObject) -> UNNotificationCategoryOptions {
        let customDismiss = type["iosCustomDismissAction"] as? Bool ?? false
        let carPlay = type["iosAllowInCarPlay"] as? Bool ?? false
        let hiddenPreviewsShowTitle = type["iosHiddenPreviewsShowTitle"] as? Bool ?? false
        let hiddenPreviewsShowSubtitle = type["iosHiddenPreviewsShowSubtitle"] as? Bool ?? false

        if customDismiss {
            return .customDismissAction
        }
        if carPlay {
            return .allowInCarPlay
        }

        if hiddenPreviewsShowTitle {
            return .hiddenPreviewsShowTitle
        }
        if hiddenPreviewsShowSubtitle {
            return .hiddenPreviewsShowSubtitle
        }

        return UNNotificationCategoryOptions(rawValue: 0)
    }

    /**
     * Build the UNNotificationAttachment object for each attachment supplied.
     */
    func makeAttachments(_ attachments: [JSObject]) throws -> [UNNotificationAttachment] {
        var createdAttachments = [UNNotificationAttachment]()

        for attachment in attachments {
            guard let id = attachment["id"] as? String else {
                throw LocalNotificationError.attachmentNoId
            }
            guard let url = attachment["url"] as? String else {
                throw LocalNotificationError.attachmentNoUrl
            }
            guard let urlObject = makeAttachmentUrl(url) else {
                throw LocalNotificationError.attachmentFileNotFound(path: url)
            }

            let options = attachment["options"] as? JSObject ?? [:]

            do {
                let newAttachment = try UNNotificationAttachment(identifier: id, url: urlObject, options: makeAttachmentOptions(options))
                createdAttachments.append(newAttachment)
            } catch {
                throw LocalNotificationError.attachmentUnableToCreate(error.localizedDescription)
            }
        }

        return createdAttachments
    }

    /**
     * Get the internal URL for the attachment URL
     */
    func makeAttachmentUrl(_ path: String) -> URL? {
        guard let webURL = URL(string: path) else {
            return nil
        }

        return bridge?.localURL(fromWebURL: webURL)
    }

    /**
     * Build the options for the attachment, if any. (For example: the clipping rectangle to use
     * for image attachments)
     */
    func makeAttachmentOptions(_ options: JSObject) -> JSObject {
        var opts: JSObject = [:]

        if let iosUNNotificationAttachmentOptionsTypeHintKey = options["iosUNNotificationAttachmentOptionsTypeHintKey"] as? String {
            opts[UNNotificationAttachmentOptionsTypeHintKey] = iosUNNotificationAttachmentOptionsTypeHintKey
        }
        if let iosUNNotificationAttachmentOptionsThumbnailHiddenKey = options["iosUNNotificationAttachmentOptionsThumbnailHiddenKey"] as? String {
            opts[UNNotificationAttachmentOptionsThumbnailHiddenKey] = iosUNNotificationAttachmentOptionsThumbnailHiddenKey
        }
        if let iosUNNotificationAttachmentOptionsThumbnailClippingRectKey = options["iosUNNotificationAttachmentOptionsThumbnailClippingRectKey"] as? String {
            opts[UNNotificationAttachmentOptionsThumbnailClippingRectKey] = iosUNNotificationAttachmentOptionsThumbnailClippingRectKey
        }
        if let iosUNNotificationAttachmentOptionsThumbnailTimeKey = options["iosUNNotificationAttachmentOptionsThumbnailTimeKey"] as? String {
            opts[UNNotificationAttachmentOptionsThumbnailTimeKey] = iosUNNotificationAttachmentOptionsThumbnailTimeKey
        }
        return opts
    }

    /**
     * Get notifications in Notification Center
     */
    @objc func getDeliveredNotifications(_ call: CAPPluginCall) {
        UNUserNotificationCenter.current().getDeliveredNotifications(completionHandler: { (notifications) in
            let ret = notifications.map({ (notification) -> [String: Any] in
                return self.notificationDelegationHandler.makeNotificationRequestJSObject(notification.request)
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
        guard let notifications = call.getArray("notifications", JSObject.self) else {
            call.reject("Must supply notifications to remove")
            return
        }

        let ids = notifications.map { "\($0["id"] ?? "")" }
        UNUserNotificationCenter.current().removeDeliveredNotifications(withIdentifiers: ids)
        call.resolve()
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
}
