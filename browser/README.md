# @capacitor/browser

The Browser API provides the ability to open an in-app browser and subscribe to browser events.

On iOS, this uses `SFSafariViewController` and is compliant with leading OAuth service in-app-browser requirements.

## Install

```bash
npm install @capacitor/browser
npx cap sync
```

## Example

```typescript
import { Browser } from '@capacitor/browser';

const openCapacitorSite = async () => {
  await Browser.open({ url: 'http://capacitorjs.com/' });
};
```

## API

<docgen-index>

* [`open(...)`](#open)
* [`close()`](#close)
* [`addListener(...)`](#addlistener)
* [`addListener(...)`](#addlistener)
* [`removeAllListeners()`](#removealllisteners)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### open(...)

```typescript
open(options: BrowserOpenOptions) => Promise<void>
```

Open a page with the specified options.

| Param         | Type                                                              |
| ------------- | ----------------------------------------------------------------- |
| **`options`** | <code><a href="#browseropenoptions">BrowserOpenOptions</a></code> |

**Since:** 1.0.0

--------------------


### close()

```typescript
close() => Promise<void>
```

Web & iOS only: Close an open browser window.

No-op on other platforms.

**Since:** 1.0.0

--------------------


### addListener(...)

```typescript
addListener(eventName: 'browserFinished', listenerFunc: () => void) => PluginListenerHandle
```

Android & iOS only: Listen for the loading finished event.

| Param              | Type                           |
| ------------------ | ------------------------------ |
| **`eventName`**    | <code>"browserFinished"</code> |
| **`listenerFunc`** | <code>() =&gt; void</code>     |

**Returns:** <code><a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

**Since:** 1.0.0

--------------------


### addListener(...)

```typescript
addListener(eventName: 'browserPageLoaded', listenerFunc: () => void) => PluginListenerHandle
```

Android & iOS only: Listen for the page loaded event.

| Param              | Type                             |
| ------------------ | -------------------------------- |
| **`eventName`**    | <code>"browserPageLoaded"</code> |
| **`listenerFunc`** | <code>() =&gt; void</code>       |

**Returns:** <code><a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

**Since:** 1.0.0

--------------------


### removeAllListeners()

```typescript
removeAllListeners() => void
```

Remove all native listeners for this plugin.

**Since:** 1.0.0

--------------------


### Interfaces


#### BrowserOpenOptions

Represents the options passed to `open`.

| Prop                    | Type                                   | Description                                                                                                                                | Since |
| ----------------------- | -------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------ | ----- |
| **`url`**               | <code>string</code>                    | The URL to which the browser is opened.                                                                                                    | 1.0.0 |
| **`windowName`**        | <code>string</code>                    | Web only: Optional target for browser open. Follows the `target` property for window.open. Defaults to _blank. Ignored on other platforms. | 1.0.0 |
| **`toolbarColor`**      | <code>string</code>                    | A hex color to which the toolbar color is set.                                                                                             | 1.0.0 |
| **`presentationStyle`** | <code>"fullscreen" \| "popover"</code> | iOS only: The presentation style of the browser. Defaults to fullscreen. Ignored on other platforms.                                       | 1.0.0 |


#### PluginListenerHandle

| Prop         | Type                       |
| ------------ | -------------------------- |
| **`remove`** | <code>() =&gt; void</code> |

</docgen-api>
