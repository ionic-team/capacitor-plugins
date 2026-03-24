import Foundation
import UIKit

@objc public class Device: NSObject {
    /**
     * Get current memory usage
     */
    public func getMemoryUsage() -> UInt64 {
        var taskInfo = mach_task_basic_info()
        var count = mach_msg_type_number_t(MemoryLayout<mach_task_basic_info>.size)/4
        let kerr: kern_return_t = withUnsafeMutablePointer(to: &taskInfo) {
            $0.withMemoryRebound(to: integer_t.self, capacity: 1) {
                task_info(mach_task_self_, task_flavor_t(MACH_TASK_BASIC_INFO), $0, &count)
            }
        }

        if kerr == KERN_SUCCESS {
            return taskInfo.resident_size
        } else {
            return 0
        }
    }

    public func getLanguageCode() -> String {
        return String(Locale.preferredLanguages[0].prefix(2))
    }

    public func getLanguageTag() -> String {
        return String(Locale.preferredLanguages[0])
    }

    public func getModelName() -> String {
        var size = 0
        sysctlbyname("hw.machine", nil, &size, nil, 0)
        var machine = [CChar](repeating: 0, count: size)
        sysctlbyname("hw.machine", &machine, &size, nil, 0)
        return String(cString: machine)
    }

    public func getSystemVersionInt() -> Int? {
        let exploded = UIDevice.current.systemVersion.split(separator: ".")

        var major = 0
        var minor = 0
        var patch = 0

        for (index, numStr) in exploded.enumerated() {
            switch index {
            case 0:
                major = Int(numStr) ?? 0
            case 1:
                minor = Int(numStr) ?? 0
            case 2:
                patch = Int(numStr) ?? 0
            default:
                break
            }
        }

        var combined: [String] = []
        combined.append(String(format: "%02d", major))
        combined.append(String(format: "%02d", minor))
        combined.append(String(format: "%02d", patch))

        return Int(combined.joined())
    }
}
