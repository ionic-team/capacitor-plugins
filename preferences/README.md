# @capacitor/preferences

The Preferences API provides a simple key/value persistent store for lightweight data.

Mobile OSs may periodically clear data set in `window.localStorage`, so this
API should be used instead. This API will fall back to using `localStorage`
when running as a Progressive Web App.

This plugin will use
[`UserDefaults`](https://developer.apple.com/documentation/foundation/userdefaults)
on iOS and
[`SharedPreferences`](https://developer.android.com/reference/android/content/SharedPreferences)
on Android. Stored data is cleared if the app is uninstalled.

**Note**: This API is _not_ meant to be used as a local database. If your app
stores a lot of data, has high read/write load, or requires complex querying,
we recommend taking a look at a SQLite-based solution. One such solution is [Ionic Secure Storage](https://ionic.io/docs/secure-storage), a SQLite-based engine with full encryption support. The [Capacitor Community](https://github.com/capacitor-community/) has also built a number of other storage engines.

## Install

```bash
npm install @capacitor/preferences
npx cap sync
```

## Example

```typescript
import { Preferences } from '@capacitor/preferences';

const setName = async () => {
  await Preferences.set({
    key: 'name',
    value: 'Max',
  });
};

const checkName = async () => {
  const { value } = await Preferences.get({ key: 'name' });

  console.log(`Hello ${value}!`);
};

const removeName = async () => {
  await Preferences.remove({ key: 'name' });
};
```

## Working with JSON

The Preferences API only supports string values. You can, however, use JSON if you `JSON.stringify` the object before calling `set()`, then `JSON.parse` the value returned from `get()`.

This method can also be used to store non-string values, such as numbers and booleans.

## API

<docgen-index>

* [`configure(...)`](#configure)
* [`get(...)`](#get)
* [`set(...)`](#set)
* [`remove(...)`](#remove)
* [`clear()`](#clear)
* [`keys()`](#keys)
* [`migrate()`](#migrate)
* [`removeOld()`](#removeold)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### configure(...)

```typescript
configure(options: ConfigureOptions) => Promise<void>
```

Configure the preferences plugin at runtime.

Options that are `undefined` will not be used.

| Param         | Type                                                          |
| ------------- | ------------------------------------------------------------- |
| **`options`** | <code><a href="#configureoptions">ConfigureOptions</a></code> |

**Since:** 1.0.0

--------------------


### get(...)

```typescript
get(options: GetOptions) => Promise<GetResult>
```

Get the value from preferences of a given key.

| Param         | Type                                              |
| ------------- | ------------------------------------------------- |
| **`options`** | <code><a href="#getoptions">GetOptions</a></code> |

**Returns:** <code>Promise&lt;<a href="#getresult">GetResult</a>&gt;</code>

**Since:** 1.0.0

--------------------


### set(...)

```typescript
set(options: SetOptions) => Promise<void>
```

Set the value in preferences for a given key.

| Param         | Type                                              |
| ------------- | ------------------------------------------------- |
| **`options`** | <code><a href="#setoptions">SetOptions</a></code> |

**Since:** 1.0.0

--------------------


### remove(...)

```typescript
remove(options: RemoveOptions) => Promise<void>
```

Remove the value from preferences for a given key, if any.

| Param         | Type                                                    |
| ------------- | ------------------------------------------------------- |
| **`options`** | <code><a href="#removeoptions">RemoveOptions</a></code> |

**Since:** 1.0.0

--------------------


### clear()

```typescript
clear() => Promise<void>
```

Clear keys and values from preferences.

**Since:** 1.0.0

--------------------


### keys()

```typescript
keys() => Promise<KeysResult>
```

Return the list of known keys in preferences.

**Returns:** <code>Promise&lt;<a href="#keysresult">KeysResult</a>&gt;</code>

**Since:** 1.0.0

--------------------


### migrate()

```typescript
migrate() => Promise<MigrateResult>
```

Migrate data from the Capacitor 2 Storage plugin.

This action is non-destructive. It will not remove old data and will only
write new data if they key was not already set.
To remove the old data after being migrated, call removeOld().

**Returns:** <code>Promise&lt;<a href="#migrateresult">MigrateResult</a>&gt;</code>

**Since:** 1.0.0

--------------------


### removeOld()

```typescript
removeOld() => Promise<void>
```

Removes old data with `_cap_` prefix from the Capacitor 2 Storage plugin.

**Since:** 1.1.0

--------------------


### Interfaces


#### ConfigureOptions

| Prop        | Type                | Description                                                                                                                                                                                                                                                                                                                                              | Default                       | Since |
| ----------- | ------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ----------------------------- | ----- |
| **`group`** | <code>string</code> | Set the preferences group. Preferences groups are used to organize key/value pairs. Using the value 'NativeStorage' provides backwards-compatibility with [`cordova-plugin-nativestorage`](https://www.npmjs.com/package/cordova-plugin-nativestorage). WARNING: The `clear()` method can delete unintended values when using the 'NativeStorage' group. | <code>CapacitorStorage</code> | 1.0.0 |


#### GetResult

| Prop        | Type                        | Description                                                                                                                       | Since |
| ----------- | --------------------------- | --------------------------------------------------------------------------------------------------------------------------------- | ----- |
| **`value`** | <code>string \| null</code> | The value from preferences associated with the given key. If a value was not previously set or was removed, value will be `null`. | 1.0.0 |


#### GetOptions

| Prop      | Type                | Description                                       | Since |
| --------- | ------------------- | ------------------------------------------------- | ----- |
| **`key`** | <code>string</code> | The key whose value to retrieve from preferences. | 1.0.0 |


#### SetOptions

| Prop        | Type                | Description                                                   | Since |
| ----------- | ------------------- | ------------------------------------------------------------- | ----- |
| **`key`**   | <code>string</code> | The key to associate with the value being set in preferences. | 1.0.0 |
| **`value`** | <code>string</code> | The value to set in preferences with the associated key.      | 1.0.0 |


#### RemoveOptions

| Prop      | Type                | Description                                     | Since |
| --------- | ------------------- | ----------------------------------------------- | ----- |
| **`key`** | <code>string</code> | The key whose value to remove from preferences. | 1.0.0 |


#### KeysResult

| Prop       | Type                  | Description                    | Since |
| ---------- | --------------------- | ------------------------------ | ----- |
| **`keys`** | <code>string[]</code> | The known keys in preferences. | 1.0.0 |


#### MigrateResult

| Prop           | Type                  | Description                                                                                                                           | Since |
| -------------- | --------------------- | ------------------------------------------------------------------------------------------------------------------------------------- | ----- |
| **`migrated`** | <code>string[]</code> | An array of keys that were migrated.                                                                                                  | 1.0.0 |
| **`existing`** | <code>string[]</code> | An array of keys that were already migrated or otherwise exist in preferences that had a value in the Capacitor 2 Preferences plugin. | 1.0.0 |

</docgen-api>
