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
        .package(url: "https://github.com/ionic-team/capacitor-spm.git", branch: "main")
    ],
    targets: [
        .target(
            name: "TextZoomPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-spm"),
                .product(name: "Cordova", package: "capacitor-spm")
            ],
            path: "ios/Sources/TextZoomPlugin"),
        .testTarget(
            name: "TextZoomPluginTests",
            dependencies: ["TextZoomPlugin"],
            path: "ios/Tests/TextZoomPluginTests")
    ]
)
