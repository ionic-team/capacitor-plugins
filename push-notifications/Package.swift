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
        .package(url: "https://github.com/ionic-team/capacitor-spm.git", branch: "main")
    ],
    targets: [
        .target(
            name: "PushNotificationsPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-spm"),
                .product(name: "Cordova", package: "capacitor-spm")
            ],
            path: "ios/Sources/PushNotificationsPlugin"),
        .testTarget(
            name: "PushNotificationsPluginTests",
            dependencies: ["PushNotificationsPlugin"],
            path: "ios/Tests/PushNotificationsPluginTests")
    ]
)
