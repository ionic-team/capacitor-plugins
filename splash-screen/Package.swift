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
        .package(url: "https://github.com/ionic-team/capacitor-spm.git", branch: "main")
    ],
    targets: [
        .target(
            name: "SplashScreenPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-spm"),
                .product(name: "Cordova", package: "capacitor-spm")
            ],
            path: "ios/Sources/SplashScreenPlugin"),
        .testTarget(
            name: "SplashScreenPluginTests",
            dependencies: ["SplashScreenPlugin"],
            path: "ios/Tests/SplashScreenPluginTests")
    ]
)
