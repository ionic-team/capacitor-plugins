// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorPushNotifications",
    platforms: [.iOS(.v13)],
    products: [
        .library(
            name: "PushNotificationsPlugin",
            targets: ["PushNotificationsPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor6-spm-test.git", branch: "main")
    ],
    targets: [
        .target(
            name: "PushNotificationsPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor6-spm-test"),
                .product(name: "Cordova", package: "capacitor6-spm-test")
            ],
            path: "ios/Sources/PushNotificationsPlugin"),
        .testTarget(
            name: "PushNotificationsPluginTests",
            dependencies: ["PushNotificationsPlugin"],
            path: "ios/Tests/PushNotificationsPluginTests")
    ]
)