// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorBrowser",
    platforms: [.iOS(.v13)],
    products: [
        .library(
            name: "BrowserPlugin",
            targets: ["BrowserPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor6-spm-test.git", branch: "main")
    ],
    targets: [
        .target(
            name: "BrowserPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor6-spm-test"),
                .product(name: "Cordova", package: "capacitor6-spm-test")
            ],
            path: "ios/Sources/BrowserPlugin"),
        .testTarget(
            name: "BrowserPluginTests",
            dependencies: ["BrowserPlugin"],
            path: "ios/Tests/BrowserPluginTests")
    ]
)