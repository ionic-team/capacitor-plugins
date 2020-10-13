# @capacitor/network

The Network API provides network and connectivity information.

<!--DOCGEN_INDEX_START-->
* [`getStatus()`](#getstatus)
* [`addListener(...)`](#addlistener)
* [`removeAllListeners()`](#removealllisteners)
* [Interfaces](#interfaces)
<!--DOCGEN_INDEX_END-->

<!--DOCGEN_API_START-->
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->
## API

### getStatus()

```typescript
getStatus() => Promise<NetworkStatus>
```

Query the current status of the network connection.

**Returns:** <code>Promise&lt;<a href="#networkstatus">NetworkStatus</a>&gt;</code>

**Since:** 1.0.0

--------------------


### addListener(...)

```typescript
addListener(eventName: 'networkStatusChange', listenerFunc: (status: NetworkStatus) => void) => PluginListenerHandle
```

Listen for changes in the network connection.

| Param            | Type                                         |
| ---------------- | -------------------------------------------- |
| **eventName**    | <code>"networkStatusChange"</code>           |
| **listenerFunc** | <code>(status: NetworkStatus) => void</code> |

**Returns:** <code><a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

**Since:** 1.0.0

--------------------


### removeAllListeners()

```typescript
removeAllListeners() => void
```

Remove all listeners (including the network status changes) for this plugin.

**Returns:** <code>void</code>

**Since:** 1.0.0

--------------------


### Interfaces


#### NetworkStatus

Represents the state and type of the network connection.

| Prop                 | Type                                                     | Description                                                                                                                   | Since |
| -------------------- | -------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------- | ----- |
| **`connected`**      | <code>boolean</code>                                     | Whether there is an active connection or not.                                                                                 | 1.0.0 |
| **`connectionType`** | <code>"wifi" \| "cellular" \| "none" \| "unknown"</code> | The type of network connection currently in use. If there is no active network connection, `connectionType` will be `'none'`. | 1.0.0 |


#### PluginListenerHandle

| Prop         | Type                    |
| ------------ | ----------------------- |
| **`remove`** | <code>() => void</code> |


<!--DOCGEN_API_END-->