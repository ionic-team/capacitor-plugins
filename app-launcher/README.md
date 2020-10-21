# @capacitor/app-launcher

The AppLauncher API allows to open other apps

<!--DOCGEN_INDEX_START-->
<div class="docgen docgen-index">

* [`canOpenUrl(...)`](#canopenurl)
* [`openUrl(...)`](#openurl)

</div>
<!--DOCGEN_INDEX_END-->

<!--DOCGEN_API_START-->
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->
<div class="docgen docgen-api">

## API

### canOpenUrl(...)

```typescript
canOpenUrl(options: { url: string; }) => Promise<{ value: boolean; }>
```

Check if an app can be opened with the given URL

| Param         | Type                          |
| ------------- | ----------------------------- |
| **`options`** | <code>{ url: string; }</code> |

**Returns:** <code>Promise&lt;{ value: boolean; }&gt;</code>

--------------------


### openUrl(...)

```typescript
openUrl(options: { url: string; }) => Promise<{ completed: boolean; }>
```

Open an app with the given URL

| Param         | Type                          |
| ------------- | ----------------------------- |
| **`options`** | <code>{ url: string; }</code> |

**Returns:** <code>Promise&lt;{ completed: boolean; }&gt;</code>

--------------------

</div>
<!--DOCGEN_API_END-->