# @capacitor/toast

The Toast API provides a notification pop up for displaying important information to a user. Just like real toast!

<!--DOCGEN_INDEX_START-->
* [show()](#show)
* [Interfaces](#interfaces)
<!--DOCGEN_INDEX_END-->

<!--DOCGEN_API_START-->
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->
## API

### show

```typescript
show(options: ToastShowOptions) => Promise<void>
```

Shows a Toast on the screen

| Param       | Type                                  |
| ----------- | ------------------------------------- |
| **options** | [ToastShowOptions](#toastshowoptions) |

**Returns:** Promise&lt;void&gt;

**Since:** 1.0.0

--------------------


### Interfaces


#### ToastShowOptions

| Prop         | Type                          | Description                                                       | Default  | Since |
| ------------ | ----------------------------- | ----------------------------------------------------------------- | -------- | ----- |
| **text**     | string                        | Text to display on the Toast                                      |          | 1.0.0 |
| **duration** | "short" \| "long"             | Duration of the Toast, either 'short' (2000ms) or 'long' (3500ms) | 'short'  | 1.0.0 |
| **position** | "top" \| "center" \| "bottom" | Postion of the Toast                                              | 'bottom' | 1.0.0 |


<!--DOCGEN_API_END-->