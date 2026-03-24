# @capacitor/screen-reader

The Screen Reader API provides access to TalkBack/VoiceOver/etc. and provides simple text-to-speech capabilities for visual accessibility.

## Install

```bash
npm install @capacitor/screen-reader
npx cap sync
```

## Example

```typescript
import { ScreenReader } from '@capacitor/screen-reader';

ScreenReader.addListener('stateChange', ({ value }) => {
  console.log(`Screen reader is now ${value ? 'on' : 'off'}`);
});

const checkScreenReaderEnabled = async () => {
  const { value } = await ScreenReader.isEnabled();

  console.log('Voice over enabled? ' + value);
};

const sayHello = async () => {
  await ScreenReader.speak({ value: 'Hello World!' });
};
```

## API

<docgen-index>

* [`isEnabled()`](#isenabled)
* [`speak(...)`](#speak)
* [`addListener('stateChange', ...)`](#addlistenerstatechange-)
* [`removeAllListeners()`](#removealllisteners)
* [Interfaces](#interfaces)
* [Type Aliases](#type-aliases)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### isEnabled()

```typescript
isEnabled() => Promise<{ value: boolean; }>
```

Whether a Screen Reader is currently active.

This method is not supported on web (it is not possible to detect Screen
Readers).

**Returns:** <code>Promise&lt;{ value: boolean; }&gt;</code>

**Since:** 1.0.0

--------------------


### speak(...)

```typescript
speak(options: SpeakOptions) => Promise<void>
```

Text-to-Speech functionality.

This function will only work if a Screen Reader is currently active.

On web, browsers must support the [SpeechSynthesis
API](https://developer.mozilla.org/en-US/docs/Web/API/SpeechSynthesis), or
this method will throw an error.

For more text-to-speech capabilities, please see the [Capacitor Community
Text-to-Speech
plugin](https://github.com/capacitor-community/text-to-speech).

| Param         | Type                                                  |
| ------------- | ----------------------------------------------------- |
| **`options`** | <code><a href="#speakoptions">SpeakOptions</a></code> |

**Since:** 1.0.0

--------------------


### addListener('stateChange', ...)

```typescript
addListener(eventName: 'stateChange', listener: StateChangeListener) => Promise<PluginListenerHandle>
```

Add a listener for when the screen reader is turned on or off.

This event used to be named `'accessibilityScreenReaderStateChange'`.

This method is not supported on web (it is not possible to detect Screen
Readers).

| Param           | Type                                                                |
| --------------- | ------------------------------------------------------------------- |
| **`eventName`** | <code>'stateChange'</code>                                          |
| **`listener`**  | <code><a href="#statechangelistener">StateChangeListener</a></code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt;</code>

**Since:** 1.0.0

--------------------


### removeAllListeners()

```typescript
removeAllListeners() => Promise<void>
```

Remove all the listeners that are attached to this plugin.

**Since:** 1.0.0

--------------------


### Interfaces


#### SpeakOptions

| Prop           | Type                | Description                                                                                                                                                               | Since |
| -------------- | ------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ----- |
| **`value`**    | <code>string</code> | The text to speak.                                                                                                                                                        | 1.0.0 |
| **`language`** | <code>string</code> | The language to speak the text in, as its [ISO 639-1 Code](https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes) (e.g.: "en"). This option is only supported on Android. | 1.0.0 |


#### PluginListenerHandle

| Prop         | Type                                      |
| ------------ | ----------------------------------------- |
| **`remove`** | <code>() =&gt; Promise&lt;void&gt;</code> |


#### ScreenReaderState

| Prop        | Type                 | Description                                  | Since |
| ----------- | -------------------- | -------------------------------------------- | ----- |
| **`value`** | <code>boolean</code> | Whether a Screen Reader is currently active. | 1.0.0 |


### Type Aliases


#### StateChangeListener

<code>(state: <a href="#screenreaderstate">ScreenReaderState</a>): void</code>

</docgen-api>
