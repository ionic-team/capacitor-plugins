# @capacitor/app-launcher

The AppLauncher API allows to open other apps

## Install

```bash
npm install @capacitor/app-launcher
npx cap sync
```

## API

<docgen-index>

* [`canOpenUrl(...)`](#canopenurl)
* [`openUrl(...)`](#openurl)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### canOpenUrl(...)

```typescript
canOpenUrl(options: { url: string; }) => Promise<{ value: boolean; }>
```

Check if an app can be opened with the given URL.

On iOS  you must declare the URL schemes you pass to this method by adding
the LSApplicationQueriesSchemes key to your app's Info.plist file.
This method always returns false for undeclared schemes, whether or not an appropriate
app is installed. To learn more about the key, see
[LSApplicationQueriesSchemes](https://developer.apple.com/library/archive/documentation/General/Reference/InfoPlistKeyReference/Articles/LaunchServicesKeys.html#//apple_ref/doc/plist/info/LSApplicationQueriesSchemes).

| Param         | Type                          |
| ------------- | ----------------------------- |
| **`options`** | <code>{ url: string; }</code> |

**Returns:** <code>Promise&lt;{ value: boolean; }&gt;</code>

**Since:** 1.0.0

--------------------


### openUrl(...)

```typescript
openUrl(options: { url: string; }) => Promise<{ completed: boolean; }>
```

Open an app with the given URL.

| Param         | Type                          |
| ------------- | ----------------------------- |
| **`options`** | <code>{ url: string; }</code> |

**Returns:** <code>Promise&lt;{ completed: boolean; }&gt;</code>

**Since:** 1.0.0

--------------------

</docgen-api>
