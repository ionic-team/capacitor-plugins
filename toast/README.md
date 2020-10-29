# @capacitor/toast

The Toast API provides a notification pop up for displaying important information to a user. Just like real toast!

## Install

```bash
npm install @capacitor/toast
npx cap sync
```

## API

<docgen-index>

* [`show(...)`](#show)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### show(...)

```typescript
show(options: ToastShowOptions) => Promise<void>
```

Shows a Toast on the screen

| Param         | Type                                                          |
| ------------- | ------------------------------------------------------------- |
| **`options`** | <code><a href="#toastshowoptions">ToastShowOptions</a></code> |

**Since:** 1.0.0

--------------------


### Interfaces


#### ToastShowOptions

| Prop           | Type                                       | Description                                                       | Default               | Since |
| -------------- | ------------------------------------------ | ----------------------------------------------------------------- | --------------------- | ----- |
| **`text`**     | <code>string</code>                        | Text to display on the Toast                                      |                       | 1.0.0 |
| **`duration`** | <code>"short" \| "long"</code>             | Duration of the Toast, either 'short' (2000ms) or 'long' (3500ms) | <code>'short'</code>  | 1.0.0 |
| **`position`** | <code>"top" \| "center" \| "bottom"</code> | Postion of the Toast                                              | <code>'bottom'</code> | 1.0.0 |

</docgen-api>
