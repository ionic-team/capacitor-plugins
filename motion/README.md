# @capacitor/motion

The Motion API tracks accelerometer and device orientation (compass heading, etc.)

<!--DOCGEN_INDEX_START-->
* [addListener()](#addlistener)
* [addListener()](#addlistener)
* [removeAllListeners()](#removealllisteners)
* [Interfaces](#interfaces)
<!--DOCGEN_INDEX_END-->

<!--DOCGEN_API_START-->
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->
## API

### addListener

```typescript
addListener(eventName: 'accel', listenerFunc: (event: MotionEventResult) => void) => PluginListenerHandle
```

Add a listener for accelerometer data

| Param            | Type                               |
| ---------------- | ---------------------------------- |
| **eventName**    | "accel"                            |
| **listenerFunc** | (event: MotionEventResult) => void |

**Returns:** [PluginListenerHandle](#pluginlistenerhandle)

**Since:** 1.0.0

--------------------


### addListener

```typescript
addListener(eventName: 'orientation', listenerFunc: (event: MotionOrientationEventResult) => void) => PluginListenerHandle
```

Add a listener for device orientation change (compass heading, etc.)

| Param            | Type                                           |
| ---------------- | ---------------------------------------------- |
| **eventName**    | "orientation"                                  |
| **listenerFunc** | (event: DeviceMotionEventRotationRate) => void |

**Returns:** [PluginListenerHandle](#pluginlistenerhandle)

**Since:** 1.0.0

--------------------


### removeAllListeners

```typescript
removeAllListeners() => void
```

Remove all the listeners that are attached to this plugin.

**Returns:** void

**Since:** 1.0.0

--------------------


### Interfaces


#### PluginListenerHandle

| Prop       | Type       |
| ---------- | ---------- |
| **remove** | () => void |


#### MotionEventResult

| Prop                             | Type                                                            | Description                                                                                                                                                             | Since |
| -------------------------------- | --------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ----- |
| **acceleration**                 | [DeviceMotionEventAcceleration](#devicemotioneventacceleration) | An object giving the acceleration of the device on the three axis X, Y and Z. Acceleration is expressed in m/s                                                          | 1.0.0 |
| **accelerationIncludingGravity** | [DeviceMotionEventAcceleration](#devicemotioneventacceleration) | An object giving the acceleration of the device on the three axis X, Y and Z with the effect of gravity. Acceleration is expressed in m/s                               | 1.0.0 |
| **rotationRate**                 | [DeviceMotionEventRotationRate](#devicemotioneventrotationrate) | An object giving the rate of change of the device's orientation on the three orientation axis alpha, beta and gamma. Rotation rate is expressed in degrees per seconds. | 1.0.0 |
| **interval**                     | number                                                          | A number representing the interval of time, in milliseconds, at which data is obtained from the device.                                                                 | 1.0.0 |


#### DeviceMotionEventAcceleration

| Prop  | Type   | Description                                  | Since |
| ----- | ------ | -------------------------------------------- | ----- |
| **x** | number | The amount of acceleration along the X axis. | 1.0.0 |
| **y** | number | The amount of acceleration along the Y axis. | 1.0.0 |
| **z** | number | The amount of acceleration along the Z axis. | 1.0.0 |


#### DeviceMotionEventRotationRate

| Prop      | Type   | Description                                                      | Since |
| --------- | ------ | ---------------------------------------------------------------- | ----- |
| **alpha** | number | The amount of rotation around the Z axis, in degrees per second. | 1.0.0 |
| **beta**  | number | The amount of rotation around the X axis, in degrees per second. | 1.0.0 |
| **gamma** | number | The amount of rotation around the Y axis, in degrees per second. | 1.0.0 |


<!--DOCGEN_API_END-->