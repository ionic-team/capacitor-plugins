# @capacitor/google-maps

Google maps on Capacitor

## Install

```bash
npm install @capacitor/google-maps
npx cap sync
```

## API

<docgen-index>

* [`initialize(...)`](#initialize)
* [`create(...)`](#create)
* [`destroy(...)`](#destroy)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### initialize(...)

```typescript
initialize(args: InitializeMapArgs) => Promise<void>
```

| Param      | Type                                                            |
| ---------- | --------------------------------------------------------------- |
| **`args`** | <code><a href="#initializemapargs">InitializeMapArgs</a></code> |

--------------------


### create(...)

```typescript
create(args: CreateMapArgs) => Promise<void>
```

| Param      | Type                                                    |
| ---------- | ------------------------------------------------------- |
| **`args`** | <code><a href="#createmapargs">CreateMapArgs</a></code> |

--------------------


### destroy(...)

```typescript
destroy(args: DestroyMapArgs) => Promise<void>
```

| Param      | Type                                                      |
| ---------- | --------------------------------------------------------- |
| **`args`** | <code><a href="#destroymapargs">DestroyMapArgs</a></code> |

--------------------


### Interfaces


#### InitializeMapArgs

| Prop      | Type                |
| --------- | ------------------- |
| **`key`** | <code>string</code> |


#### CreateMapArgs

| Prop              | Type                                                        |
| ----------------- | ----------------------------------------------------------- |
| **`id`**          | <code>string</code>                                         |
| **`config`**      | <code><a href="#googlemapconfig">GoogleMapConfig</a></code> |
| **`forceCreate`** | <code>boolean</code>                                        |


#### GoogleMapConfig

| Prop                  | Type                                      |
| --------------------- | ----------------------------------------- |
| **`width`**           | <code>number</code>                       |
| **`height`**          | <code>number</code>                       |
| **`x`**               | <code>number</code>                       |
| **`y`**               | <code>number</code>                       |
| **`center`**          | <code><a href="#latlng">LatLng</a></code> |
| **`zoom`**            | <code>number</code>                       |
| **`androidLiteMode`** | <code>boolean</code>                      |


#### LatLng

| Prop      | Type                |
| --------- | ------------------- |
| **`lat`** | <code>number</code> |
| **`lng`** | <code>number</code> |


#### DestroyMapArgs

| Prop     | Type                |
| -------- | ------------------- |
| **`id`** | <code>string</code> |

</docgen-api>
