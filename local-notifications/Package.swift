// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorLocalNotifications",
    platforms: [.iOS(.v13)],
    products: [
        .library(
            name: "LocalNotificationsPlugin",
            targets: ["LocalNotificationsPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor6-spm-test.git", branch: "main")
    ],
    targets: [
        .target(
            name: "LocalNotificationsPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor6-spm-test"),
                .product(name: "Cordova", package: "capacitor6-spm-test")
            ],
            path: "ios/Sources/LocalNotificationsPlugin"),
        .testTarget(
            name: "LocalNotificationsPluginTests",
            dependencies: ["LocalNotificationsPlugin"],
            path: "ios/Tests/LocalNotificationsPluginTests")
    ]
)
