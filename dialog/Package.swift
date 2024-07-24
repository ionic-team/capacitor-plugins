// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorDialog",
    platforms: [.iOS(.v13)],
    products: [
        .library(
            name: "CapacitorDialog",
            targets: ["DialogPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", branch: "main")
    ],
    targets: [
        .target(
            name: "DialogPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/DialogPlugin"),
        .testTarget(
            name: "DialogPluginTests",
            dependencies: ["DialogPlugin"],
            path: "ios/Tests/DialogPluginTests")
    ]
)
