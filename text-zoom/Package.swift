// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorTextZoom",
    platforms: [.iOS(.v16)],
    products: [
        .library(
            name: "CapacitorTextZoom",
            targets: ["TextZoomPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor.git", from: "9.0.0-alpha.7")
    ],
    targets: [
        .target(
            name: "TextZoomPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor")
            ],
            path: "ios/Sources/TextZoomPlugin"),
        .testTarget(
            name: "TextZoomPluginTests",
            dependencies: ["TextZoomPlugin"],
            path: "ios/Tests/TextZoomPluginTests")
    ]
)
