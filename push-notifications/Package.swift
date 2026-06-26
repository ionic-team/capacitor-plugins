// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorPushNotifications",
    platforms: [.iOS(.v16)],
    products: [
        .library(
            name: "CapacitorPushNotifications",
            targets: ["PushNotificationsPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", from: "9.0.0-alpha.5")
    ],
    targets: [
        .target(
            name: "PushNotificationsPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/PushNotificationsPlugin"),
        .testTarget(
            name: "PushNotificationsPluginTests",
            dependencies: ["PushNotificationsPlugin"],
            path: "ios/Tests/PushNotificationsPluginTests")
    ]
)
