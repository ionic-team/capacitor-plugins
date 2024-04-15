// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorAppLauncher",
    platforms: [.iOS(.v13)],
    products: [
        .library(
            name: "CapacitorAppLauncher",
            targets: ["AppLauncherPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", branch: "main")
    ],
    targets: [
        .target(
            name: "AppLauncherPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/AppLauncherPlugin"),
        .testTarget(
            name: "AppLauncherPluginTests",
            dependencies: ["AppLauncherPlugin"],
            path: "ios/Tests/AppLauncherPluginTests")
    ]
)
