# @capacitor/Dialog

The Dialog API provides methods for triggering native dialog windows for alerts, confirmations, and input prompts

<!--DOCGEN_INDEX_START-->
* [alert()](#alert)
* [prompt()](#prompt)
* [confirm()](#confirm)
* [Interfaces](#interfaces)
<!--DOCGEN_INDEX_END-->

<!--DOCGEN_API_START-->
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->
## API

### alert

```typescript
alert(options: AlertOptions) => Promise<void>
```

Show an alert dialog

| Param       | Type                          |
| ----------- | ----------------------------- |
| **options** | [AlertOptions](#alertoptions) |

**Returns:** Promise&lt;void&gt;

**Since:** 1.0.0

--------------------


### prompt

```typescript
prompt(options: PromptOptions) => Promise<PromptResult>
```

Show a prompt dialog

| Param       | Type                            |
| ----------- | ------------------------------- |
| **options** | [PromptOptions](#promptoptions) |

**Returns:** Promise&lt;[PromptResult](#promptresult)&gt;

**Since:** 1.0.0

--------------------


### confirm

```typescript
confirm(options: ConfirmOptions) => Promise<ConfirmResult>
```

Show a confirmation dialog

| Param       | Type                              |
| ----------- | --------------------------------- |
| **options** | [ConfirmOptions](#confirmoptions) |

**Returns:** Promise&lt;[ConfirmResult](#confirmresult)&gt;

**Since:** 1.0.0

--------------------


### Interfaces


#### AlertOptions

| Prop            | Type   | Description                       | Default | Since |
| --------------- | ------ | --------------------------------- | ------- | ----- |
| **title**       | string | Title of the dialog.              |         | 1.0.0 |
| **message**     | string | Message to show on the dialog.    |         | 1.0.0 |
| **buttonTitle** | string | Text to use on the action button. | "OK"    | 1.0.0 |


#### PromptResult

| Prop          | Type    | Description                                     | Since |
| ------------- | ------- | ----------------------------------------------- | ----- |
| **value**     | string  | Text entered on the prompt.                     | 1.0.0 |
| **cancelled** | boolean | Whether if the prompt was canceled or accepted. | 1.0.0 |


#### PromptOptions

| Prop                  | Type   | Description                                | Default  | Since |
| --------------------- | ------ | ------------------------------------------ | -------- | ----- |
| **title**             | string | Title of the dialog.                       |          | 1.0.0 |
| **message**           | string | Message to show on the dialog.             |          | 1.0.0 |
| **okButtonTitle**     | string | Text to use on the positive action button. | "OK"     | 1.0.0 |
| **cancelButtonTitle** | string | Text to use on the negative action button. | "Cancel" | 1.0.0 |
| **inputPlaceholder**  | string | Placeholder text for hints.                |          | 1.0.0 |
| **inputText**         | string | Prepopulated text.                         |          | 1.0.0 |


#### ConfirmResult

| Prop      | Type    | Description                                               | Since |
| --------- | ------- | --------------------------------------------------------- | ----- |
| **value** | boolean | true if the positive button was clicked, false otherwise. | 1.0.0 |


#### ConfirmOptions

| Prop                  | Type   | Description                                | Default  | Since |
| --------------------- | ------ | ------------------------------------------ | -------- | ----- |
| **title**             | string | Title of the dialog.                       |          | 1.0.0 |
| **message**           | string | Message to show on the dialog.             |          | 1.0.0 |
| **okButtonTitle**     | string | Text to use on the positive action button. | "OK"     | 1.0.0 |
| **cancelButtonTitle** | string | Text to use on the negative action button. | "Cancel" | 1.0.0 |


<!--DOCGEN_API_END-->