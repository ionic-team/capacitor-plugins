# @capacitor/toast

The Toast API provides a notification pop up for displaying important information to a user. Just like real toast!

<!--DOCGEN_INDEX_START-->
<div class="docgen docgen-index">

* [`show(...)`](#show)
* [Interfaces](#interfaces)

</div>
<!--DOCGEN_INDEX_END-->

<!--DOCGEN_API_START-->
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->
<div class="docgen docgen-api">

## API

### show(...)

```typescript
show(options: ToastShowOptions) => Promise<void>
```

Shows a Toast on the screen

| Param         | Type                                                          |
| ------------- | ------------------------------------------------------------- |
| **`options`** | <code><a href="#toastshowoptions">ToastShowOptions</a></code> |

**Returns:** <code>Promise&lt;void&gt;</code>

**Since:** 1.0.0

--------------------


### Interfaces


#### ToastShowOptions

| Prop           | Type                                       | Description                                                       | Default               | Since |
| -------------- | ------------------------------------------ | ----------------------------------------------------------------- | --------------------- | ----- |
| **`text`**     | <code>string</code>                        | Text to display on the Toast                                      |                       | 1.0.0 |
| **`duration`** | <code>"short" \| "long"</code>             | Duration of the Toast, either 'short' (2000ms) or 'long' (3500ms) | <code>'short'</code>  | 1.0.0 |
| **`position`** | <code>"top" \| "center" \| "bottom"</code> | Postion of the Toast                                              | <code>'bottom'</code> | 1.0.0 |

</div>
<!--DOCGEN_API_END-->