// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorSplashScreen",
    platforms: [.iOS(.v16)],
    products: [
        .library(
            name: "CapacitorSplashScreen",
            targets: ["SplashScreenPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor.git", from: "9.0.0-alpha.7")
    ],
    targets: [
        .target(
            name: "SplashScreenPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor")
            ],
            path: "ios/Sources/SplashScreenPlugin"),
        .testTarget(
            name: "SplashScreenPluginTests",
            dependencies: ["SplashScreenPlugin"],
            path: "ios/Tests/SplashScreenPluginTests")
    ]
)
