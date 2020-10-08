# @capacitor/text-zoom

The Text Zoom API provides the ability to change Web View text size for visual accessibility.

<!--DOCGEN_INDEX_START-->
* [get()](#get)
* [getPreferred()](#getpreferred)
* [set()](#set)
* [Interfaces](#interfaces)
<!--DOCGEN_INDEX_END-->

<!--DOCGEN_API_START-->
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->
## API

### get

```typescript
get() => Promise<GetResponse>
```

Get the current zoom level.

Zoom levels are represented as a decimal (e.g. 1.2 is 120%).

**Returns:** Promise&lt;[GetResponse](#getresponse)&gt;

**Since:** 1.0.0

--------------------


### getPreferred

```typescript
getPreferred() => Promise<GetPreferredResponse>
```

Get the preferred zoom level.

Zoom levels are represented as a decimal (e.g. 1.2 is 120%).

**Returns:** Promise&lt;[GetPreferredResponse](#getpreferredresponse)&gt;

**Since:** 1.0.0

--------------------


### set

```typescript
set(options: SetOptions) => Promise<void>
```

Set the current zoom level.

Zoom levels are represented as a decimal (e.g. 1.2 is 120%).

| Param       | Type                      |
| ----------- | ------------------------- |
| **options** | [SetOptions](#setoptions) |

**Returns:** Promise&lt;void&gt;

**Since:** 1.0.0

--------------------


### Interfaces


#### GetResponse

| Prop      | Type   | Description                                        | Since |
| --------- | ------ | -------------------------------------------------- | ----- |
| **value** | number | The current zoom level (represented as a decimal). | 1.0.0 |


#### GetPreferredResponse

| Prop      | Type   | Description                                          | Since |
| --------- | ------ | ---------------------------------------------------- | ----- |
| **value** | number | The preferred zoom level (represented as a decimal). | 1.0.0 |


#### SetOptions

| Prop      | Type   | Description                                    | Since |
| --------- | ------ | ---------------------------------------------- | ----- |
| **value** | number | The new zoom level (represented as a decimal). | 1.0.0 |


<!--DOCGEN_API_END-->