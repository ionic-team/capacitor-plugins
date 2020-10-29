# @capacitor/network

The Network API provides network and connectivity information.

## Install

```bash
npm install @capacitor/network
npx cap sync
```

## API

<docgen-index>

* [`getStatus()`](#getstatus)
* [`addListener(...)`](#addlistener)
* [`removeAllListeners()`](#removealllisteners)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

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

| Param              | Type                                                                         |
| ------------------ | ---------------------------------------------------------------------------- |
| **`eventName`**    | <code>"networkStatusChange"</code>                                           |
| **`listenerFunc`** | <code>(status: <a href="#networkstatus">NetworkStatus</a>) =&gt; void</code> |

**Returns:** <code><a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

**Since:** 1.0.0

--------------------


### removeAllListeners()

```typescript
removeAllListeners() => void
```

Remove all listeners (including the network status changes) for this plugin.

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

| Prop         | Type                       |
| ------------ | -------------------------- |
| **`remove`** | <code>() =&gt; void</code> |

</docgen-api>
