# @capacitor/action-sheet

The Action Sheet API provides access to native Action Sheets, which come up from the bottom of the screen and display actions a user can take.

<!--DOCGEN_INDEX_START-->
* [showActions()](#showactions)
* [Interfaces](#interfaces)
* [Enums](#enums)
<!--DOCGEN_INDEX_END-->

<!--DOCGEN_API_START-->
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->
## API

### showActions

```typescript
showActions(options: ActionSheetOptions) => Promise<ActionSheetResult>
```

Show an Action Sheet style modal with various options for the user
to select.

| Param       | Type                                      |
| ----------- | ----------------------------------------- |
| **options** | [ActionSheetOptions](#actionsheetoptions) |

**Returns:** Promise&lt;[ActionSheetResult](#actionsheetresult)&gt;

**Since:** 1.0.0

--------------------


### Interfaces


#### ActionSheetResult

| Prop      | Type   | Description                                  | Since |
| --------- | ------ | -------------------------------------------- | ----- |
| **index** | number | The index of the clicked option (Zero-based) | 1.0.0 |


#### ActionSheetOptions

| Prop        | Type                | Description                                                              | Since |
| ----------- | ------------------- | ------------------------------------------------------------------------ | ----- |
| **title**   | string              | The title of the Action Sheet.                                           | 1.0.0 |
| **message** | string              | A message to show under the title. This option is only supported on iOS. | 1.0.0 |
| **options** | ActionSheetOption[] | Options the user can choose from.                                        | 1.0.0 |


#### ActionSheetOption

| Prop      | Type                                              | Description                                                                           | Since |
| --------- | ------------------------------------------------- | ------------------------------------------------------------------------------------- | ----- |
| **title** | string                                            | The title of the option                                                               | 1.0.0 |
| **style** | [ActionSheetOptionStyle](#actionsheetoptionstyle) | The style of the option This option is only supported on iOS.                         | 1.0.0 |
| **icon**  | string                                            | Icon for the option (ionicon naming convention) This option is only supported on Web. | 1.0.0 |


### Enums


#### ActionSheetOptionStyle

| Members         | Value         | Description                                                                                                 | Since |
| --------------- | ------------- | ----------------------------------------------------------------------------------------------------------- | ----- |
| **Default**     | 'DEFAULT'     | Default style of the option.                                                                                | 1.0.0 |
| **Destructive** | 'DESTRUCTIVE' | Style to use on destructive options.                                                                        | 1.0.0 |
| **Cancel**      | 'CANCEL'      | Style to use on the option that cancels the Action Sheet. If used, should be on the latest availabe option. | 1.0.0 |


<!--DOCGEN_API_END-->