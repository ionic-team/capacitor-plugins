// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorAppLauncher",
    platforms: [.iOS(.v13)],
    products: [
        .library(
            name: "AppLauncherPlugin",
            targets: ["AppLauncherPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor6-spm-test.git", branch: "main")
    ],
    targets: [
        .target(
            name: "AppLauncherPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor6-spm-test"),
                .product(name: "Cordova", package: "capacitor6-spm-test")
            ],
            path: "ios/Sources/AppLauncherPlugin"),
        .testTarget(
            name: "AppLauncherPluginTests",
            dependencies: ["AppLauncherPlugin"],
            path: "ios/Tests/AppLauncherPluginTests")
    ]
)