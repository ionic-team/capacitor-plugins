# @capacitor/geolocation

The Geolocation API provides simple methods for getting and tracking the current position of the device using GPS, along with altitude, heading, and speed information if available.

## Install

```bash
npm install @capacitor/geolocation
npx cap sync
```

## API

<docgen-index>

* [`getCurrentPosition(...)`](#getcurrentposition)
* [`watchPosition(...)`](#watchposition)
* [`clearWatch(...)`](#clearwatch)
* [`checkPermissions()`](#checkpermissions)
* [`requestPermissions()`](#requestpermissions)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### getCurrentPosition(...)

```typescript
getCurrentPosition(options?: GeolocationOptions | undefined) => Promise<GeolocationPosition>
```

Get the current GPS location of the device

| Param         | Type                                                              |
| ------------- | ----------------------------------------------------------------- |
| **`options`** | <code><a href="#geolocationoptions">GeolocationOptions</a></code> |

**Returns:** <code>Promise&lt;<a href="#geolocationposition">GeolocationPosition</a>&gt;</code>

**Since:** 1.0.0

--------------------


### watchPosition(...)

```typescript
watchPosition(options: GeolocationOptions, callback: GeolocationWatchCallback) => CallbackID
```

Set up a watch for location changes. Note that watching for location changes
can consume a large amount of energy. Be smart about listening only when you need to.

| Param          | Type                                                                                                          |
| -------------- | ------------------------------------------------------------------------------------------------------------- |
| **`options`**  | <code><a href="#geolocationoptions">GeolocationOptions</a></code>                                             |
| **`callback`** | <code>(position: <a href="#geolocationposition">GeolocationPosition</a> \| null, err?: any) =&gt; void</code> |

**Returns:** <code>string</code>

**Since:** 1.0.0

--------------------


### clearWatch(...)

```typescript
clearWatch(options: { id: string; }) => Promise<void>
```

Clear a given watch

| Param         | Type                         |
| ------------- | ---------------------------- |
| **`options`** | <code>{ id: string; }</code> |

**Since:** 1.0.0

--------------------


### checkPermissions()

```typescript
checkPermissions() => Promise<GeolocationPermissionStatus>
```

Check location permissions

**Returns:** <code>Promise&lt;<a href="#geolocationpermissionstatus">GeolocationPermissionStatus</a>&gt;</code>

**Since:** 1.0.0

--------------------


### requestPermissions()

```typescript
requestPermissions() => Promise<GeolocationPermissionStatus>
```

Request location permissions

**Returns:** <code>Promise&lt;<a href="#geolocationpermissionstatus">GeolocationPermissionStatus</a>&gt;</code>

**Since:** 1.0.0

--------------------


### Interfaces


#### GeolocationPosition

| Prop            | Type                                                                                                                                                                                | Description                                             | Since |
| --------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------- | ----- |
| **`timestamp`** | <code>number</code>                                                                                                                                                                 | Creation timestamp for coords                           | 1.0.0 |
| **`coords`**    | <code>{ latitude: number; longitude: number; accuracy: number; altitudeAccuracy: number \| null; altitude: number \| null; speed: number \| null; heading: number \| null; }</code> | The GPS coordinates along with the accuracy of the data | 1.0.0 |


#### GeolocationOptions

| Prop                     | Type                 | Description                                                                                | Default            | Since |
| ------------------------ | -------------------- | ------------------------------------------------------------------------------------------ | ------------------ | ----- |
| **`enableHighAccuracy`** | <code>boolean</code> | High accuracy mode (such as GPS, if available)                                             | <code>false</code> | 1.0.0 |
| **`timeout`**            | <code>number</code>  | The maximum wait time in milliseconds for location updates                                 | <code>10000</code> | 1.0.0 |
| **`maximumAge`**         | <code>number</code>  | The maximum age in milliseconds of a possible cached position that is acceptable to return | <code>0</code>     | 1.0.0 |


#### GeolocationPermissionStatus

| Prop           | Type                                                                      |
| -------------- | ------------------------------------------------------------------------- |
| **`location`** | <code>"prompt" \| "prompt-with-rationale" \| "granted" \| "denied"</code> |

</docgen-api>
