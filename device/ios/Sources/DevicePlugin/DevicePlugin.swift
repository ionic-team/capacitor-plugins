import Foundation
import UIKit
import Capacitor

@objc(DevicePlugin)
public class DevicePlugin: CAPPlugin, CAPBridgedPlugin {
    public static let batteryChargingStateChangeEvent = "batteryChargingStateChange"

    public let identifier = "DevicePlugin"
    public let jsName = "Device"
    public let pluginMethods: [CAPPluginMethod] = [
        CAPPluginMethod(name: "getId", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "getInfo", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "getBatteryInfo", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "getLanguageCode", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "getLanguageTag", returnType: CAPPluginReturnPromise)
    ]
    private let implementation = Device()
    private var lastBatteryChargingState: Bool?

    override public func load() {
        UIDevice.current.isBatteryMonitoringEnabled = true
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(batteryStateDidChange),
            name: UIDevice.batteryStateDidChangeNotification,
            object: nil
        )
    }

    deinit {
        NotificationCenter.default.removeObserver(self)
        UIDevice.current.isBatteryMonitoringEnabled = false
    }

    @objc private func batteryStateDidChange() {
        let state = UIDevice.current.batteryState
        if state == .unknown {
            return
        }
        let charging = state == .charging || state == .full
        if lastBatteryChargingState == nil {
            lastBatteryChargingState = charging
            return
        }
        if lastBatteryChargingState != charging {
            lastBatteryChargingState = charging
            notifyListeners(DevicePlugin.batteryChargingStateChangeEvent, data: [
                "batteryLevel": UIDevice.current.batteryLevel,
                "isCharging": charging
            ])
        }
    }

    @objc func getId(_ call: CAPPluginCall) {
        if let uuid = UIDevice.current.identifierForVendor {
            call.resolve([
                "identifier": uuid.uuidString
            ])
        } else {
            call.reject("Id not available")
        }
    }
    @objc func getInfo(_ call: CAPPluginCall) {
        var isSimulator = false
        var modelName = ""
        #if targetEnvironment(simulator)
        isSimulator = true
        modelName = ProcessInfo().environment["SIMULATOR_MODEL_IDENTIFIER"] ?? "Simulator"
        #else
        modelName = implementation.getModelName()
        #endif

        let memUsed = implementation.getMemoryUsage()
        let systemVersionNum = implementation.getSystemVersionInt() ?? 0

        call.resolve([
            "memUsed": memUsed,
            "name": UIDevice.current.name,
            "model": modelName,
            "operatingSystem": "ios",
            "osVersion": UIDevice.current.systemVersion,
            "iOSVersion": systemVersionNum,
            "platform": "ios",
            "manufacturer": "Apple",
            "isVirtual": isSimulator,
            "webViewVersion": UIDevice.current.systemVersion
        ])
    }

    @objc func getBatteryInfo(_ call: CAPPluginCall) {
        UIDevice.current.isBatteryMonitoringEnabled = true

        call.resolve([
            "batteryLevel": UIDevice.current.batteryLevel,
            "isCharging": UIDevice.current.batteryState == .charging || UIDevice.current.batteryState == .full
        ])
    }

    @objc func getLanguageCode(_ call: CAPPluginCall) {
        let code = implementation.getLanguageCode()
        call.resolve([
            "value": code
        ])
    }

    @objc func getLanguageTag(_ call: CAPPluginCall) {
        let tag = implementation.getLanguageTag()
        call.resolve([
            "value": tag
        ])
    }

}
