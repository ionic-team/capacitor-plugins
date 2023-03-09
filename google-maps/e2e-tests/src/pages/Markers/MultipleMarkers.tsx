import { useState } from 'react';
import { GoogleMap, Marker } from '@capacitor/google-maps';
import { IonButton, IonTextarea } from '@ionic/react';
import BaseTestingPage from '../../components/BaseTestingPage';

const MultipleMarkers: React.FC = () => {
  const [map, setMap] = useState<GoogleMap | null>(null);
  const [markerIds, setMarkerIds] = useState<string[]>([]);
  const [commandOutput, setCommandOutput] = useState('');
  const [commandOutput2, setCommandOutput2] = useState('');
  const apiKey = process.env.REACT_APP_GOOGLE_MAPS_API_KEY;

  const onBoundsChanged = (data: any) => {
    setCommandOutput(`BOUNDS CHANGED:  ${JSON.stringify(data)}`);
  };

  const onCameraIdle = (data: any) => {
    setCommandOutput(`CAMERA IDLE:  ${JSON.stringify(data)}`);
  };

  const onCameraMoveStarted = (data: any) => {
    setCommandOutput(`CAMERA MOVE STARTED:  ${JSON.stringify(data)}`);
  };

  const onClusterClick = (data: any) => {
    setCommandOutput2(`CLUSTER CLICKED:  ${JSON.stringify(data)}`);
  };

  const onClusterInfoWindowClick = (data: any) => {
    setCommandOutput2(`CLUSTER INFO WINDOW CLICKED:  ${JSON.stringify(data)}`);
  };

  const onInfoWindowClick = (data: any) => {
    setCommandOutput2(`INFO WINDOW CLICKED:  ${JSON.stringify(data)}`);
  };

  const onMapClick = (data: any) => {
    setCommandOutput(`MAP CLICKED:  ${JSON.stringify(data)}`);
    setCommandOutput2('');
  };

  const onMarkerClick = (data: any) => {
    setCommandOutput2(`MARKER CLICKED:  ${JSON.stringify(data)}`);
  };

  const onMyLocationButtonClick = (data: any) => {
    setCommandOutput2(`MY LOCATION BUTTON CLICKED:  ${JSON.stringify(data)}`);
  };

  const onMyLocationClick = (data: any) => {
    setCommandOutput2(`MY LOCATION CLICKED:  ${JSON.stringify(data)}`);
  };

  async function createMap() {
    try {
      const element = document.getElementById('multipleMarkers_map1');
      if (element !== null) {
        const newMap = await GoogleMap.create({
          element: element,
          id: 'test-map',
          apiKey: apiKey!,
          config: {
            center: {
              lat: 47.6,
              lng: -122.33,
            },
            zoom: 5,
          },
        });

        setMap(newMap);

        setCommandOutput('Map created');
        setCommandOutput2('');
      }
    } catch (err: any) {
      setCommandOutput(err.message);
      setCommandOutput2('');
    }
  }

  async function setEventListeners() {
    map?.setOnBoundsChangedListener(onBoundsChanged);
    map?.setOnCameraIdleListener(onCameraIdle);
    map?.setOnCameraMoveStartedListener(onCameraMoveStarted);
    map?.setOnClusterClickListener(onClusterClick);
    map?.setOnClusterInfoWindowClickListener(onClusterInfoWindowClick);
    map?.setOnInfoWindowClickListener(onInfoWindowClick);
    map?.setOnMapClickListener(onMapClick);
    map?.setOnMarkerClickListener(onMarkerClick);
    map?.setOnMyLocationButtonClickListener(onMyLocationButtonClick);
    map?.setOnMyLocationClickListener(onMyLocationClick);
    setCommandOutput('Set Event Listeners!');
    setCommandOutput2('');
  }

  async function removeEventListeners() {
    map?.removeAllMapListeners();
    setCommandOutput('Removed Event Listeners!');
    setCommandOutput2('');
  }

  async function enableClustering() {
    try {
      if (map) {
        await map.enableClustering(2);
        setCommandOutput('marker clustering enabled');
        setCommandOutput2('');
      }
    } catch (err: any) {
      setCommandOutput(err.message);
      setCommandOutput2('');
    }
  }

  async function addMultipleMarkers() {
    try {
      if (map) {
        const markers: Marker[] = [
          {
            coordinate: {
              lat: 47.6,
              lng: -122.33,
            },
            title: 'Title 1',
            snippet: 'Snippet 1',
          },
          {
            coordinate: {
              lat: 47.6,
              lng: -122.46,
            },
            title: 'Title 2',
            snippet: 'Snippet 2',
          },
          {
            coordinate: {
              lat: 47.3,
              lng: -122.46,
            },
            title: 'Title 3',
            snippet: 'Snippet 3',
          },
          {
            coordinate: {
              lat: 47.2,
              lng: -122.23,
            },
            title: 'Title 4',
            snippet: 'Snippet 4',
          },
        ];

        const ids = await map.addMarkers(markers);
        console.log('@@IDS: ', ids);
        setMarkerIds(ids);
        setCommandOutput(`${ids.length} markers added`);
        setCommandOutput2('');
      }
    } catch (err: any) {
      setCommandOutput(err.message);
      setCommandOutput2('');
    }
  }

  async function removeAllMarkers() {
    try {
      if (map) {
        await map.removeMarkers(markerIds);
        setCommandOutput(`${markerIds.length} markers removed`);
        setCommandOutput2('');
        setMarkerIds([]);
      }
    } catch (err: any) {
      setCommandOutput(err.message);
      setCommandOutput2('');
    }
  }

  async function disableClustering() {
    try {
      if (map) {
        await map.disableClustering();
        setCommandOutput('marker clustering disabled');
        setCommandOutput2('');
      }
    } catch (err: any) {
      setCommandOutput(err.message);
      setCommandOutput2('');
    }
  }

  async function destroyMap() {
    setCommandOutput('');
    try {
      if (map) {
        await map.destroy();
        setCommandOutput('Map destroyed');
        setCommandOutput2('');
      }
    } catch (err: any) {
      setCommandOutput(err.message);
      setCommandOutput2('');
    }
  }

  async function showCurrentLocation() {
    await map?.setCamera({
      zoom: 1,
      animate: true,
      animationDuration: 50,
    });
    await map?.enableCurrentLocation(true);
  }

  async function showCurrentBounds() {
    const bounds = await map?.getMapBounds();
    setCommandOutput(JSON.stringify(bounds));
  }

  return (
    <BaseTestingPage pageTitle="Multiple Markers">
      <div>
        <IonButton id="createMapButton" onClick={createMap}>
          Create Map
        </IonButton>
        <IonButton id="setOnMarkerClickButton" onClick={setEventListeners}>
          Set Event Listeners
        </IonButton>
        <IonButton
          id="removeOnMarkerClickButton"
          onClick={removeEventListeners}
        >
          Remove Event Listeners
        </IonButton>
        <IonButton id="showCurrentLocationButton" onClick={showCurrentLocation}>
          Show Current Location
        </IonButton>
        <IonButton id="showCurrentBoundsButton" onClick={showCurrentBounds}>
          Show Current Bounds
        </IonButton>
        <IonButton id="addMarkersButton" onClick={addMultipleMarkers}>
          Add Multiple Markers
        </IonButton>
        <IonButton id="enableClusteringButton" onClick={enableClustering}>
          Enable Clustering
        </IonButton>
        <IonButton id="disableClusteringButton" onClick={disableClustering}>
          Disable Clustering
        </IonButton>
        <IonButton id="removeMarkersButton" onClick={removeAllMarkers}>
          Remove Markers
        </IonButton>
        <IonButton id="destroyMapButton" onClick={destroyMap}>
          Destroy Map
        </IonButton>
      </div>
      <div>
        <IonTextarea id="commandOutput" value={commandOutput}></IonTextarea>
        <IonTextarea id="commandOutput2" value={commandOutput2}></IonTextarea>
      </div>
      <capacitor-google-map
        id="multipleMarkers_map1"
        style={{
          position: 'absolute',
          top: window.innerHeight - 150,
          left: 0,
          width: window.innerWidth,
          height: 150,
        }}
      ></capacitor-google-map>
    </BaseTestingPage>
  );
};

export default MultipleMarkers;
