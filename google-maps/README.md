# @capacitor/google-maps

Google maps on Capacitor

## Install

```bash
npm install @capacitor/google-maps
npx cap sync
```

## iOS

The Google Maps SDK supports the use of showing the users current location via `enableCurrentLocation(bool)`.  To use this, Apple requires privacy descriptions to be specified in `Info.plist`:

- `NSLocationAlwaysUsageDescription` (`Privacy - Location Always Usage Description`)
- `NSLocationWhenInUseUsageDescription` (`Privacy - Location When In Use Usage Description`)

Read about [Configuring `Info.plist`](https://capacitorjs.com/docs/ios/configuration#configuring-infoplist) in the [iOS Guide](https://capacitorjs.com/docs/ios) for more information on setting iOS permissions in Xcode.

## Android
Inside of your application android folder, add the following to your `local.properties` file:

```
REACT_APP_GOOGLE_MAPS_API_KEY=[YOUR_GOOGLE_MAPS_API_KEY]
```

This to use certain location features, API requires the following permissions be added to your AndroidManifest.xml:

```
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
```


## Example
```
import { GoogleMap } from '@capacitor/google-maps';

const apiKey = process.env.REACT_APP_GOOGLE_MAPS_API_KEY;

const mapRef = document.getElementById("map");

const newMap = await GoogleMap.create(mapRef, "my-map", apiKey!, {
    center: {
        lat: 33.6,
        lng: -117.9,
    },
    zoom: 8,
    androidLiteMode: false,
});  

await newMap.addMarkers({
    coordinate: {
        lat: 33.6,
        lng: -117.9,
    },
    title: "Hello world",
});


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
* [Interfaces](#interfaces)
* [Enums](#enums)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### create(...)

```typescript
create(element: any, id: string, apiKey: string, config: GoogleMapConfig, forceCreate?: boolean | undefined) => Promise<GoogleMap>
```

| Param             | Type                                                        |
| ----------------- | ----------------------------------------------------------- |
| **`element`**     | <code>any</code>                                            |
| **`id`**          | <code>string</code>                                         |
| **`apiKey`**      | <code>string</code>                                         |
| **`config`**      | <code><a href="#googlemapconfig">GoogleMapConfig</a></code> |
| **`forceCreate`** | <code>boolean</code>                                        |

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


### Interfaces


#### GoogleMapConfig

| Prop                  | Type                                      | Description                               | Default            |
| --------------------- | ----------------------------------------- | ----------------------------------------- | ------------------ |
| **`width`**           | <code>number</code>                       |                                           |                    |
| **`height`**          | <code>number</code>                       |                                           |                    |
| **`x`**               | <code>number</code>                       |                                           |                    |
| **`y`**               | <code>number</code>                       |                                           |                    |
| **`center`**          | <code><a href="#latlng">LatLng</a></code> |                                           |                    |
| **`zoom`**            | <code>number</code>                       | Sets the zoom of the map.                 |                    |
| **`androidLiteMode`** | <code>boolean</code>                      | Enables image-based lite mode on Android. | <code>false</code> |


#### LatLng

An interface representing a pair of latitude and longitude coordinates.

| Prop      | Type                | Description                                                               |
| --------- | ------------------- | ------------------------------------------------------------------------- |
| **`lat`** | <code>number</code> | Coordinate latitude, in degrees. This value is in the range [-90, 90].    |
| **`lng`** | <code>number</code> | Coordinate longitude, in degrees. This value is in the range [-180, 180]. |


#### Marker

A marker is an icon placed at a particular point on the map's surface.

| Prop             | Type                                      | Description                                                                                               | Default            |
| ---------------- | ----------------------------------------- | --------------------------------------------------------------------------------------------------------- | ------------------ |
| **`coordinate`** | <code><a href="#latlng">LatLng</a></code> | <a href="#marker">Marker</a> position                                                                     |                    |
| **`opacity`**    | <code>number</code>                       | Sets the opacity of the marker, between 0 (completely transparent) and 1 inclusive.                       | <code>1</code>     |
| **`title`**      | <code>string</code>                       | Title, a short description of the overlay.                                                                |                    |
| **`snippet`**    | <code>string</code>                       | Snippet text, shown beneath the title in the info window when selected.                                   |                    |
| **`isFlat`**     | <code>boolean</code>                      | Controls whether this marker should be flat against the Earth's surface or a billboard facing the camera. | <code>false</code> |
| **`iconUrl`**    | <code>string</code>                       | <a href="#marker">Marker</a> icon to render.                                                              |                    |
| **`draggable`**  | <code>boolean</code>                      | Controls whether this marker can be dragged interactively                                                 | <code>false</code> |


#### CameraConfig

Configuration properties for a Google Map Camera

| Prop                    | Type                                      | Description                                                                                                            | Default            |
| ----------------------- | ----------------------------------------- | ---------------------------------------------------------------------------------------------------------------------- | ------------------ |
| **`coordinate`**        | <code><a href="#latlng">LatLng</a></code> | Location on the Earth towards which the camera points.                                                                 |                    |
| **`zoom`**              | <code>number</code>                       | Sets the zoom of the map.                                                                                              |                    |
| **`bearing`**           | <code>number</code>                       | Bearing of the camera, in degrees clockwise from true north.                                                           | <code>0</code>     |
| **`angle`**             | <code>number</code>                       | The angle, in degrees, of the camera from the nadir (directly facing the Earth). The only allowed values are 0 and 45. | <code>0</code>     |
| **`animate`**           | <code>boolean</code>                      | Animate the transition to the new Camera properties.                                                                   | <code>false</code> |
| **`animationDuration`** | <code>number</code>                       |                                                                                                                        |                    |


#### MapPadding

Controls for setting padding on the 'visible' region of the view.

| Prop         | Type                |
| ------------ | ------------------- |
| **`top`**    | <code>number</code> |
| **`left`**   | <code>number</code> |
| **`right`**  | <code>number</code> |
| **`bottom`** | <code>number</code> |


### Enums


#### MapType

| Members         | Value                    | Description                              |
| --------------- | ------------------------ | ---------------------------------------- |
| **`Normal`**    | <code>'Normal'</code>    | Basic map.                               |
| **`Hybrid`**    | <code>'Hybrid'</code>    | Satellite imagery with roads and labels. |
| **`Satellite`** | <code>'Satellite'</code> | Satellite imagery with no labels.        |
| **`Terrain`**   | <code>'Terrain'</code>   | Topographic data.                        |
| **`None`**      | <code>'None'</code>      | No base map tiles.                       |

</docgen-api>
