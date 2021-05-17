# @capacitor/device

The Device API exposes internal information about the device, such as the model and operating system version, along with user information such as unique ids.

## Install

```bash
npm install @capacitor/device
npx cap sync
```

## Example

```typescript
import { Device } from '@capacitor/device';

const logDeviceInfo = async () => {
  const info = await Device.getInfo();

  console.log(info);
};

const logBatteryInfo = async () => {
  const info = await Device.getBatteryInfo();

  console.log(info);
};
```

## API

<docgen-index>

* [`getId()`](#getid)
* [`getInfo()`](#getinfo)
* [`getBatteryInfo()`](#getbatteryinfo)
* [`getLanguageCode()`](#getlanguagecode)
* [Interfaces](#interfaces)
* [Type Aliases](#type-aliases)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### getId()

```typescript
getId() => Promise<DeviceId>
```

Return an unique identifier for the device.

**Returns:** <code>Promise&lt;<a href="#deviceid">DeviceId</a>&gt;</code>

**Since:** 1.0.0

--------------------


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
getBatteryInfo() => Promise<BatteryInfo>
```

Return information about the battery.

**Returns:** <code>Promise&lt;<a href="#batteryinfo">BatteryInfo</a>&gt;</code>

**Since:** 1.0.0

--------------------


### getLanguageCode()

```typescript
getLanguageCode() => Promise<GetLanguageCodeResult>
```

Get the device's current language locale code.

**Returns:** <code>Promise&lt;<a href="#getlanguagecoderesult">GetLanguageCodeResult</a>&gt;</code>

**Since:** 1.0.0

--------------------


### Interfaces


#### DeviceId

| Prop       | Type                | Description                                                                                                                                                                                                                            | Since |
| ---------- | ------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ----- |
| **`uuid`** | <code>string</code> | The UUID of the device as available to the app. This identifier may change on modern mobile platforms that only allow per-app install UUIDs. On web, a random identifier is generated and stored on localStorage for subsequent calls. | 1.0.0 |


#### DeviceInfo

| Prop                  | Type                                                        | Description                                                                                                   | Since |
| --------------------- | ----------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------- | ----- |
| **`name`**            | <code>string</code>                                         | The name of the device. For example, "John's iPhone". This is only supported on iOS and Android 7.1 or above. | 1.0.0 |
| **`model`**           | <code>string</code>                                         | The device model. For example, "iPhone".                                                                      | 1.0.0 |
| **`platform`**        | <code>'ios' \| 'android' \| 'web'</code>                    | The device platform (lowercase).                                                                              | 1.0.0 |
| **`operatingSystem`** | <code><a href="#operatingsystem">OperatingSystem</a></code> | The operating system of the device.                                                                           | 1.0.0 |
| **`osVersion`**       | <code>string</code>                                         | The version of the device OS.                                                                                 | 1.0.0 |
| **`manufacturer`**    | <code>string</code>                                         | The manufacturer of the device.                                                                               | 1.0.0 |
| **`isVirtual`**       | <code>boolean</code>                                        | Whether the app is running in a simulator/emulator.                                                           | 1.0.0 |
| **`memUsed`**         | <code>number</code>                                         | Approximate memory used by the current app, in bytes. Divide by 1048576 to get the number of MBs used.        | 1.0.0 |
| **`diskFree`**        | <code>number</code>                                         | How much free disk space is available on the the normal data storage. path for the os, in bytes               | 1.0.0 |
| **`diskTotal`**       | <code>number</code>                                         | The total size of the normal data storage path for the OS, in bytes.                                          | 1.0.0 |
| **`webViewVersion`**  | <code>string</code>                                         | The web view browser version                                                                                  | 1.0.0 |


#### BatteryInfo

| Prop               | Type                 | Description                                                       | Since |
| ------------------ | -------------------- | ----------------------------------------------------------------- | ----- |
| **`batteryLevel`** | <code>number</code>  | A percentage (0 to 1) indicating how much the battery is charged. | 1.0.0 |
| **`isCharging`**   | <code>boolean</code> | Whether the device is charging.                                   | 1.0.0 |


#### GetLanguageCodeResult

| Prop        | Type                | Description                  | Since |
| ----------- | ------------------- | ---------------------------- | ----- |
| **`value`** | <code>string</code> | Two character language code. | 1.0.0 |


### Type Aliases


#### OperatingSystem

<code>'ios' | 'android' | 'windows' | 'mac' | 'unknown'</code>

</docgen-api>
