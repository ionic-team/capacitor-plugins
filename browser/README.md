# @capacitor/browser

The Browser API provides the ability to open an in-app browser and subscribe to browser events.

On iOS, this uses `SFSafariViewController` and is compliant with leading OAuth service in-app-browser requirements.

## Install

```bash
npm install @capacitor/browser
npx cap sync
```

## Android

### Variables

This plugin will use the following project variables (defined in your app's `variables.gradle` file):

- `androidxBrowserVersion`: version of `androidx.browser:browser` (default: `1.7.0`)

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
* [`addListener('browserFinished', ...)`](#addlistenerbrowserfinished-)
* [`addListener('browserPageLoaded', ...)`](#addlistenerbrowserpageloaded-)
* [`removeAllListeners()`](#removealllisteners)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### open(...)

```typescript
open(options: OpenOptions) => Promise<void>
```

Open a page with the specified options.

| Param         | Type                                                |
| ------------- | --------------------------------------------------- |
| **`options`** | <code><a href="#openoptions">OpenOptions</a></code> |

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


### addListener('browserFinished', ...)

```typescript
addListener(eventName: 'browserFinished', listenerFunc: () => void) => Promise<PluginListenerHandle>
```

Android & iOS only: Listen for the browser finished event.
It fires when the Browser is closed by the user.

| Param              | Type                           |
| ------------------ | ------------------------------ |
| **`eventName`**    | <code>'browserFinished'</code> |
| **`listenerFunc`** | <code>() =&gt; void</code>     |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt;</code>

**Since:** 1.0.0

--------------------


### addListener('browserPageLoaded', ...)

```typescript
addListener(eventName: 'browserPageLoaded', listenerFunc: () => void) => Promise<PluginListenerHandle>
```

Android & iOS only: Listen for the page loaded event.
It's only fired when the URL passed to open method finish loading.
It is not invoked for any subsequent page loads.

| Param              | Type                             |
| ------------------ | -------------------------------- |
| **`eventName`**    | <code>'browserPageLoaded'</code> |
| **`listenerFunc`** | <code>() =&gt; void</code>       |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt;</code>

**Since:** 1.0.0

--------------------


### removeAllListeners()

```typescript
removeAllListeners() => Promise<void>
```

Remove all native listeners for this plugin.

**Since:** 1.0.0

--------------------


### Interfaces


#### OpenOptions

Represents the options passed to `open`.

| Prop                    | Type                                   | Description                                                                                                                                | Since |
| ----------------------- | -------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------ | ----- |
| **`url`**               | <code>string</code>                    | The URL to which the browser is opened.                                                                                                    | 1.0.0 |
| **`windowName`**        | <code>string</code>                    | Web only: Optional target for browser open. Follows the `target` property for window.open. Defaults to _blank. Ignored on other platforms. | 1.0.0 |
| **`toolbarColor`**      | <code>string</code>                    | A hex color to which the toolbar color is set.                                                                                             | 1.0.0 |
| **`presentationStyle`** | <code>'fullscreen' \| 'popover'</code> | iOS only: The presentation style of the browser. Defaults to fullscreen. Ignored on other platforms.                                       | 1.0.0 |
| **`width`**             | <code>number</code>                    | iOS only: The width the browser when using presentationStyle 'popover' on iPads. Ignored on other platforms.                               | 4.0.0 |
| **`height`**            | <code>number</code>                    | iOS only: The height the browser when using presentationStyle 'popover' on iPads. Ignored on other platforms.                              | 4.0.0 |


#### PluginListenerHandle

| Prop         | Type                                      |
| ------------ | ----------------------------------------- |
| **`remove`** | <code>() =&gt; Promise&lt;void&gt;</code> |

</docgen-api>
