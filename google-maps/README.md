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
* [`addMarkers(...)`](#addmarkers)
* [`removeMarker(...)`](#removemarker)
* [`removeMarkers(...)`](#removemarkers)
* [`enableClustering(...)`](#enableclustering)
* [`disableClustering(...)`](#disableclustering)
* [`destroy(...)`](#destroy)
* [`setCamera(...)`](#setcamera)
* [`setMapType(...)`](#setmaptype)
* [`enableIndoorMaps(...)`](#enableindoormaps)
* [`enableTrafficLayer(...)`](#enabletrafficlayer)
* [`enableAccessibilityElements(...)`](#enableaccessibilityelements)
* [`enableCurrentLocation(...)`](#enablecurrentlocation)
* [`setPadding(...)`](#setpadding)
* [`onScroll(...)`](#onscroll)
* [`addListener('onCameraIdle', ...)`](#addlisteneroncameraidle)
* [`addListener('onCameraMoveStarted', ...)`](#addlisteneroncameramovestarted)
* [`addListener('onClusterClick', ...)`](#addlisteneronclusterclick)
* [`addListener('onClusterInfoWindowClick', ...)`](#addlisteneronclusterinfowindowclick)
* [`addListener('onInfoWindowClick', ...)`](#addlisteneroninfowindowclick)
* [`addListener('onMapReady', ...)`](#addlisteneronmapready)
* [`addListener('onMapClick', ...)`](#addlisteneronmapclick)
* [`addListener('onMarkerClick', ...)`](#addlisteneronmarkerclick)
* [`addListener('onMyLocationButtonClick', ...)`](#addlisteneronmylocationbuttonclick)
* [`addListener('onMyLocationClick', ...)`](#addlisteneronmylocationclick)
* [Interfaces](#interfaces)
* [Type Aliases](#type-aliases)
* [Enums](#enums)

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


### addMarkers(...)

```typescript
addMarkers(args: AddMarkersArgs) => Promise<{ ids: string[]; }>
```

| Param      | Type                                                      |
| ---------- | --------------------------------------------------------- |
| **`args`** | <code><a href="#addmarkersargs">AddMarkersArgs</a></code> |

**Returns:** <code>Promise&lt;{ ids: string[]; }&gt;</code>

--------------------


### removeMarker(...)

```typescript
removeMarker(args: RemoveMarkerArgs) => Promise<void>
```

| Param      | Type                                                          |
| ---------- | ------------------------------------------------------------- |
| **`args`** | <code><a href="#removemarkerargs">RemoveMarkerArgs</a></code> |

--------------------


### removeMarkers(...)

```typescript
removeMarkers(args: RemoveMarkersArgs) => Promise<void>
```

| Param      | Type                                                            |
| ---------- | --------------------------------------------------------------- |
| **`args`** | <code><a href="#removemarkersargs">RemoveMarkersArgs</a></code> |

--------------------


### enableClustering(...)

```typescript
enableClustering(args: { id: string; }) => Promise<void>
```

| Param      | Type                         |
| ---------- | ---------------------------- |
| **`args`** | <code>{ id: string; }</code> |

--------------------


### disableClustering(...)

```typescript
disableClustering(args: { id: string; }) => Promise<void>
```

| Param      | Type                         |
| ---------- | ---------------------------- |
| **`args`** | <code>{ id: string; }</code> |

--------------------


### destroy(...)

```typescript
destroy(args: DestroyMapArgs) => Promise<void>
```

| Param      | Type                                                      |
| ---------- | --------------------------------------------------------- |
| **`args`** | <code><a href="#destroymapargs">DestroyMapArgs</a></code> |

--------------------


### setCamera(...)

```typescript
setCamera(args: CameraArgs) => Promise<void>
```

| Param      | Type                                              |
| ---------- | ------------------------------------------------- |
| **`args`** | <code><a href="#cameraargs">CameraArgs</a></code> |

--------------------


### setMapType(...)

```typescript
setMapType(args: MapTypeArgs) => Promise<void>
```

| Param      | Type                                                |
| ---------- | --------------------------------------------------- |
| **`args`** | <code><a href="#maptypeargs">MapTypeArgs</a></code> |

--------------------


### enableIndoorMaps(...)

```typescript
enableIndoorMaps(args: IndoorMapArgs) => Promise<void>
```

| Param      | Type                                                    |
| ---------- | ------------------------------------------------------- |
| **`args`** | <code><a href="#indoormapargs">IndoorMapArgs</a></code> |

--------------------


### enableTrafficLayer(...)

```typescript
enableTrafficLayer(args: TrafficLayerArgs) => Promise<void>
```

| Param      | Type                                                          |
| ---------- | ------------------------------------------------------------- |
| **`args`** | <code><a href="#trafficlayerargs">TrafficLayerArgs</a></code> |

--------------------


### enableAccessibilityElements(...)

```typescript
enableAccessibilityElements(args: AccElementsArgs) => Promise<void>
```

| Param      | Type                                                        |
| ---------- | ----------------------------------------------------------- |
| **`args`** | <code><a href="#accelementsargs">AccElementsArgs</a></code> |

--------------------


### enableCurrentLocation(...)

```typescript
enableCurrentLocation(args: CurrentLocArgs) => Promise<void>
```

| Param      | Type                                                      |
| ---------- | --------------------------------------------------------- |
| **`args`** | <code><a href="#currentlocargs">CurrentLocArgs</a></code> |

--------------------


### setPadding(...)

```typescript
setPadding(args: PaddingArgs) => Promise<void>
```

| Param      | Type                                                |
| ---------- | --------------------------------------------------- |
| **`args`** | <code><a href="#paddingargs">PaddingArgs</a></code> |

--------------------


### onScroll(...)

```typescript
onScroll(args: OnScrollArgs) => Promise<void>
```

| Param      | Type                                                  |
| ---------- | ----------------------------------------------------- |
| **`args`** | <code><a href="#onscrollargs">OnScrollArgs</a></code> |

--------------------


### addListener('onCameraIdle', ...)

```typescript
addListener(eventName: 'onCameraIdle', listenerFunc: MapListenerCallback) => PluginListenerHandle
```

| Param              | Type                                                                |
| ------------------ | ------------------------------------------------------------------- |
| **`eventName`**    | <code>'onCameraIdle'</code>                                         |
| **`listenerFunc`** | <code><a href="#maplistenercallback">MapListenerCallback</a></code> |

**Returns:** <code><a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

--------------------


### addListener('onCameraMoveStarted', ...)

```typescript
addListener(eventName: 'onCameraMoveStarted', listenerFunc: MapListenerCallback) => PluginListenerHandle
```

| Param              | Type                                                                |
| ------------------ | ------------------------------------------------------------------- |
| **`eventName`**    | <code>'onCameraMoveStarted'</code>                                  |
| **`listenerFunc`** | <code><a href="#maplistenercallback">MapListenerCallback</a></code> |

**Returns:** <code><a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

--------------------


### addListener('onClusterClick', ...)

```typescript
addListener(eventName: 'onClusterClick', listenerFunc: MapListenerCallback) => PluginListenerHandle
```

| Param              | Type                                                                |
| ------------------ | ------------------------------------------------------------------- |
| **`eventName`**    | <code>'onClusterClick'</code>                                       |
| **`listenerFunc`** | <code><a href="#maplistenercallback">MapListenerCallback</a></code> |

**Returns:** <code><a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

--------------------


### addListener('onClusterInfoWindowClick', ...)

```typescript
addListener(eventName: 'onClusterInfoWindowClick', listenerFunc: MapListenerCallback) => PluginListenerHandle
```

| Param              | Type                                                                |
| ------------------ | ------------------------------------------------------------------- |
| **`eventName`**    | <code>'onClusterInfoWindowClick'</code>                             |
| **`listenerFunc`** | <code><a href="#maplistenercallback">MapListenerCallback</a></code> |

**Returns:** <code><a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

--------------------


### addListener('onInfoWindowClick', ...)

```typescript
addListener(eventName: 'onInfoWindowClick', listenerFunc: MapListenerCallback) => PluginListenerHandle
```

| Param              | Type                                                                |
| ------------------ | ------------------------------------------------------------------- |
| **`eventName`**    | <code>'onInfoWindowClick'</code>                                    |
| **`listenerFunc`** | <code><a href="#maplistenercallback">MapListenerCallback</a></code> |

**Returns:** <code><a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

--------------------


### addListener('onMapReady', ...)

```typescript
addListener(eventName: 'onMapReady', listenerFunc: MapListenerCallback) => PluginListenerHandle
```

| Param              | Type                                                                |
| ------------------ | ------------------------------------------------------------------- |
| **`eventName`**    | <code>'onMapReady'</code>                                           |
| **`listenerFunc`** | <code><a href="#maplistenercallback">MapListenerCallback</a></code> |

**Returns:** <code><a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

--------------------


### addListener('onMapClick', ...)

```typescript
addListener(eventName: 'onMapClick', listenerFunc: MapListenerCallback) => PluginListenerHandle
```

| Param              | Type                                                                |
| ------------------ | ------------------------------------------------------------------- |
| **`eventName`**    | <code>'onMapClick'</code>                                           |
| **`listenerFunc`** | <code><a href="#maplistenercallback">MapListenerCallback</a></code> |

**Returns:** <code><a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

--------------------


### addListener('onMarkerClick', ...)

```typescript
addListener(eventName: 'onMarkerClick', listenerFunc: MapListenerCallback) => PluginListenerHandle
```

| Param              | Type                                                                |
| ------------------ | ------------------------------------------------------------------- |
| **`eventName`**    | <code>'onMarkerClick'</code>                                        |
| **`listenerFunc`** | <code><a href="#maplistenercallback">MapListenerCallback</a></code> |

**Returns:** <code><a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

--------------------


### addListener('onMyLocationButtonClick', ...)

```typescript
addListener(eventName: 'onMyLocationButtonClick', listenerFunc: MapListenerCallback) => PluginListenerHandle
```

| Param              | Type                                                                |
| ------------------ | ------------------------------------------------------------------- |
| **`eventName`**    | <code>'onMyLocationButtonClick'</code>                              |
| **`listenerFunc`** | <code><a href="#maplistenercallback">MapListenerCallback</a></code> |

**Returns:** <code><a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

--------------------


### addListener('onMyLocationClick', ...)

```typescript
addListener(eventName: 'onMyLocationClick', listenerFunc: MapListenerCallback) => PluginListenerHandle
```

| Param              | Type                                                                |
| ------------------ | ------------------------------------------------------------------- |
| **`eventName`**    | <code>'onMyLocationClick'</code>                                    |
| **`listenerFunc`** | <code><a href="#maplistenercallback">MapListenerCallback</a></code> |

**Returns:** <code><a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

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


#### AddMarkersArgs

| Prop          | Type                  |
| ------------- | --------------------- |
| **`id`**      | <code>string</code>   |
| **`markers`** | <code>Marker[]</code> |


#### RemoveMarkerArgs

| Prop           | Type                |
| -------------- | ------------------- |
| **`id`**       | <code>string</code> |
| **`markerId`** | <code>string</code> |


#### RemoveMarkersArgs

| Prop            | Type                  |
| --------------- | --------------------- |
| **`id`**        | <code>string</code>   |
| **`markerIds`** | <code>string[]</code> |


#### DestroyMapArgs

| Prop     | Type                |
| -------- | ------------------- |
| **`id`** | <code>string</code> |


#### CameraArgs

| Prop         | Type                                                  |
| ------------ | ----------------------------------------------------- |
| **`id`**     | <code>string</code>                                   |
| **`config`** | <code><a href="#cameraconfig">CameraConfig</a></code> |


#### CameraConfig

| Prop                    | Type                                      |
| ----------------------- | ----------------------------------------- |
| **`coordinate`**        | <code><a href="#latlng">LatLng</a></code> |
| **`zoom`**              | <code>number</code>                       |
| **`bearing`**           | <code>number</code>                       |
| **`angle`**             | <code>number</code>                       |
| **`animate`**           | <code>boolean</code>                      |
| **`animationDuration`** | <code>number</code>                       |


#### MapTypeArgs

| Prop          | Type                                        |
| ------------- | ------------------------------------------- |
| **`id`**      | <code>string</code>                         |
| **`mapType`** | <code><a href="#maptype">MapType</a></code> |


#### IndoorMapArgs

| Prop          | Type                 |
| ------------- | -------------------- |
| **`id`**      | <code>string</code>  |
| **`enabled`** | <code>boolean</code> |


#### TrafficLayerArgs

| Prop          | Type                 |
| ------------- | -------------------- |
| **`id`**      | <code>string</code>  |
| **`enabled`** | <code>boolean</code> |


#### AccElementsArgs

| Prop          | Type                 |
| ------------- | -------------------- |
| **`id`**      | <code>string</code>  |
| **`enabled`** | <code>boolean</code> |


#### CurrentLocArgs

| Prop          | Type                 |
| ------------- | -------------------- |
| **`id`**      | <code>string</code>  |
| **`enabled`** | <code>boolean</code> |


#### PaddingArgs

| Prop          | Type                                              |
| ------------- | ------------------------------------------------- |
| **`id`**      | <code>string</code>                               |
| **`padding`** | <code><a href="#mappadding">MapPadding</a></code> |


#### MapPadding

| Prop         | Type                |
| ------------ | ------------------- |
| **`top`**    | <code>number</code> |
| **`left`**   | <code>number</code> |
| **`right`**  | <code>number</code> |
| **`bottom`** | <code>number</code> |


#### OnScrollArgs

| Prop            | Type                                                                  |
| --------------- | --------------------------------------------------------------------- |
| **`id`**        | <code>string</code>                                                   |
| **`mapBounds`** | <code>{ x: number; y: number; width: number; height: number; }</code> |


#### PluginListenerHandle

| Prop         | Type                                      |
| ------------ | ----------------------------------------- |
| **`remove`** | <code>() =&gt; Promise&lt;void&gt;</code> |


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
