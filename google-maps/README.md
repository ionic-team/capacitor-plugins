# @capacitor/google-maps

Google maps on Capacitor

## Install

```bash
npm install @capacitor/google-maps
npx cap sync
```

## API

<docgen-index>

* [`create(...)`](#create)
* [`addMarker(...)`](#addmarker)
* [`removeMarker(...)`](#removemarker)
* [`destroy(...)`](#destroy)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### create(...)

```typescript
create(args: CreateMapArgs) => Promise<void>
```

| Param      | Type                                                    |
| ---------- | ------------------------------------------------------- |
| **`args`** | <code><a href="#createmapargs">CreateMapArgs</a></code> |

--------------------


### addMarker(...)

```typescript
addMarker(args: AddMarkerArgs) => Promise<{ id: string; }>
```

| Param      | Type                                                    |
| ---------- | ------------------------------------------------------- |
| **`args`** | <code><a href="#addmarkerargs">AddMarkerArgs</a></code> |

**Returns:** <code>Promise&lt;{ id: string; }&gt;</code>

--------------------


### removeMarker(...)

```typescript
removeMarker(args: RemoveMarkerArgs) => Promise<void>
```

| Param      | Type                                                          |
| ---------- | ------------------------------------------------------------- |
| **`args`** | <code><a href="#removemarkerargs">RemoveMarkerArgs</a></code> |

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


#### CreateMapArgs

| Prop              | Type                                                        |
| ----------------- | ----------------------------------------------------------- |
| **`id`**          | <code>string</code>                                         |
| **`apiKey`**      | <code>string</code>                                         |
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


#### AddMarkerArgs

| Prop         | Type                                      |
| ------------ | ----------------------------------------- |
| **`id`**     | <code>string</code>                       |
| **`marker`** | <code><a href="#marker">Marker</a></code> |


#### Marker

| Prop             | Type                                      |
| ---------------- | ----------------------------------------- |
| **`coordinate`** | <code><a href="#latlng">LatLng</a></code> |
| **`opacity`**    | <code>number</code>                       |
| **`title`**      | <code>string</code>                       |
| **`snippet`**    | <code>string</code>                       |
| **`isFlat`**     | <code>boolean</code>                      |
| **`iconUrl`**    | <code>string</code>                       |
| **`draggable`**  | <code>boolean</code>                      |


#### RemoveMarkerArgs

| Prop           | Type                |
| -------------- | ------------------- |
| **`id`**       | <code>string</code> |
| **`markerId`** | <code>string</code> |


#### DestroyMapArgs

| Prop     | Type                |
| -------- | ------------------- |
| **`id`** | <code>string</code> |

</docgen-api>
