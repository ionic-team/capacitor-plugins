# @capacitor/device

The Device API exposes internal information about the device, such as the model and operating system version, along with user information such as unique ids.

## Install

```bash
npm install @capacitor/device
npx cap sync
```

## API

<docgen-index>

* [`getInfo()`](#getinfo)
* [`getBatteryInfo()`](#getbatteryinfo)
* [`getLanguageCode()`](#getlanguagecode)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### getInfo()

```typescript
getInfo() => Promise<DeviceInfo>
```

Return information about the underlying device/os/platform.

**Returns:** <code>Promise&lt;<a href="#deviceinfo">DeviceInfo</a>&gt;</code>

**Since:** 1.0.0

--------------------


### getBatteryInfo()

```typescript
getBatteryInfo() => Promise<DeviceBatteryInfo>
```

Return information about the battery.

**Returns:** <code>Promise&lt;<a href="#devicebatteryinfo">DeviceBatteryInfo</a>&gt;</code>

**Since:** 1.0.0

--------------------


### getLanguageCode()

```typescript
getLanguageCode() => Promise<DeviceLanguageCodeResult>
```

Get the device's current language locale code.

**Returns:** <code>Promise&lt;<a href="#devicelanguagecoderesult">DeviceLanguageCodeResult</a>&gt;</code>

**Since:** 1.0.0

--------------------


### Interfaces


#### DeviceInfo

| Prop                  | Type                                                               | Description                                                                                                                                  | Since |
| --------------------- | ------------------------------------------------------------------ | -------------------------------------------------------------------------------------------------------------------------------------------- | ----- |
| **`name`**            | <code>string</code>                                                | The name of the device. For example, "John's iPhone". This is only supported on iOS and Android 7.1 or above.                                             | 1.0.0 |
| **`model`**           | <code>string</code>                                                | The device model. For example, "iPhone".                                                                                                     | 1.0.0 |
| **`platform`**        | <code>"ios" \| "android" \| "web"</code>                           | The device platform (lowercase).                                                                                                             | 1.0.0 |
| **`uuid`**            | <code>string</code>                                                | The UUID of the device as available to the app. This identifier may change on modern mobile platforms that only allow per-app install UUIDs. | 1.0.0 |
| **`operatingSystem`** | <code>"ios" \| "android" \| "windows" \| "mac" \| "unknown"</code> | The operating system of the device.                                                                                                          | 1.0.0 |
| **`osVersion`**       | <code>string</code>                                                | The version of the device OS.                                                                                                                | 1.0.0 |
| **`manufacturer`**    | <code>string</code>                                                | The manufacturer of the device.                                                                                                              | 1.0.0 |
| **`isVirtual`**       | <code>boolean</code>                                               | Whether the app is running in a simulator/emulator.                                                                                          | 1.0.0 |
| **`memUsed`**         | <code>number</code>                                                | Approximate memory used by the current app, in bytes. Divide by 1048576 to get the number of MBs used.                                       | 1.0.0 |
| **`diskFree`**        | <code>number</code>                                                | How much free disk space is available on the the normal data storage. path for the os, in bytes                                              | 1.0.0 |
| **`diskTotal`**       | <code>number</code>                                                | The total size of the normal data storage path for the OS, in bytes.                                                                         | 1.0.0 |


#### DeviceBatteryInfo

| Prop               | Type                 | Description                                                       | Since |
| ------------------ | -------------------- | ----------------------------------------------------------------- | ----- |
| **`batteryLevel`** | <code>number</code>  | A percentage (0 to 1) indicating how much the battery is charged. | 1.0.0 |
| **`isCharging`**   | <code>boolean</code> | Whether the device is charging.                                   | 1.0.0 |


#### DeviceLanguageCodeResult

| Prop        | Type                | Description                  | Since |
| ----------- | ------------------- | ---------------------------- | ----- |
| **`value`** | <code>string</code> | Two character language code. | 1.0.0 |

</docgen-api>
