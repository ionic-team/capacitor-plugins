// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorTextZoom",
    platforms: [.iOS(.v14)],
    products: [
        .library(
            name: "CapacitorTextZoom",
            targets: ["TextZoomPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", from: "7.0.0")
    ],
    targets: [
        .target(
            name: "TextZoomPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/TextZoomPlugin"),
        .testTarget(
            name: "TextZoomPluginTests",
            dependencies: ["TextZoomPlugin"],
            path: "ios/Tests/TextZoomPluginTests")
    ]
)
