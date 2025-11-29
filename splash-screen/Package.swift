// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorSplashScreen",
    platforms: [.iOS(.v15)],
    products: [
        .library(
            name: "CapacitorSplashScreen",
            targets: ["SplashScreenPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", from: "8.0.0-beta")
    ],
    targets: [
        .target(
            name: "SplashScreenPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/SplashScreenPlugin"),
        .testTarget(
            name: "SplashScreenPluginTests",
            dependencies: ["SplashScreenPlugin"],
            path: "ios/Tests/SplashScreenPluginTests")
    ]
)
