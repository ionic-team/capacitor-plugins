# @capacitor/motion

The Motion API tracks accelerometer and device orientation (compass heading, etc.)

## Install

```bash
npm install @capacitor/motion
npx cap sync
```

## API

<docgen-index>

* [`addListener('accel', ...)`](#addlisteneraccel-)
* [`addListener('orientation', ...)`](#addlistenerorientation-)
* [`removeAllListeners()`](#removealllisteners)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### addListener('accel', ...)

```typescript
addListener(eventName: 'accel', listenerFunc: AccelListener) => PluginListenerHandle
```

Add a listener for accelerometer data

| Param              | Type                                                |
| ------------------ | --------------------------------------------------- |
| **`eventName`**    | <code>"accel"</code>                                |
| **`listenerFunc`** | <code>(event: AccelListenerEvent) =&gt; void</code> |

**Returns:** <code><a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

**Since:** 1.0.0

--------------------


### addListener('orientation', ...)

```typescript
addListener(eventName: 'orientation', listenerFunc: OrientationListener) => PluginListenerHandle
```

Add a listener for device orientation change (compass heading, etc.)

| Param              | Type                                          |
| ------------------ | --------------------------------------------- |
| **`eventName`**    | <code>"orientation"</code>                    |
| **`listenerFunc`** | <code>(event: RotationRate) =&gt; void</code> |

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

</docgen-api>
