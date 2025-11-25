# @capacitor/clipboard

The Clipboard API enables copy and pasting to/from the system clipboard.

## Install

```bash
npm install @capacitor/clipboard@latest-7
npx cap sync
```

## Example

```typescript
import { Clipboard } from '@capacitor/clipboard';

const writeToClipboard = async () => {
  await Clipboard.write({
    string: 'Hello World!',
  });
};

const checkClipboard = async () => {
  const { type, value } = await Clipboard.read();

  console.log(`Got ${type} from clipboard: ${value}`);
};
```

## API

<docgen-index>

- [`write(...)`](#write)
- [`read()`](#read)
- [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### write(...)

```typescript
write(options: WriteOptions) => Promise<void>
```

Write a value to the clipboard (the "copy" action)

| Param         | Type                                                  |
| ------------- | ----------------------------------------------------- |
| **`options`** | <code><a href="#writeoptions">WriteOptions</a></code> |

**Since:** 1.0.0

---

### read()

```typescript
read() => Promise<ReadResult>
```

Read a value from the clipboard (the "paste" action)

**Returns:** <code>Promise&lt;<a href="#readresult">ReadResult</a>&gt;</code>

**Since:** 1.0.0

---

### Interfaces

#### WriteOptions

Represents the data to be written to the clipboard.

| Prop         | Type                | Description                                                                                                     | Since |
| ------------ | ------------------- | --------------------------------------------------------------------------------------------------------------- | ----- |
| **`string`** | <code>string</code> | Text value to copy.                                                                                             | 1.0.0 |
| **`image`**  | <code>string</code> | Image in [Data URL](https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/Data_URIs) format to copy. | 1.0.0 |
| **`url`**    | <code>string</code> | URL string to copy.                                                                                             | 1.0.0 |
| **`label`**  | <code>string</code> | User visible label to accompany the copied data (Android Only).                                                 | 1.0.0 |

#### ReadResult

Represents the data read from the clipboard.

| Prop        | Type                | Description                    | Since |
| ----------- | ------------------- | ------------------------------ | ----- |
| **`value`** | <code>string</code> | Data read from the clipboard.  | 1.0.0 |
| **`type`**  | <code>string</code> | Type of data in the clipboard. | 1.0.0 |

</docgen-api>
