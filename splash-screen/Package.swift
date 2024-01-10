// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorSplashScreen",
    platforms: [.iOS(.v13)],
    products: [
        .library(
            name: "SplashScreenPlugin",
            targets: ["SplashScreenPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor6-spm-test.git", branch: "main")
    ],
    targets: [
        .target(
            name: "SplashScreenPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor6-spm-test"),
                .product(name: "Cordova", package: "capacitor6-spm-test")
            ],
            path: "ios/Sources/SplashScreenPlugin"),
        .testTarget(
            name: "SplashScreenPluginTests",
            dependencies: ["SplashScreenPlugin"],
            path: "ios/Tests/SplashScreenPluginTests")
    ]
)