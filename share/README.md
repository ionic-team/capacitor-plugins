# @capacitor/share

The Share API provides methods for sharing content in any sharing-enabled apps the user may have installed.

<!--DOCGEN_INDEX_START-->
* [share()](#share)
* [Interfaces](#interfaces)
<!--DOCGEN_INDEX_END-->

<!--DOCGEN_API_START-->
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->
## API

### share

```typescript
share(options: ShareOptions) => Promise<ShareResult>
```

Show a Share modal for sharing content with other apps

| Param       | Type                          |
| ----------- | ----------------------------- |
| **options** | [ShareOptions](#shareoptions) |

**Returns:** Promise&lt;[ShareResult](#shareresult)&gt;

**Since:** 1.0.0

--------------------


### Interfaces


#### ShareResult

| Prop             | Type   | Description                                                                                                              | Since |
| ---------------- | ------ | ------------------------------------------------------------------------------------------------------------------------ | ----- |
| **activityType** | string | Identifier of the app that received the share action. Can be an empty string in some cases. On web it will be undefined. | 1.0.0 |


#### ShareOptions

| Prop            | Type   | Description                                                                | Since |
| --------------- | ------ | -------------------------------------------------------------------------- | ----- |
| **title**       | string | Set a title for any message. This will be the subject if sharing to email  | 1.0.0 |
| **text**        | string | Set some text to share                                                     | 1.0.0 |
| **url**         | string | Set a URL to share, can be http, https or file:// URL                      | 1.0.0 |
| **dialogTitle** | string | Set a title for the share modal. This option is only supported on Android. | 1.0.0 |


<!--DOCGEN_API_END-->