# @capacitor/screen-reader

The Screen Reader API provides access to TalkBack/VoiceOver/etc. and provides simple text-to-speech capabilities for visual accessibility.

<!--DOCGEN_INDEX_START-->
* [isEnabled()](#isenabled)
* [speak()](#speak)
* [addListener()](#addlistener)
* [removeAllListeners()](#removealllisteners)
* [Interfaces](#interfaces)
<!--DOCGEN_INDEX_END-->

<!--DOCGEN_API_START-->
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->
## API

### isEnabled

```typescript
isEnabled() => Promise<{ value: boolean; }>
```

Whether a Screen Reader is currently active.

This method is not supported on web (it is not possible to detect Screen
Readers).

**Returns:** Promise&lt;{ value: boolean; }&gt;

**Since:** 1.0.0

--------------------


### speak

```typescript
speak(options: ScreenReaderSpeakOptions) => Promise<void>
```

Text-to-Speech functionality.

This function will only work if a Screen Reader is currently active.

On web, browsers must support the [SpeechSynthesis
API](https://developer.mozilla.org/en-US/docs/Web/API/SpeechSynthesis), or
this method will throw an error.

For more text-to-speech capabilities, please see the [Capacitor Community
Text-to-Speech
plugin](https://github.com/capacitor-community/text-to-speech).

| Param       | Type                                                  |
| ----------- | ----------------------------------------------------- |
| **options** | [ScreenReaderSpeakOptions](#screenreaderspeakoptions) |

**Returns:** Promise&lt;void&gt;

**Since:** 1.0.0

--------------------


### addListener

```typescript
addListener(eventName: 'screenReaderStateChange', listener: ScreenReaderStateChangeListener) => PluginListenerHandle
```

Add a listener

This method is not supported on web (it is not possible to detect Screen
Readers).

| Param         | Type                                |
| ------------- | ----------------------------------- |
| **eventName** | "screenReaderStateChange"           |
| **listener**  | (state: { value: boolean; }) => any |

**Returns:** [PluginListenerHandle](#pluginlistenerhandle)

**Since:** 1.0.0

--------------------


### removeAllListeners

```typescript
removeAllListeners() => void
```

Remove all the listeners that are attached to this plugin.

**Returns:** void

**Since:** 1.0.0

--------------------


### Interfaces


#### ScreenReaderSpeakOptions

| Prop         | Type   | Description                                                                                                                                                               | Since |
| ------------ | ------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ----- |
| **value**    | string | The text to speak.                                                                                                                                                        | 1.0.0 |
| **language** | string | The language to speak the text in, as its [ISO 639-1 Code](https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes) (e.g.: "en"). This option is only supported on Android. | 1.0.0 |


#### PluginListenerHandle

| Prop       | Type       |
| ---------- | ---------- |
| **remove** | () => void |


<!--DOCGEN_API_END-->