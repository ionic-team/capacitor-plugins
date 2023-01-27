# @capacitor/screen-orientation

The Screen Orientation API provides information and functionality related to the orientation of the screen.

## Install

```bash
npm install @capacitor/screen-orientation
npx cap sync
```

## iOS

iOS requires slight adjustments to your app's `AppDelegate.swift` file.

Import the plugin in `AppDelegate.swift`:

```swift
import CapacitorScreenOrientation
```

Once imported, add the following method to the `AppDelegate` class:

```swift
func application(_ application: UIApplication, supportedInterfaceOrientationsFor window: UIWindow?) -> UIInterfaceOrientationMask {
  return ScreenOrientationPlugin.supportedOrientations
}
```

## API

<docgen-index>

* [`orientation()`](#orientation)
* [`lock(...)`](#lock)
* [`unlock()`](#unlock)
* [`addListener('screenOrientationChange', ...)`](#addlistenerscreenorientationchange)
* [`removeAllListeners()`](#removealllisteners)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### orientation()

```typescript
orientation() => Promise<{ type: OrientationType; }>
```

Returns the current screen orientation.

**Returns:** <code>Promise&lt;{ type: any; }&gt;</code>

**Since:** 4.0.0

--------------------


### lock(...)

```typescript
lock(opts: { orientation: OrientationLockType; }) => Promise<void>
```

Locks the screen orientation.

| Param      | Type                               |
| ---------- | ---------------------------------- |
| **`opts`** | <code>{ orientation: any; }</code> |

**Since:** 4.0.0

--------------------


### unlock()

```typescript
unlock() => Promise<void>
```

Unlocks the screen's orientation.

**Since:** 4.0.0

--------------------


### addListener('screenOrientationChange', ...)

```typescript
addListener(eventName: 'screenOrientationChange', listenerFunc: (orientation: { type: OrientationType; }) => void) => Promise<PluginListenerHandle> & PluginListenerHandle
```

Listens for screen orientation changes.

| Param              | Type                                                  |
| ------------------ | ----------------------------------------------------- |
| **`eventName`**    | <code>'screenOrientationChange'</code>                |
| **`listenerFunc`** | <code>(orientation: { type: any; }) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt; & <a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

**Since:** 4.0.0

--------------------


### removeAllListeners()

```typescript
removeAllListeners() => Promise<void>
```

Removes all listeners.

**Since:** 4.0.0

--------------------


### Interfaces


#### PluginListenerHandle

| Prop         | Type                                      |
| ------------ | ----------------------------------------- |
| **`remove`** | <code>() =&gt; Promise&lt;void&gt;</code> |

</docgen-api>
