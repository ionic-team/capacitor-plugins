// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorActionSheet",
    platforms: [.iOS(.v13)],
    products: [
        .library(
            name: "CapacitorActionSheet",
            targets: ["ActionSheetPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-spm.git", branch: "main")
    ],
    targets: [
        .target(
            name: "ActionSheetPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-spm"),
                .product(name: "Cordova", package: "capacitor-spm")
            ],
            path: "ios/Sources/ActionSheetPlugin"),
        .testTarget(
            name: "ActionSheetPluginTests",
            dependencies: ["ActionSheetPlugin"],
            path: "ios/Tests/ActionSheetPluginTests")
    ]
)
