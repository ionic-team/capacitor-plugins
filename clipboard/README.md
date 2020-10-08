# @capacitor/clipboard

The Clipboard API enables copy and pasting to/from the system clipboard.

<!--DOCGEN_INDEX_START-->
* [write()](#write)
* [read()](#read)
* [Interfaces](#interfaces)
<!--DOCGEN_INDEX_END-->

<!--DOCGEN_API_START-->
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->
## API

### write

```typescript
write(options: ClipboardWriteOptions) => Promise<void>
```

Write a value to the clipboard (the "copy" action)

| Param       | Type                                            |
| ----------- | ----------------------------------------------- |
| **options** | [ClipboardWriteOptions](#clipboardwriteoptions) |

**Returns:** Promise&lt;void&gt;

**Since:** 1.0.0

--------------------


### read

```typescript
read() => Promise<ClipboardReadResult>
```

Read a value from the clipboard (the "paste" action)

**Returns:** Promise&lt;[ClipboardReadResult](#clipboardreadresult)&gt;

**Since:** 1.0.0

--------------------


### Interfaces


#### ClipboardWriteOptions

Represents the data to be written to the clipboard.

| Prop       | Type   | Description                                                                                                     | Since |
| ---------- | ------ | --------------------------------------------------------------------------------------------------------------- | ----- |
| **string** | string | Text value to copy.                                                                                             | 1.0.0 |
| **image**  | string | Image in [Data URL](https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/Data_URIs) format to copy. | 1.0.0 |
| **url**    | string | URL string to copy.                                                                                             | 1.0.0 |
| **label**  | string | User visible label to accompany the copied data (Android Only).                                                 | 1.0.0 |


#### ClipboardReadResult

Represents the data read from the clipboard.

| Prop      | Type   | Description                    | Since |
| --------- | ------ | ------------------------------ | ----- |
| **value** | string | Data read from the clipboard.  | 1.0.0 |
| **type**  | string | Type of data in the clipboard. | 1.0.0 |


<!--DOCGEN_API_END-->