# @capacitor/google-maps

Google maps on Capacitor

## Install

```bash
npm install @capacitor/google-maps
npx cap sync
```

## API Keys

To use the Google Maps SDK on any platform, API keys associated with an account _with billing enabled_ are required. These can be obtained from the [Google Cloud Console](https://console.cloud.google.com). This is required for all three platforms, Android, iOS, and Javascript. Additional information about obtaining these API keys can be found in the [Google Maps documentation](https://developers.google.com/maps/documentation/android-sdk/overview) for each platform.

## iOS

The Google Maps SDK supports the use of showing the users current location via `enableCurrentLocation(bool)`. To use this, Apple requires privacy descriptions to be specified in `Info.plist`:

- `NSLocationWhenInUseUsageDescription` (`Privacy - Location When In Use Usage Description`)

Read about [Configuring `Info.plist`](https://capacitorjs.com/docs/ios/configuration#configuring-infoplist) in the [iOS Guide](https://capacitorjs.com/docs/ios) for more information on setting iOS permissions in Xcode.

> The main Google Maps SDK now supports running on simulators on Apple Silicon Macs, but make sure you have the latest version of [Google-Maps-iOS-Utils](https://github.com/googlemaps/google-maps-ios-utils) installed.

If you added the previous workaround for getting the unreleased version, you can delete it now by removing this line from `ios/App/Podfile`:

```
pod 'Google-Maps-iOS-Utils', :git => 'https://github.com/googlemaps/google-maps-ios-utils.git', :commit => '637954e5bcb2a879c11a6f2cead153a6bad5339f'
```

Then run `pod update Google-Maps-iOS-Utils` from the `ios/App/` folder:

```
cd ios/App
pod update Google-Maps-iOS-Utils
```

## Android

The Google Maps SDK for Android requires you to add your API key to the AndroidManifest.xml file in your project.

```xml
<meta-data android:name="com.google.android.geo.API_KEY" android:value="YOUR_API_KEY_HERE"/>
```

To use certain location features, the SDK requires the following permissions to also be added to your AndroidManifest.xml:

```xml
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
```

### Variables

This plugin will use the following project variables (defined in your app's `variables.gradle` file):

- `googleMapsPlayServicesVersion`: version of `com.google.android.gms:play-services-maps` (default: `18.1.0`)
- `googleMapsUtilsVersion`: version of `com.google.maps.android:android-maps-utils` (default: `3.5.3`)
- `googleMapsKtxVersion`: version of `com.google.maps.android:maps-ktx` (default: `3.4.0`)
- `googleMapsUtilsKtxVersion`: version of `com.google.maps.android:maps-utils-ktx` (default: `3.4.0`)
- `kotlinxCoroutinesVersion`: version of `org.jetbrains.kotlinx:kotlinx-coroutines-android` and `org.jetbrains.kotlinx:kotlinx-coroutines-core` (default: `1.7.3`)
- `androidxCoreKTXVersion`: version of `androidx.core:core-ktx` (default: `1.12.0`)
- `kotlin_version`: version of `org.jetbrains.kotlin:kotlin-stdlib` (default: `1.9.10`)


## Usage

The Google Maps Capacitor plugin ships with a web component that must be used to render the map in your application as it enables us to embed the native view more effectively on iOS. The plugin will automatically register this web component for use in your application.

> For Angular users, you will get an error warning that this web component is unknown to the Angular compiler. This is resolved by modifying the module that declares your component to allow for custom web components.
>
> ```typescript
> import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
>
> @NgModule({
>   schemas: [CUSTOM_ELEMENTS_SCHEMA]
> })
> ```

Include this component in your HTML and assign it an ID so that you can easily query for that element reference later.

```html
<capacitor-google-map id="map"></capacitor-google-map>
```

> On Android, the map is rendered beneath the entire webview, and uses this component to manage its positioning during scrolling events. This means that as the developer, you _must_ ensure that the webview is transparent all the way through the layers to the very bottom. In a typically Ionic application, that means setting transparency on elements such as IonContent and the root HTML tag to ensure that it can be seen. If you can't see your map on Android, this should be the first thing you check.
>
> On iOS, we render the map directly into the webview and so the same transparency effects are not required. We are investigating alternate methods for Android still and hope to resolve this better in a future update.

The Google Map element itself comes unstyled, so you should style it to fit within the layout of your page structure. Because we're rendering a view into this slot, by itself the element has no width or height, so be sure to set those explicitly.

```css
capacitor-google-map {
  display: inline-block;
  width: 275px;
  height: 400px;
}
```

Next, we should create the map reference. This is done by importing the GoogleMap class from the Capacitor plugin and calling the create method, and passing in the required parameters.

```typescript
import { GoogleMap } from '@capacitor/google-maps';

const apiKey = 'YOUR_API_KEY_HERE';

const mapRef = document.getElementById('map');

const newMap = await GoogleMap.create({
  id: 'my-map', // Unique identifier for this map instance
  element: mapRef, // reference to the capacitor-google-map element
  apiKey: apiKey, // Your Google Maps API Key
  config: {
    center: {
      // The initial position to be rendered by the map
      lat: 33.6,
      lng: -117.9,
    },
    zoom: 8, // The initial zoom level to be rendered by the map
  },
});
```

At this point, your map should be created within your application. Using the returned reference to the map, you can easily interact with your map in a number of way, a few of which are shown here.

```typescript
const newMap = await GoogleMap.create({...});

// Add a marker to the map
const markerId = await newMap.addMarker({
  coordinate: {
    lat: 33.6,
    lng: -117.9
  }
});

// Move the map programmatically
await newMap.setCamera({
  coordinate: {
    lat: 33.6,
    lng: -117.9
  }
});

// Enable marker clustering
await newMap.enableClustering();

// Handle marker click
await newMap.setOnMarkerClickListener((event) => {...});

// Clean up map reference
await newMap.destroy();
```

## Full Examples

### Angular

```typescript
import { GoogleMap } from '@capacitor/google-maps';

@Component({
  template: `
    <capacitor-google-map #map></capacitor-google-map>
    <button (click)="createMap()">Create Map</button>
  `,
  styles: [
    `
      capacitor-google-map {
        display: inline-block;
        width: 275px;
        height: 400px;
      }
    `,
  ],
})
export class MyMap {
  @ViewChild('map')
  mapRef: ElementRef<HTMLElement>;
  newMap: GoogleMap;

  async createMap() {
    this.newMap = await GoogleMap.create({
      id: 'my-cool-map',
      element: this.mapRef.nativeElement,
      apiKey: environment.apiKey,
      config: {
        center: {
          lat: 33.6,
          lng: -117.9,
        },
        zoom: 8,
      },
    });
  }
}
```

### React

```jsx
import { GoogleMap } from '@capacitor/google-maps';
import { useRef } from 'react';

const MyMap: React.FC = () => {
  const mapRef = useRef<HTMLElement>();
  let newMap: GoogleMap;

  async function createMap() {
    if (!mapRef.current) return;

    newMap = await GoogleMap.create({
      id: 'my-cool-map',
      element: mapRef.current,
      apiKey: process.env.REACT_APP_YOUR_API_KEY_HERE,
      config: {
        center: {
          lat: 33.6,
          lng: -117.9
        },
        zoom: 8
      }
    })
  }

  return (
    <div className="component-wrapper">
      <capacitor-google-map ref={mapRef} style={{
        display: 'inline-block',
        width: 275,
        height: 400
      }}></capacitor-google-map>

      <button onClick={createMap}>Create Map</button>
    </div>
  )
}

export default MyMap;
```

### Javascript

```html
<capacitor-google-map id="map"></capacitor-google-map>
<button onclick="createMap()">Create Map</button>

<style>
  capacitor-google-map {
    display: inline-block;
    width: 275px;
    height: 400px;
  }
</style>

<script>
  import { GoogleMap } from '@capacitor/google-maps';

  const createMap = async () => {
    const mapRef = document.getElementById('map');

    const newMap = await GoogleMap.create({
      id: 'my-map', // Unique identifier for this map instance
      element: mapRef, // reference to the capacitor-google-map element
      apiKey: 'YOUR_API_KEY_HERE', // Your Google Maps API Key
      config: {
        center: {
          // The initial position to be rendered by the map
          lat: 33.6,
          lng: -117.9,
        },
        zoom: 8, // The initial zoom level to be rendered by the map
      },
    });
  };
</script>
```

## API

<docgen-index>

* [`create(...)`](#create)
* [`enableTouch()`](#enabletouch)
* [`disableTouch()`](#disabletouch)
* [`enableClustering(...)`](#enableclustering)
* [`disableClustering()`](#disableclustering)
* [`addMarker(...)`](#addmarker)
* [`addMarkers(...)`](#addmarkers)
* [`removeMarker(...)`](#removemarker)
* [`removeMarkers(...)`](#removemarkers)
* [`addPolygons(...)`](#addpolygons)
* [`removePolygons(...)`](#removepolygons)
* [`addCircles(...)`](#addcircles)
* [`removeCircles(...)`](#removecircles)
* [`addPolylines(...)`](#addpolylines)
* [`removePolylines(...)`](#removepolylines)
* [`destroy()`](#destroy)
* [`setCamera(...)`](#setcamera)
* [`getMapType()`](#getmaptype)
* [`setMapType(...)`](#setmaptype)
* [`enableIndoorMaps(...)`](#enableindoormaps)
* [`enableTrafficLayer(...)`](#enabletrafficlayer)
* [`enableAccessibilityElements(...)`](#enableaccessibilityelements)
* [`enableCurrentLocation(...)`](#enablecurrentlocation)
* [`setPadding(...)`](#setpadding)
* [`fitBounds(...)`](#fitbounds)
* [`setOnBoundsChangedListener(...)`](#setonboundschangedlistener)
* [`setOnCameraIdleListener(...)`](#setoncameraidlelistener)
* [`setOnCameraMoveStartedListener(...)`](#setoncameramovestartedlistener)
* [`setOnClusterClickListener(...)`](#setonclusterclicklistener)
* [`setOnClusterInfoWindowClickListener(...)`](#setonclusterinfowindowclicklistener)
* [`setOnInfoWindowClickListener(...)`](#setoninfowindowclicklistener)
* [`setOnMapClickListener(...)`](#setonmapclicklistener)
* [`setOnMarkerClickListener(...)`](#setonmarkerclicklistener)
* [`setOnPolygonClickListener(...)`](#setonpolygonclicklistener)
* [`setOnCircleClickListener(...)`](#setoncircleclicklistener)
* [`setOnPolylineClickListener(...)`](#setonpolylineclicklistener)
* [`setOnMarkerDragStartListener(...)`](#setonmarkerdragstartlistener)
* [`setOnMarkerDragListener(...)`](#setonmarkerdraglistener)
* [`setOnMarkerDragEndListener(...)`](#setonmarkerdragendlistener)
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
create(options: CreateMapArgs, callback?: MapListenerCallback<MapReadyCallbackData> | undefined) => Promise<GoogleMap>
```

| Param          | Type                                                                                                                                |
| -------------- | ----------------------------------------------------------------------------------------------------------------------------------- |
| **`options`**  | <code><a href="#createmapargs">CreateMapArgs</a></code>                                                                             |
| **`callback`** | <code><a href="#maplistenercallback">MapListenerCallback</a>&lt;<a href="#mapreadycallbackdata">MapReadyCallbackData</a>&gt;</code> |

**Returns:** <code>Promise&lt;GoogleMap&gt;</code>

--------------------


### enableTouch()

```typescript
enableTouch() => Promise<void>
```

--------------------


### disableTouch()

```typescript
disableTouch() => Promise<void>
```

--------------------


### enableClustering(...)

```typescript
enableClustering(minClusterSize?: number | undefined) => Promise<void>
```

| Param                | Type                | Description                                                                             |
| -------------------- | ------------------- | --------------------------------------------------------------------------------------- |
| **`minClusterSize`** | <code>number</code> | The minimum number of markers that can be clustered together. The default is 4 markers. |

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


### addPolygons(...)

```typescript
addPolygons(polygons: Polygon[]) => Promise<string[]>
```

| Param          | Type                   |
| -------------- | ---------------------- |
| **`polygons`** | <code>Polygon[]</code> |

**Returns:** <code>Promise&lt;string[]&gt;</code>

--------------------


### removePolygons(...)

```typescript
removePolygons(ids: string[]) => Promise<void>
```

| Param     | Type                  |
| --------- | --------------------- |
| **`ids`** | <code>string[]</code> |

--------------------


### addCircles(...)

```typescript
addCircles(circles: Circle[]) => Promise<string[]>
```

| Param         | Type                  |
| ------------- | --------------------- |
| **`circles`** | <code>Circle[]</code> |

**Returns:** <code>Promise&lt;string[]&gt;</code>

--------------------


### removeCircles(...)

```typescript
removeCircles(ids: string[]) => Promise<void>
```

| Param     | Type                  |
| --------- | --------------------- |
| **`ids`** | <code>string[]</code> |

--------------------


### addPolylines(...)

```typescript
addPolylines(polylines: Polyline[]) => Promise<string[]>
```

| Param           | Type                    |
| --------------- | ----------------------- |
| **`polylines`** | <code>Polyline[]</code> |

**Returns:** <code>Promise&lt;string[]&gt;</code>

--------------------


### removePolylines(...)

```typescript
removePolylines(ids: string[]) => Promise<void>
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


### getMapType()

```typescript
getMapType() => Promise<MapType>
```

Get current map type

**Returns:** <code>Promise&lt;<a href="#maptype">MapType</a>&gt;</code>

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


### fitBounds(...)

```typescript
fitBounds(bounds: LatLngBounds, padding?: number | undefined) => Promise<void>
```

Sets the map viewport to contain the given bounds.

| Param         | Type                      | Description                                                                                                               |
| ------------- | ------------------------- | ------------------------------------------------------------------------------------------------------------------------- |
| **`bounds`**  | <code>LatLngBounds</code> | The bounds to fit in the viewport.                                                                                        |
| **`padding`** | <code>number</code>       | Optional padding to apply in pixels. The bounds will be fit in the part of the map that remains after padding is removed. |

--------------------


### setOnBoundsChangedListener(...)

```typescript
setOnBoundsChangedListener(callback?: MapListenerCallback<CameraIdleCallbackData> | undefined) => Promise<void>
```

| Param          | Type                                                                                                                                    |
| -------------- | --------------------------------------------------------------------------------------------------------------------------------------- |
| **`callback`** | <code><a href="#maplistenercallback">MapListenerCallback</a>&lt;<a href="#cameraidlecallbackdata">CameraIdleCallbackData</a>&gt;</code> |

--------------------


### setOnCameraIdleListener(...)

```typescript
setOnCameraIdleListener(callback?: MapListenerCallback<CameraIdleCallbackData> | undefined) => Promise<void>
```

| Param          | Type                                                                                                                                    |
| -------------- | --------------------------------------------------------------------------------------------------------------------------------------- |
| **`callback`** | <code><a href="#maplistenercallback">MapListenerCallback</a>&lt;<a href="#cameraidlecallbackdata">CameraIdleCallbackData</a>&gt;</code> |

--------------------


### setOnCameraMoveStartedListener(...)

```typescript
setOnCameraMoveStartedListener(callback?: MapListenerCallback<CameraMoveStartedCallbackData> | undefined) => Promise<void>
```

| Param          | Type                                                                                                                                                  |
| -------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------- |
| **`callback`** | <code><a href="#maplistenercallback">MapListenerCallback</a>&lt;<a href="#cameramovestartedcallbackdata">CameraMoveStartedCallbackData</a>&gt;</code> |

--------------------


### setOnClusterClickListener(...)

```typescript
setOnClusterClickListener(callback?: MapListenerCallback<ClusterClickCallbackData> | undefined) => Promise<void>
```

| Param          | Type                                                                                                                                        |
| -------------- | ------------------------------------------------------------------------------------------------------------------------------------------- |
| **`callback`** | <code><a href="#maplistenercallback">MapListenerCallback</a>&lt;<a href="#clusterclickcallbackdata">ClusterClickCallbackData</a>&gt;</code> |

--------------------


### setOnClusterInfoWindowClickListener(...)

```typescript
setOnClusterInfoWindowClickListener(callback?: MapListenerCallback<ClusterClickCallbackData> | undefined) => Promise<void>
```

| Param          | Type                                                                                                                                        |
| -------------- | ------------------------------------------------------------------------------------------------------------------------------------------- |
| **`callback`** | <code><a href="#maplistenercallback">MapListenerCallback</a>&lt;<a href="#clusterclickcallbackdata">ClusterClickCallbackData</a>&gt;</code> |

--------------------


### setOnInfoWindowClickListener(...)

```typescript
setOnInfoWindowClickListener(callback?: MapListenerCallback<MarkerClickCallbackData> | undefined) => Promise<void>
```

| Param          | Type                                                                                                                                      |
| -------------- | ----------------------------------------------------------------------------------------------------------------------------------------- |
| **`callback`** | <code><a href="#maplistenercallback">MapListenerCallback</a>&lt;<a href="#markerclickcallbackdata">MarkerClickCallbackData</a>&gt;</code> |

--------------------


### setOnMapClickListener(...)

```typescript
setOnMapClickListener(callback?: MapListenerCallback<MapClickCallbackData> | undefined) => Promise<void>
```

| Param          | Type                                                                                                                                |
| -------------- | ----------------------------------------------------------------------------------------------------------------------------------- |
| **`callback`** | <code><a href="#maplistenercallback">MapListenerCallback</a>&lt;<a href="#mapclickcallbackdata">MapClickCallbackData</a>&gt;</code> |

--------------------


### setOnMarkerClickListener(...)

```typescript
setOnMarkerClickListener(callback?: MapListenerCallback<MarkerClickCallbackData> | undefined) => Promise<void>
```

| Param          | Type                                                                                                                                      |
| -------------- | ----------------------------------------------------------------------------------------------------------------------------------------- |
| **`callback`** | <code><a href="#maplistenercallback">MapListenerCallback</a>&lt;<a href="#markerclickcallbackdata">MarkerClickCallbackData</a>&gt;</code> |

--------------------


### setOnPolygonClickListener(...)

```typescript
setOnPolygonClickListener(callback?: MapListenerCallback<PolygonClickCallbackData> | undefined) => Promise<void>
```

| Param          | Type                                                                                                                                        |
| -------------- | ------------------------------------------------------------------------------------------------------------------------------------------- |
| **`callback`** | <code><a href="#maplistenercallback">MapListenerCallback</a>&lt;<a href="#polygonclickcallbackdata">PolygonClickCallbackData</a>&gt;</code> |

--------------------


### setOnCircleClickListener(...)

```typescript
setOnCircleClickListener(callback?: MapListenerCallback<CircleClickCallbackData> | undefined) => Promise<void>
```

| Param          | Type                                                                                                                                      |
| -------------- | ----------------------------------------------------------------------------------------------------------------------------------------- |
| **`callback`** | <code><a href="#maplistenercallback">MapListenerCallback</a>&lt;<a href="#circleclickcallbackdata">CircleClickCallbackData</a>&gt;</code> |

--------------------


### setOnPolylineClickListener(...)

```typescript
setOnPolylineClickListener(callback?: MapListenerCallback<PolylineCallbackData> | undefined) => Promise<void>
```

| Param          | Type                                                                                                                                |
| -------------- | ----------------------------------------------------------------------------------------------------------------------------------- |
| **`callback`** | <code><a href="#maplistenercallback">MapListenerCallback</a>&lt;<a href="#polylinecallbackdata">PolylineCallbackData</a>&gt;</code> |

--------------------


### setOnMarkerDragStartListener(...)

```typescript
setOnMarkerDragStartListener(callback?: MapListenerCallback<MarkerClickCallbackData> | undefined) => Promise<void>
```

| Param          | Type                                                                                                                                      |
| -------------- | ----------------------------------------------------------------------------------------------------------------------------------------- |
| **`callback`** | <code><a href="#maplistenercallback">MapListenerCallback</a>&lt;<a href="#markerclickcallbackdata">MarkerClickCallbackData</a>&gt;</code> |

--------------------


### setOnMarkerDragListener(...)

```typescript
setOnMarkerDragListener(callback?: MapListenerCallback<MarkerClickCallbackData> | undefined) => Promise<void>
```

| Param          | Type                                                                                                                                      |
| -------------- | ----------------------------------------------------------------------------------------------------------------------------------------- |
| **`callback`** | <code><a href="#maplistenercallback">MapListenerCallback</a>&lt;<a href="#markerclickcallbackdata">MarkerClickCallbackData</a>&gt;</code> |

--------------------


### setOnMarkerDragEndListener(...)

```typescript
setOnMarkerDragEndListener(callback?: MapListenerCallback<MarkerClickCallbackData> | undefined) => Promise<void>
```

| Param          | Type                                                                                                                                      |
| -------------- | ----------------------------------------------------------------------------------------------------------------------------------------- |
| **`callback`** | <code><a href="#maplistenercallback">MapListenerCallback</a>&lt;<a href="#markerclickcallbackdata">MarkerClickCallbackData</a>&gt;</code> |

--------------------


### setOnMyLocationButtonClickListener(...)

```typescript
setOnMyLocationButtonClickListener(callback?: MapListenerCallback<MyLocationButtonClickCallbackData> | undefined) => Promise<void>
```

| Param          | Type                                                                                                                                                          |
| -------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **`callback`** | <code><a href="#maplistenercallback">MapListenerCallback</a>&lt;<a href="#mylocationbuttonclickcallbackdata">MyLocationButtonClickCallbackData</a>&gt;</code> |

--------------------


### setOnMyLocationClickListener(...)

```typescript
setOnMyLocationClickListener(callback?: MapListenerCallback<MapClickCallbackData> | undefined) => Promise<void>
```

| Param          | Type                                                                                                                                |
| -------------- | ----------------------------------------------------------------------------------------------------------------------------------- |
| **`callback`** | <code><a href="#maplistenercallback">MapListenerCallback</a>&lt;<a href="#mapclickcallbackdata">MapClickCallbackData</a>&gt;</code> |

--------------------


### Interfaces


#### CreateMapArgs

An interface containing the options used when creating a map.

| Prop              | Type                                                        | Description                                                                                                                                                                            | Default            |
| ----------------- | ----------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------ |
| **`id`**          | <code>string</code>                                         | A unique identifier for the map instance.                                                                                                                                              |                    |
| **`apiKey`**      | <code>string</code>                                         | The Google Maps SDK API Key.                                                                                                                                                           |                    |
| **`config`**      | <code><a href="#googlemapconfig">GoogleMapConfig</a></code> | The initial configuration settings for the map.                                                                                                                                        |                    |
| **`element`**     | <code>HTMLElement</code>                                    | The DOM element that the Google Map View will be mounted on which determines size and positioning.                                                                                     |                    |
| **`forceCreate`** | <code>boolean</code>                                        | Destroy and re-create the map instance if a map with the supplied id already exists                                                                                                    | <code>false</code> |
| **`region`**      | <code>string</code>                                         | The region parameter alters your application to serve different map tiles or bias the application (such as biasing geocoding results towards the region). Only available for web.      |                    |
| **`language`**    | <code>string</code>                                         | The language parameter affects the names of controls, copyright notices, driving directions, and control labels, as well as the responses to service requests. Only available for web. |                    |


#### GoogleMapConfig

For web, all the javascript Google Maps options are available as
GoogleMapConfig extends google.maps.MapOptions.
For iOS and Android only the config options declared on <a href="#googlemapconfig">GoogleMapConfig</a> are available.

| Prop                   | Type                                      | Description                                                                                                                                               | Default            | Since |
| ---------------------- | ----------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------ | ----- |
| **`width`**            | <code>number</code>                       | Override width for native map.                                                                                                                            |                    |       |
| **`height`**           | <code>number</code>                       | Override height for native map.                                                                                                                           |                    |       |
| **`x`**                | <code>number</code>                       | Override absolute x coordinate position for native map.                                                                                                   |                    |       |
| **`y`**                | <code>number</code>                       | Override absolute y coordinate position for native map.                                                                                                   |                    |       |
| **`center`**           | <code><a href="#latlng">LatLng</a></code> | Default location on the Earth towards which the camera points.                                                                                            |                    |       |
| **`zoom`**             | <code>number</code>                       | Sets the zoom of the map.                                                                                                                                 |                    |       |
| **`androidLiteMode`**  | <code>boolean</code>                      | Enables image-based lite mode on Android.                                                                                                                 | <code>false</code> |       |
| **`devicePixelRatio`** | <code>number</code>                       | Override pixel ratio for native map.                                                                                                                      |                    |       |
| **`styles`**           | <code>MapTypeStyle[] \| null</code>       | Styles to apply to each of the default map types. Note that for satellite, hybrid and terrain modes, these styles will only apply to labels and geometry. |                    | 4.3.0 |
| **`mapId`**            | <code>string</code>                       | A map id associated with a specific map style or feature. [Use Map IDs](https://developers.google.com/maps/documentation/get-map-id) Only for Web.        |                    | 6.0.0 |
| **`androidMapId`**     | <code>string</code>                       | A map id associated with a specific map style or feature. [Use Map IDs](https://developers.google.com/maps/documentation/get-map-id) Only for Android.    |                    | 6.0.0 |
| **`iOSMapId`**         | <code>string</code>                       | A map id associated with a specific map style or feature. [Use Map IDs](https://developers.google.com/maps/documentation/get-map-id) Only for iOS.        |                    | 6.0.0 |


#### LatLng

An interface representing a pair of latitude and longitude coordinates.

| Prop      | Type                | Description                                                               |
| --------- | ------------------- | ------------------------------------------------------------------------- |
| **`lat`** | <code>number</code> | Coordinate latitude, in degrees. This value is in the range [-90, 90].    |
| **`lng`** | <code>number</code> | Coordinate longitude, in degrees. This value is in the range [-180, 180]. |


#### MapReadyCallbackData

| Prop        | Type                |
| ----------- | ------------------- |
| **`mapId`** | <code>string</code> |


#### Marker

A marker is an icon placed at a particular point on the map's surface.

| Prop             | Type                                                         | Description                                                                                                                                                                               | Default            | Since |
| ---------------- | ------------------------------------------------------------ | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------ | ----- |
| **`coordinate`** | <code><a href="#latlng">LatLng</a></code>                    | <a href="#marker">Marker</a> position                                                                                                                                                     |                    |       |
| **`opacity`**    | <code>number</code>                                          | Sets the opacity of the marker, between 0 (completely transparent) and 1 inclusive.                                                                                                       | <code>1</code>     |       |
| **`title`**      | <code>string</code>                                          | Title, a short description of the overlay.                                                                                                                                                |                    |       |
| **`snippet`**    | <code>string</code>                                          | Snippet text, shown beneath the title in the info window when selected.                                                                                                                   |                    |       |
| **`isFlat`**     | <code>boolean</code>                                         | Controls whether this marker should be flat against the Earth's surface or a billboard facing the camera.                                                                                 | <code>false</code> |       |
| **`iconUrl`**    | <code>string</code>                                          | Path to a marker icon to render. It can be relative to the web app public directory, or a https url of a remote marker icon. **SVGs are not supported on native platforms.**              |                    | 4.2.0 |
| **`iconSize`**   | <code><a href="#size">Size</a></code>                        | Controls the scaled size of the marker image set in `iconUrl`.                                                                                                                            |                    | 4.2.0 |
| **`iconOrigin`** | <code><a href="#point">Point</a></code>                      | The position of the image within a sprite, if any. By default, the origin is located at the top left corner of the image .                                                                |                    | 4.2.0 |
| **`iconAnchor`** | <code><a href="#point">Point</a></code>                      | The position at which to anchor an image in correspondence to the location of the marker on the map. By default, the anchor is located along the center point of the bottom of the image. |                    | 4.2.0 |
| **`tintColor`**  | <code>{ r: number; g: number; b: number; a: number; }</code> | Customizes the color of the default marker image. Each value must be between 0 and 255. Only for iOS and Android.                                                                         |                    | 4.2.0 |
| **`draggable`**  | <code>boolean</code>                                         | Controls whether this marker can be dragged interactively                                                                                                                                 | <code>false</code> |       |
| **`zIndex`**     | <code>number</code>                                          | Specifies the stack order of this marker, relative to other markers on the map. A marker with a high z-index is drawn on top of markers with lower z-indexes                              | <code>0</code>     |       |


#### Size

| Prop         | Type                |
| ------------ | ------------------- |
| **`width`**  | <code>number</code> |
| **`height`** | <code>number</code> |


#### Point

<a href="#point">Point</a> geometry object.
https://tools.ietf.org/html/rfc7946#section-3.1.2

| Prop              | Type                                          | Description                           |
| ----------------- | --------------------------------------------- | ------------------------------------- |
| **`type`**        | <code>'<a href="#point">Point</a>'</code>     | Specifies the type of GeoJSON object. |
| **`coordinates`** | <code><a href="#position">Position</a></code> |                                       |


#### Polygon

<a href="#polygon">Polygon</a> geometry object.
https://tools.ietf.org/html/rfc7946#section-3.1.6

| Prop              | Type                                          | Description                           |
| ----------------- | --------------------------------------------- | ------------------------------------- |
| **`type`**        | <code>'<a href="#polygon">Polygon</a>'</code> | Specifies the type of GeoJSON object. |
| **`coordinates`** | <code>Position[][]</code>                     |                                       |


#### Circle

For web, all the javascript <a href="#circle">Circle</a> options are available as
Polygon extends google.maps.CircleOptions.
For iOS and Android only the config options declared on <a href="#circle">Circle</a> are available.

| Prop               | Type                 | Description                                                                                                                                                                            |
| ------------------ | -------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **`fillColor`**    | <code>string</code>  | The fill color. All CSS3 colors are supported except for extended named colors.                                                                                                        |
| **`fillOpacity`**  | <code>number</code>  | The fill opacity between 0.0 and 1.0.                                                                                                                                                  |
| **`strokeColor`**  | <code>string</code>  | The stroke color. All CSS3 colors are supported except for extended named colors.                                                                                                      |
| **`strokeWeight`** | <code>number</code>  | The stroke width in pixels.                                                                                                                                                            |
| **`geodesic`**     | <code>boolean</code> |                                                                                                                                                                                        |
| **`clickable`**    | <code>boolean</code> | Indicates whether this &lt;code&gt;<a href="#circle">Circle</a>&lt;/code&gt; handles mouse events.                                                                                     |
| **`title`**        | <code>string</code>  | Title, a short description of the overlay. Some overlays, such as markers, will display the title on the map. The title is also the default accessibility text. Only available on iOS. |
| **`tag`**          | <code>string</code>  |                                                                                                                                                                                        |


#### Polyline

For web, all the javascript <a href="#polyline">Polyline</a> options are available as
Polyline extends google.maps.PolylineOptions.
For iOS and Android only the config options declared on <a href="#polyline">Polyline</a> are available.

| Prop                | Type                     | Description                                                                                                                                                                                                                                                                                                                                                                                    |
| ------------------- | ------------------------ | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **`strokeColor`**   | <code>string</code>      | The stroke color. All CSS3 colors are supported except for extended named colors.                                                                                                                                                                                                                                                                                                              |
| **`strokeOpacity`** | <code>number</code>      | The stroke opacity between 0.0 and 1.0.                                                                                                                                                                                                                                                                                                                                                        |
| **`strokeWeight`**  | <code>number</code>      | The stroke width in pixels.                                                                                                                                                                                                                                                                                                                                                                    |
| **`geodesic`**      | <code>boolean</code>     | When &lt;code&gt;true&lt;/code&gt;, edges of the polygon are interpreted as geodesic and will follow the curvature of the Earth. When &lt;code&gt;false&lt;/code&gt;, edges of the polygon are rendered as straight lines in screen space. Note that the shape of a geodesic polygon may appear to change when dragged, as the dimensions are maintained relative to the surface of the earth. |
| **`clickable`**     | <code>boolean</code>     | Indicates whether this &lt;code&gt;<a href="#polyline">Polyline</a>&lt;/code&gt; handles mouse events.                                                                                                                                                                                                                                                                                         |
| **`tag`**           | <code>string</code>      |                                                                                                                                                                                                                                                                                                                                                                                                |
| **`styleSpans`**    | <code>StyleSpan[]</code> | Used to specify the color of one or more segments of a polyline. The styleSpans property is an array of <a href="#stylespan">StyleSpan</a> objects. Setting the spans property is the preferred way to change the color of a polyline. Only on iOS and Android.                                                                                                                                |


#### StyleSpan

Describes the style for some region of a polyline.

| Prop           | Type                | Description                                                                       |
| -------------- | ------------------- | --------------------------------------------------------------------------------- |
| **`color`**    | <code>string</code> | The stroke color. All CSS3 colors are supported except for extended named colors. |
| **`segments`** | <code>number</code> | The length of this span in number of segments.                                    |


#### CameraConfig

Configuration properties for a Google Map Camera

| Prop                    | Type                                      | Description                                                                                                            | Default            |
| ----------------------- | ----------------------------------------- | ---------------------------------------------------------------------------------------------------------------------- | ------------------ |
| **`coordinate`**        | <code><a href="#latlng">LatLng</a></code> | Location on the Earth towards which the camera points.                                                                 |                    |
| **`zoom`**              | <code>number</code>                       | Sets the zoom of the map.                                                                                              |                    |
| **`bearing`**           | <code>number</code>                       | Bearing of the camera, in degrees clockwise from true north.                                                           | <code>0</code>     |
| **`angle`**             | <code>number</code>                       | The angle, in degrees, of the camera from the nadir (directly facing the Earth). The only allowed values are 0 and 45. | <code>0</code>     |
| **`animate`**           | <code>boolean</code>                      | Animate the transition to the new Camera properties.                                                                   | <code>false</code> |
| **`animationDuration`** | <code>number</code>                       | This configuration option is not being used.                                                                           |                    |


#### MapPadding

Controls for setting padding on the 'visible' region of the view.

| Prop         | Type                |
| ------------ | ------------------- |
| **`top`**    | <code>number</code> |
| **`left`**   | <code>number</code> |
| **`right`**  | <code>number</code> |
| **`bottom`** | <code>number</code> |


#### CameraIdleCallbackData

| Prop            | Type                      |
| --------------- | ------------------------- |
| **`mapId`**     | <code>string</code>       |
| **`bounds`**    | <code>LatLngBounds</code> |
| **`bearing`**   | <code>number</code>       |
| **`latitude`**  | <code>number</code>       |
| **`longitude`** | <code>number</code>       |
| **`tilt`**      | <code>number</code>       |
| **`zoom`**      | <code>number</code>       |


#### CameraMoveStartedCallbackData

| Prop            | Type                 |
| --------------- | -------------------- |
| **`mapId`**     | <code>string</code>  |
| **`isGesture`** | <code>boolean</code> |


#### ClusterClickCallbackData

| Prop            | Type                              |
| --------------- | --------------------------------- |
| **`mapId`**     | <code>string</code>               |
| **`latitude`**  | <code>number</code>               |
| **`longitude`** | <code>number</code>               |
| **`size`**      | <code>number</code>               |
| **`items`**     | <code>MarkerCallbackData[]</code> |


#### MarkerCallbackData

| Prop            | Type                |
| --------------- | ------------------- |
| **`markerId`**  | <code>string</code> |
| **`latitude`**  | <code>number</code> |
| **`longitude`** | <code>number</code> |
| **`title`**     | <code>string</code> |
| **`snippet`**   | <code>string</code> |


#### MarkerClickCallbackData

| Prop        | Type                |
| ----------- | ------------------- |
| **`mapId`** | <code>string</code> |


#### MapClickCallbackData

| Prop            | Type                |
| --------------- | ------------------- |
| **`mapId`**     | <code>string</code> |
| **`latitude`**  | <code>number</code> |
| **`longitude`** | <code>number</code> |


#### PolygonClickCallbackData

| Prop            | Type                |
| --------------- | ------------------- |
| **`mapId`**     | <code>string</code> |
| **`polygonId`** | <code>string</code> |
| **`tag`**       | <code>string</code> |


#### CircleClickCallbackData

| Prop           | Type                |
| -------------- | ------------------- |
| **`mapId`**    | <code>string</code> |
| **`circleId`** | <code>string</code> |
| **`tag`**      | <code>string</code> |


#### PolylineCallbackData

| Prop             | Type                |
| ---------------- | ------------------- |
| **`polylineId`** | <code>string</code> |
| **`tag`**        | <code>string</code> |


#### MyLocationButtonClickCallbackData

| Prop        | Type                |
| ----------- | ------------------- |
| **`mapId`** | <code>string</code> |


### Type Aliases


#### MapListenerCallback

The callback function to be called when map events are emitted.

<code>(data: T): void</code>


#### Position

A <a href="#position">Position</a> is an array of coordinates.
https://tools.ietf.org/html/rfc7946#section-3.1.1
Array should contain between two and three elements.
The previous GeoJSON specification allowed more elements (e.g., which could be used to represent M values),
but the current specification only allows X, Y, and (optionally) Z to be defined.

<code>number[]</code>


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
