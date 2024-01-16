// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorTextZoom",
    platforms: [.iOS(.v13)],
    products: [
        .library(
            name: "TextZoomPlugin",
            targets: ["TextZoomPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor6-spm-test.git", branch: "main")
    ],
    targets: [
        .target(
            name: "TextZoomPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor6-spm-test"),
                .product(name: "Cordova", package: "capacitor6-spm-test")
            ],
            path: "ios/Sources/TextZoomPlugin"),
        .testTarget(
            name: "TextZoomPluginTests",
            dependencies: ["TextZoomPlugin"],
            path: "ios/Tests/TextZoomPluginTests")
    ]
)