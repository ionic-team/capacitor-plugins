// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorGoogleMaps",
    platforms: [.iOS(.v14)],
    products: [
        .library(
            name: "CapacitorGoogleMapsPlugin",
            targets: ["CapacitorGoogleMapsPlugin"]),
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor6-spm-test.git", branch: "main"),
        .package(url: "https://github.com/googlemaps/google-maps-ios-utils.git", exact: "4.2.2"),
        .package(url: "https://github.com/ionic-team/capacitor-gmaps-spm", from: "8.3.1"),
    ],
    targets: [
        .target(
            name: "CapacitorGoogleMapsPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor6-spm-test"),
                .product(name: "Cordova", package: "capacitor6-spm-test"),
                .product(name: "GoogleMapsUtils", package: "google-maps-ios-utils"),
                .product(name: "GoogleMaps", package: "capacitor-gmaps-spm")
            ],
            path: "ios/Sources/CapacitorGoogleMapsPlugin"),
        .testTarget(
            name: "CapacitorGoogleMapsPluginTests",
            dependencies: ["CapacitorGoogleMapsPlugin"],
            path: "ios/Tests/CapacitorGoogleMapsPluginTests"),
    ]
)
