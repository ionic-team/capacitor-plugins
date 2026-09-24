import XCTest
import Capacitor
import UserNotifications
@testable import PushNotificationsPlugin

class PushNotificationsTests: XCTestCase {
    func testAppEnabledForCurrentAuthorizationStatus() {
        let handler = TestPushNotificationsHandler()
        let statuses: [(UNAuthorizationStatus, Bool)] = [
            (.notDetermined, false),
            (.denied, false),
            (.authorized, true),
            (.provisional, true),
            (.ephemeral, true)
        ]

        for (status, expected) in statuses {
            handler.status = status
            let completion = expectation(description: "Authorization status \(status.rawValue)")
            handler.checkAppEnabled { enabled in
                XCTAssertEqual(enabled, expected)
                completion.fulfill()
            }
            wait(for: [completion], timeout: 1)
        }
    }

    func testEnabledMethodsAreRegistered() {
        let plugin = PushNotificationsPlugin()
        for name in ["checkAppEnabled", "checkChannelEnabled"] {
            XCTAssertTrue(plugin.pluginMethods.contains { $0.name == name && $0.returnType == CAPPluginReturnPromise })
        }
    }

    func testCheckChannelEnabledIsUnimplemented() throws {
        let rejected = expectation(description: "Channel checks are unavailable on iOS")
        let call = try XCTUnwrap(CAPPluginCall(
            callbackId: "test",
            methodName: "checkChannelEnabled",
            options: ["id": "general"],
            success: { _, _ in XCTFail("Channel checks should not resolve on iOS") },
            error: { error in
                XCTAssertEqual(error?.code, "UNIMPLEMENTED")
                XCTAssertEqual(error?.message, "Not available on iOS")
                rejected.fulfill()
            }
        ))

        PushNotificationsPlugin().checkChannelEnabled(call)
        wait(for: [rejected], timeout: 1)
    }
}

private class TestPushNotificationsHandler: PushNotificationsHandler {
    var status: UNAuthorizationStatus = .notDetermined

    override func checkPermissions(with completion: ((UNAuthorizationStatus) -> Void)? = nil) {
        completion?(status)
    }
}
