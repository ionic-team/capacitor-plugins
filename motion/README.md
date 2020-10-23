# @capacitor/motion

The Motion API tracks accelerometer and device orientation (compass heading, etc.)

## API

<docgen-index>

* [`addListener(...)`](#addlistener)
* [`addListener(...)`](#addlistener)
* [`removeAllListeners()`](#removealllisteners)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### addListener(...)

```typescript
addListener(eventName: 'accel', listenerFunc: (event: MotionEventResult) => void) => PluginListenerHandle
```

Add a listener for accelerometer data

| Param              | Type                                                                                |
| ------------------ | ----------------------------------------------------------------------------------- |
| **`eventName`**    | <code>"accel"</code>                                                                |
| **`listenerFunc`** | <code>(event: <a href="#motioneventresult">MotionEventResult</a>) =&gt; void</code> |

**Returns:** <code><a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

**Since:** 1.0.0

--------------------


### addListener(...)

```typescript
addListener(eventName: 'orientation', listenerFunc: (event: MotionOrientationEventResult) => void) => PluginListenerHandle
```

Add a listener for device orientation change (compass heading, etc.)

| Param              | Type                                                                                                        |
| ------------------ | ----------------------------------------------------------------------------------------------------------- |
| **`eventName`**    | <code>"orientation"</code>                                                                                  |
| **`listenerFunc`** | <code>(event: <a href="#devicemotioneventrotationrate">DeviceMotionEventRotationRate</a>) =&gt; void</code> |

**Returns:** <code><a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

**Since:** 1.0.0

--------------------


### removeAllListeners()

```typescript
removeAllListeners() => void
```

Remove all the listeners that are attached to this plugin.

**Since:** 1.0.0

--------------------


### Interfaces


#### PluginListenerHandle

| Prop         | Type                       |
| ------------ | -------------------------- |
| **`remove`** | <code>() =&gt; void</code> |


#### MotionEventResult

| Prop                               | Type                                                                                    | Description                                                                                                                                                             | Since |
| ---------------------------------- | --------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ----- |
| **`acceleration`**                 | <code><a href="#devicemotioneventacceleration">DeviceMotionEventAcceleration</a></code> | An object giving the acceleration of the device on the three axis X, Y and Z. Acceleration is expressed in m/s                                                          | 1.0.0 |
| **`accelerationIncludingGravity`** | <code><a href="#devicemotioneventacceleration">DeviceMotionEventAcceleration</a></code> | An object giving the acceleration of the device on the three axis X, Y and Z with the effect of gravity. Acceleration is expressed in m/s                               | 1.0.0 |
| **`rotationRate`**                 | <code><a href="#devicemotioneventrotationrate">DeviceMotionEventRotationRate</a></code> | An object giving the rate of change of the device's orientation on the three orientation axis alpha, beta and gamma. Rotation rate is expressed in degrees per seconds. | 1.0.0 |
| **`interval`**                     | <code>number</code>                                                                     | A number representing the interval of time, in milliseconds, at which data is obtained from the device.                                                                 | 1.0.0 |


#### DeviceMotionEventAcceleration

| Prop    | Type                | Description                                  | Since |
| ------- | ------------------- | -------------------------------------------- | ----- |
| **`x`** | <code>number</code> | The amount of acceleration along the X axis. | 1.0.0 |
| **`y`** | <code>number</code> | The amount of acceleration along the Y axis. | 1.0.0 |
| **`z`** | <code>number</code> | The amount of acceleration along the Z axis. | 1.0.0 |


#### DeviceMotionEventRotationRate

| Prop        | Type                | Description                                                      | Since |
| ----------- | ------------------- | ---------------------------------------------------------------- | ----- |
| **`alpha`** | <code>number</code> | The amount of rotation around the Z axis, in degrees per second. | 1.0.0 |
| **`beta`**  | <code>number</code> | The amount of rotation around the X axis, in degrees per second. | 1.0.0 |
| **`gamma`** | <code>number</code> | The amount of rotation around the Y axis, in degrees per second. | 1.0.0 |

</docgen-api>
