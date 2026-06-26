// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorBrowser",
    platforms: [.iOS(.v16)],
    products: [
        .library(
            name: "CapacitorBrowser",
            targets: ["BrowserPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", from: "9.0.0-alpha.5")
    ],
    targets: [
        .target(
            name: "BrowserPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/BrowserPlugin"),
        .testTarget(
            name: "BrowserPluginTests",
            dependencies: ["BrowserPlugin"],
            path: "ios/Tests/BrowserPluginTests")
    ]
)
