// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorHapticsPlugin",
    platforms: [.iOS(.v13)],
    products: [
        .library(
            name: "HapticsPlugin",
            targets: ["HapticsPlugin", "Capacitor", "Cordova"]),
    ],
    targets: [
        .target(
            name: "HapticsPlugin", 
            dependencies: ["Capacitor", "Cordova"],
            path: "ios/Sources/HapticsPlugin"),
        .binaryTarget(
            name: "Capacitor",
            path: "../node_modules/@capacitor/ios/Frameworks/Capacitor.xcframework"
        ),
        .binaryTarget(
            name: "Cordova",
            path: "../node_modules/@capacitor/ios/Frameworks/Cordova.xcframework"
        ),
        .testTarget(
            name: "HapticsPluginTests",
            dependencies: ["HapticsPlugin"],
            path: "ios/Tests/HapticsPluginTests"),
    ]
)
