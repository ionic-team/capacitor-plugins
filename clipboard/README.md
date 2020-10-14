# @capacitor/clipboard

The Clipboard API enables copy and pasting to/from the system clipboard.

<!--DOCGEN_INDEX_START-->
<div class="docgen docgen-index">

* [`write(...)`](#write)
* [`read()`](#read)
* [Interfaces](#interfaces)

</div>
<!--DOCGEN_INDEX_END-->

<!--DOCGEN_API_START-->
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->
<div class="docgen docgen-api">

## API

### write(...)

```typescript
write(options: ClipboardWriteOptions) => Promise<void>
```

Write a value to the clipboard (the "copy" action)

| Param         | Type                                                                    |
| ------------- | ----------------------------------------------------------------------- |
| **`options`** | <code><a href="#clipboardwriteoptions">ClipboardWriteOptions</a></code> |

**Returns:** <code>Promise&lt;void&gt;</code>

**Since:** 1.0.0

--------------------


### read()

```typescript
read() => Promise<ClipboardReadResult>
```

Read a value from the clipboard (the "paste" action)

**Returns:** <code>Promise&lt;<a href="#clipboardreadresult">ClipboardReadResult</a>&gt;</code>

**Since:** 1.0.0

--------------------


### Interfaces


#### ClipboardWriteOptions

Represents the data to be written to the clipboard.

| Prop         | Type                | Description                                                                                                     | Since |
| ------------ | ------------------- | --------------------------------------------------------------------------------------------------------------- | ----- |
| **`string`** | <code>string</code> | Text value to copy.                                                                                             | 1.0.0 |
| **`image`**  | <code>string</code> | Image in [Data URL](https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/Data_URIs) format to copy. | 1.0.0 |
| **`url`**    | <code>string</code> | URL string to copy.                                                                                             | 1.0.0 |
| **`label`**  | <code>string</code> | User visible label to accompany the copied data (Android Only).                                                 | 1.0.0 |


#### ClipboardReadResult

Represents the data read from the clipboard.

| Prop        | Type                | Description                    | Since |
| ----------- | ------------------- | ------------------------------ | ----- |
| **`value`** | <code>string</code> | Data read from the clipboard.  | 1.0.0 |
| **`type`**  | <code>string</code> | Type of data in the clipboard. | 1.0.0 |

</div>
<!--DOCGEN_API_END-->