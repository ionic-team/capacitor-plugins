# @capacitor/action-sheet

The Action Sheet API provides access to native Action Sheets, which come up from the bottom of the screen and display actions a user can take.

## Install

```bash
npm install @capacitor/action-sheet
npx cap sync
```

### Variables

This plugin will use the following project variables (defined in your app's `variables.gradle` file):

- `androidxMaterialVersion`: version of `com.google.android.material:material` (default: `1.10.0`)

## PWA Notes

[PWA Elements](https://capacitorjs.com/docs/web/pwa-elements) are required for Action Sheet plugin to work.

## Example

```typescript
import { ActionSheet, ActionSheetButtonStyle } from '@capacitor/action-sheet';

const showActions = async () => {
  const result = await ActionSheet.showActions({
    title: 'Photo Options',
    message: 'Select an option to perform',
    options: [
      {
        title: 'Upload',
      },
      {
        title: 'Share',
      },
      {
        title: 'Remove',
        style: ActionSheetButtonStyle.Destructive,
      },
    ],
  });

  console.log('Action Sheet result:', result);
};
```

## API

<docgen-index>

* [`showActions(...)`](#showactions)
* [Interfaces](#interfaces)
* [Enums](#enums)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### showActions(...)

```typescript
showActions(options: ShowActionsOptions) => Promise<ShowActionsResult>
```

Show an Action Sheet style modal with various options for the user
to select.

| Param         | Type                                                              |
| ------------- | ----------------------------------------------------------------- |
| **`options`** | <code><a href="#showactionsoptions">ShowActionsOptions</a></code> |

**Returns:** <code>Promise&lt;<a href="#showactionsresult">ShowActionsResult</a>&gt;</code>

**Since:** 1.0.0

--------------------


### Interfaces


#### ShowActionsResult

| Prop        | Type                | Description                                  | Since |
| ----------- | ------------------- | -------------------------------------------- | ----- |
| **`index`** | <code>number</code> | The index of the clicked option (Zero-based) | 1.0.0 |


#### ShowActionsOptions

| Prop          | Type                             | Description                                                              | Since |
| ------------- | -------------------------------- | ------------------------------------------------------------------------ | ----- |
| **`title`**   | <code>string</code>              | The title of the Action Sheet.                                           | 1.0.0 |
| **`message`** | <code>string</code>              | A message to show under the title. This option is only supported on iOS. | 1.0.0 |
| **`options`** | <code>ActionSheetButton[]</code> | Options the user can choose from.                                        | 1.0.0 |


#### ActionSheetButton

| Prop        | Type                                                                      | Description                                                                           | Since |
| ----------- | ------------------------------------------------------------------------- | ------------------------------------------------------------------------------------- | ----- |
| **`title`** | <code>string</code>                                                       | The title of the option                                                               | 1.0.0 |
| **`style`** | <code><a href="#actionsheetbuttonstyle">ActionSheetButtonStyle</a></code> | The style of the option This option is only supported on iOS.                         | 1.0.0 |
| **`icon`**  | <code>string</code>                                                       | Icon for the option (ionicon naming convention) This option is only supported on Web. | 1.0.0 |


### Enums


#### ActionSheetButtonStyle

| Members           | Value                      | Description                                                                                                 | Since |
| ----------------- | -------------------------- | ----------------------------------------------------------------------------------------------------------- | ----- |
| **`Default`**     | <code>'DEFAULT'</code>     | Default style of the option.                                                                                | 1.0.0 |
| **`Destructive`** | <code>'DESTRUCTIVE'</code> | Style to use on destructive options.                                                                        | 1.0.0 |
| **`Cancel`**      | <code>'CANCEL'</code>      | Style to use on the option that cancels the Action Sheet. If used, should be on the latest availabe option. | 1.0.0 |

</docgen-api>
