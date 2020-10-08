# @capacitor/storage

The Storage API provides a simple key/value persistent store for lightweight data.

<!--DOCGEN_INDEX_START-->
* [configure()](#configure)
* [get()](#get)
* [set()](#set)
* [remove()](#remove)
* [clear()](#clear)
* [keys()](#keys)
* [migrate()](#migrate)
* [Interfaces](#interfaces)
<!--DOCGEN_INDEX_END-->

<!--DOCGEN_API_START-->
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->
## API

### configure

```typescript
configure(options: ConfigureOptions) => Promise<void>
```

Configure the storage plugin at runtime.

Options that are `undefined` will not be used.

| Param       | Type                                  |
| ----------- | ------------------------------------- |
| **options** | [ConfigureOptions](#configureoptions) |

**Returns:** Promise&lt;void&gt;

**Since:** 1.0.0

--------------------


### get

```typescript
get(options: GetOptions) => Promise<GetResult>
```

Get the value from storage of a given key.

| Param       | Type                      |
| ----------- | ------------------------- |
| **options** | [GetOptions](#getoptions) |

**Returns:** Promise&lt;[GetResult](#getresult)&gt;

**Since:** 1.0.0

--------------------


### set

```typescript
set(options: SetOptions) => Promise<void>
```

Set the value in storage for a given key.

| Param       | Type                      |
| ----------- | ------------------------- |
| **options** | [SetOptions](#setoptions) |

**Returns:** Promise&lt;void&gt;

**Since:** 1.0.0

--------------------


### remove

```typescript
remove(options: RemoveOptions) => Promise<void>
```

Remove the value from storage for a given key, if any.

| Param       | Type                            |
| ----------- | ------------------------------- |
| **options** | [RemoveOptions](#removeoptions) |

**Returns:** Promise&lt;void&gt;

**Since:** 1.0.0

--------------------


### clear

```typescript
clear() => Promise<void>
```

Clear keys and values from storage.

**Returns:** Promise&lt;void&gt;

**Since:** 1.0.0

--------------------


### keys

```typescript
keys() => Promise<KeysResult>
```

Return the list of known keys in storage.

**Returns:** Promise&lt;[KeysResult](#keysresult)&gt;

**Since:** 1.0.0

--------------------


### migrate

```typescript
migrate() => Promise<MigrateResult>
```

Migrate data from the Capacitor 2 Storage plugin.

This action is non-destructive. It will not remove old data and will only
write new data if they key was not already set.

**Returns:** Promise&lt;[MigrateResult](#migrateresult)&gt;

**Since:** 1.0.0

--------------------


### Interfaces


#### ConfigureOptions

| Prop      | Type   | Description                                                                                                                                                                                                                                                                                                                                      | Default          | Since |
| --------- | ------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | ---------------- | ----- |
| **group** | string | Set the storage group. Storage groups are used to organize key/value pairs. Using the value 'NativeStorage' provides backwards-compatibility with [`cordova-plugin-nativestorage`](https://www.npmjs.com/package/cordova-plugin-nativestorage). WARNING: The `clear()` method can delete unintended values when using the 'NativeStorage' group. | CapacitorStorage | 1.0.0 |


#### GetResult

| Prop      | Type           | Description                                                                                                                   | Since |
| --------- | -------------- | ----------------------------------------------------------------------------------------------------------------------------- | ----- |
| **value** | string \| null | The value from storage associated with the given key. If a value was not previously set or was removed, value will be `null`. | 1.0.0 |


#### GetOptions

| Prop    | Type   | Description                                   | Since |
| ------- | ------ | --------------------------------------------- | ----- |
| **key** | string | The key whose value to retrieve from storage. | 1.0.0 |


#### SetOptions

| Prop      | Type   | Description                                               | Since |
| --------- | ------ | --------------------------------------------------------- | ----- |
| **key**   | string | The key to associate with the value being set in storage. | 1.0.0 |
| **value** | string | The value to set in storage with the associated key.      | 1.0.0 |


#### RemoveOptions

| Prop    | Type   | Description                                 | Since |
| ------- | ------ | ------------------------------------------- | ----- |
| **key** | string | The key whose value to remove from storage. | 1.0.0 |


#### KeysResult

| Prop     | Type     | Description                | Since |
| -------- | -------- | -------------------------- | ----- |
| **keys** | string[] | The known keys in storage. | 1.0.0 |


#### MigrateResult

| Prop         | Type     | Description                                                                                                                   | Since |
| ------------ | -------- | ----------------------------------------------------------------------------------------------------------------------------- | ----- |
| **migrated** | string[] | An array of keys that were migrated.                                                                                          | 1.0.0 |
| **existing** | string[] | An array of keys that were already migrated or otherwise exist in storage that had a value in the Capacitor 2 Storage plugin. | 1.0.0 |


<!--DOCGEN_API_END-->