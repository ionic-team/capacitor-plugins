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
* [`enableClustering()`](#enableclustering)
* [`disableClustering()`](#disableclustering)
* [`addMarker(...)`](#addmarker)
* [`addMarkers(...)`](#addmarkers)
* [`removeMarker(...)`](#removemarker)
* [`removeMarkers(...)`](#removemarkers)
* [`destroy()`](#destroy)
* [`setCamera(...)`](#setcamera)
* [`setMapType(...)`](#setmaptype)
* [`enableIndoorMaps(...)`](#enableindoormaps)
* [`enableTrafficLayer(...)`](#enabletrafficlayer)
* [`enableAccessibilityElements(...)`](#enableaccessibilityelements)
* [`enableCurrentLocation(...)`](#enablecurrentlocation)
* [`setPadding(...)`](#setpadding)
* [`setOnCameraIdleListener(...)`](#setoncameraidlelistener)
* [`setOnCameraMoveStartedListener(...)`](#setoncameramovestartedlistener)
* [`setOnClusterClickListener(...)`](#setonclusterclicklistener)
* [`setOnClusterInfoWindowClickListener(...)`](#setonclusterinfowindowclicklistener)
* [`setOnInfoWindowClickListener(...)`](#setoninfowindowclicklistener)
* [`setOnMapClickListener(...)`](#setonmapclicklistener)
* [`setOnMarkerClickListener(...)`](#setonmarkerclicklistener)
* [`setOnMyLocationButtonClickListener(...)`](#setonmylocationbuttonclicklistener)
* [`setOnMyLocationClickListener(...)`](#setonmylocationclicklistener)
* [Interfaces](#interfaces)
* [Type Aliases](#type-aliases)
* [Enums](#enums)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### create(...)

```typescript
create(options: CreateMapArgs, callback?: MapListenerCallback | undefined) => Promise<GoogleMap>
```

| Param          | Type                                                                |
| -------------- | ------------------------------------------------------------------- |
| **`options`**  | <code><a href="#createmapargs">CreateMapArgs</a></code>             |
| **`callback`** | <code><a href="#maplistenercallback">MapListenerCallback</a></code> |

**Returns:** <code>Promise&lt;GoogleMap&gt;</code>

--------------------


### enableClustering()

```typescript
enableClustering() => Promise<void>
```

--------------------


### disableClustering()

```typescript
disableClustering() => Promise<void>
```

--------------------


### addMarker(...)

```typescript
addMarker(marker: Marker) => Promise<string>
```

| Param        | Type                                      |
| ------------ | ----------------------------------------- |
| **`marker`** | <code><a href="#marker">Marker</a></code> |

**Returns:** <code>Promise&lt;string&gt;</code>

--------------------


### addMarkers(...)

```typescript
addMarkers(markers: Marker[]) => Promise<string[]>
```

| Param         | Type                  |
| ------------- | --------------------- |
| **`markers`** | <code>Marker[]</code> |

**Returns:** <code>Promise&lt;string[]&gt;</code>

--------------------


### removeMarker(...)

```typescript
removeMarker(id: string) => Promise<void>
```

| Param    | Type                |
| -------- | ------------------- |
| **`id`** | <code>string</code> |

--------------------


### removeMarkers(...)

```typescript
removeMarkers(ids: string[]) => Promise<void>
```

| Param     | Type                  |
| --------- | --------------------- |
| **`ids`** | <code>string[]</code> |

--------------------


### destroy()

```typescript
destroy() => Promise<void>
```

--------------------


### setCamera(...)

```typescript
setCamera(config: CameraConfig) => Promise<void>
```

| Param        | Type                                                  |
| ------------ | ----------------------------------------------------- |
| **`config`** | <code><a href="#cameraconfig">CameraConfig</a></code> |

--------------------


### setMapType(...)

```typescript
setMapType(mapType: MapType) => Promise<void>
```

| Param         | Type                                        |
| ------------- | ------------------------------------------- |
| **`mapType`** | <code><a href="#maptype">MapType</a></code> |

--------------------


### enableIndoorMaps(...)

```typescript
enableIndoorMaps(enabled: boolean) => Promise<void>
```

| Param         | Type                 |
| ------------- | -------------------- |
| **`enabled`** | <code>boolean</code> |

--------------------


### enableTrafficLayer(...)

```typescript
enableTrafficLayer(enabled: boolean) => Promise<void>
```

| Param         | Type                 |
| ------------- | -------------------- |
| **`enabled`** | <code>boolean</code> |

--------------------


### enableAccessibilityElements(...)

```typescript
enableAccessibilityElements(enabled: boolean) => Promise<void>
```

| Param         | Type                 |
| ------------- | -------------------- |
| **`enabled`** | <code>boolean</code> |

--------------------


### enableCurrentLocation(...)

```typescript
enableCurrentLocation(enabled: boolean) => Promise<void>
```

| Param         | Type                 |
| ------------- | -------------------- |
| **`enabled`** | <code>boolean</code> |

--------------------


### setPadding(...)

```typescript
setPadding(padding: MapPadding) => Promise<void>
```

| Param         | Type                                              |
| ------------- | ------------------------------------------------- |
| **`padding`** | <code><a href="#mappadding">MapPadding</a></code> |

--------------------


### setOnCameraIdleListener(...)

```typescript
setOnCameraIdleListener(callback?: MapListenerCallback | undefined) => Promise<void>
```

| Param          | Type                                                                |
| -------------- | ------------------------------------------------------------------- |
| **`callback`** | <code><a href="#maplistenercallback">MapListenerCallback</a></code> |

--------------------


### setOnCameraMoveStartedListener(...)

```typescript
setOnCameraMoveStartedListener(callback?: MapListenerCallback | undefined) => Promise<void>
```

| Param          | Type                                                                |
| -------------- | ------------------------------------------------------------------- |
| **`callback`** | <code><a href="#maplistenercallback">MapListenerCallback</a></code> |

--------------------


### setOnClusterClickListener(...)

```typescript
setOnClusterClickListener(callback?: MapListenerCallback | undefined) => Promise<void>
```

| Param          | Type                                                                |
| -------------- | ------------------------------------------------------------------- |
| **`callback`** | <code><a href="#maplistenercallback">MapListenerCallback</a></code> |

--------------------


### setOnClusterInfoWindowClickListener(...)

```typescript
setOnClusterInfoWindowClickListener(callback?: MapListenerCallback | undefined) => Promise<void>
```

| Param          | Type                                                                |
| -------------- | ------------------------------------------------------------------- |
| **`callback`** | <code><a href="#maplistenercallback">MapListenerCallback</a></code> |

--------------------


### setOnInfoWindowClickListener(...)

```typescript
setOnInfoWindowClickListener(callback?: MapListenerCallback | undefined) => Promise<void>
```

| Param          | Type                                                                |
| -------------- | ------------------------------------------------------------------- |
| **`callback`** | <code><a href="#maplistenercallback">MapListenerCallback</a></code> |

--------------------


### setOnMapClickListener(...)

```typescript
setOnMapClickListener(callback?: MapListenerCallback | undefined) => Promise<void>
```

| Param          | Type                                                                |
| -------------- | ------------------------------------------------------------------- |
| **`callback`** | <code><a href="#maplistenercallback">MapListenerCallback</a></code> |

--------------------


### setOnMarkerClickListener(...)

```typescript
setOnMarkerClickListener(callback?: MapListenerCallback | undefined) => Promise<void>
```

| Param          | Type                                                                |
| -------------- | ------------------------------------------------------------------- |
| **`callback`** | <code><a href="#maplistenercallback">MapListenerCallback</a></code> |

--------------------


### setOnMyLocationButtonClickListener(...)

```typescript
setOnMyLocationButtonClickListener(callback?: MapListenerCallback | undefined) => Promise<void>
```

| Param          | Type                                                                |
| -------------- | ------------------------------------------------------------------- |
| **`callback`** | <code><a href="#maplistenercallback">MapListenerCallback</a></code> |

--------------------


### setOnMyLocationClickListener(...)

```typescript
setOnMyLocationClickListener(callback?: MapListenerCallback | undefined) => Promise<void>
```

| Param          | Type                                                                |
| -------------- | ------------------------------------------------------------------- |
| **`callback`** | <code><a href="#maplistenercallback">MapListenerCallback</a></code> |

--------------------


### Interfaces


#### CreateMapArgs

| Prop              | Type                                                        |
| ----------------- | ----------------------------------------------------------- |
| **`id`**          | <code>string</code>                                         |
| **`apiKey`**      | <code>string</code>                                         |
| **`config`**      | <code><a href="#googlemapconfig">GoogleMapConfig</a></code> |
| **`element`**     | <code>HTMLElement</code>                                    |
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


#### CameraConfig

| Prop                    | Type                                      |
| ----------------------- | ----------------------------------------- |
| **`coordinate`**        | <code><a href="#latlng">LatLng</a></code> |
| **`zoom`**              | <code>number</code>                       |
| **`bearing`**           | <code>number</code>                       |
| **`angle`**             | <code>number</code>                       |
| **`animate`**           | <code>boolean</code>                      |
| **`animationDuration`** | <code>number</code>                       |


#### MapPadding

| Prop         | Type                |
| ------------ | ------------------- |
| **`top`**    | <code>number</code> |
| **`left`**   | <code>number</code> |
| **`right`**  | <code>number</code> |
| **`bottom`** | <code>number</code> |


### Type Aliases


#### MapListenerCallback

<code>(data: any): void</code>


### Enums


#### MapType

| Members         | Value                    |
| --------------- | ------------------------ |
| **`Normal`**    | <code>'Normal'</code>    |
| **`Hybrid`**    | <code>'Hybrid'</code>    |
| **`Satellite`** | <code>'Satellite'</code> |
| **`Terrain`**   | <code>'Terrain'</code>   |
| **`None`**      | <code>'None'</code>      |

</docgen-api>
