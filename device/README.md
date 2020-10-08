# @capacitor/device

The Device API exposes internal information about the device, such as the model and operating system version, along with user information such as unique ids.

<!--DOCGEN_INDEX_START-->
* [getInfo()](#getinfo)
* [getBatteryInfo()](#getbatteryinfo)
* [getLanguageCode()](#getlanguagecode)
* [Interfaces](#interfaces)
<!--DOCGEN_INDEX_END-->

<!--DOCGEN_API_START-->
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->
## API

### getInfo

```typescript
getInfo() => Promise<DeviceInfo>
```

Return information about the underlying device/os/platform.

**Returns:** Promise&lt;[DeviceInfo](#deviceinfo)&gt;

**Since:** 1.0.0

--------------------


### getBatteryInfo

```typescript
getBatteryInfo() => Promise<DeviceBatteryInfo>
```

Return information about the battery.

**Returns:** Promise&lt;[DeviceBatteryInfo](#devicebatteryinfo)&gt;

**Since:** 1.0.0

--------------------


### getLanguageCode

```typescript
getLanguageCode() => Promise<DeviceLanguageCodeResult>
```

Get the device's current language locale code.

**Returns:** Promise&lt;[DeviceLanguageCodeResult](#devicelanguagecoderesult)&gt;

**Since:** 1.0.0

--------------------


### Interfaces


#### DeviceInfo

| Prop                | Type                                                  | Description                                                                                                                                  | Since |
| ------------------- | ----------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------- | ----- |
| **name**            | string                                                | The name of the device. For example, "John's iPhone". This is only supported on iOS.                                                         | 1.0.0 |
| **model**           | string                                                | The device model. For example, "iPhone".                                                                                                     | 1.0.0 |
| **platform**        | "ios" \| "android" \| "web"                           | The device platform (lowercase).                                                                                                             | 1.0.0 |
| **uuid**            | string                                                | The UUID of the device as available to the app. This identifier may change on modern mobile platforms that only allow per-app install UUIDs. | 1.0.0 |
| **operatingSystem** | "ios" \| "android" \| "windows" \| "mac" \| "unknown" | The operating system of the device.                                                                                                          | 1.0.0 |
| **osVersion**       | string                                                | The version of the device OS.                                                                                                                | 1.0.0 |
| **manufacturer**    | string                                                | The manufacturer of the device.                                                                                                              | 1.0.0 |
| **isVirtual**       | boolean                                               | Whether the app is running in a simulator/emulator.                                                                                          | 1.0.0 |
| **memUsed**         | number                                                | Approximate memory used by the current app, in bytes. Divide by 1048576 to get the number of MBs used.                                       | 1.0.0 |
| **diskFree**        | number                                                | How much free disk space is available on the the normal data storage. path for the os, in bytes                                              | 1.0.0 |
| **diskTotal**       | number                                                | The total size of the normal data storage path for the OS, in bytes.                                                                         | 1.0.0 |


#### DeviceBatteryInfo

| Prop             | Type    | Description                                                       | Since |
| ---------------- | ------- | ----------------------------------------------------------------- | ----- |
| **batteryLevel** | number  | A percentage (0 to 1) indicating how much the battery is charged. | 1.0.0 |
| **isCharging**   | boolean | Whether the device is charging.                                   | 1.0.0 |


#### DeviceLanguageCodeResult

| Prop      | Type   | Description                  | Since |
| --------- | ------ | ---------------------------- | ----- |
| **value** | string | Two character language code. | 1.0.0 |


<!--DOCGEN_API_END-->