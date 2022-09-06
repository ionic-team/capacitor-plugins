import { useState } from 'react';
import { GoogleMap, Marker } from '@capacitor/google-maps';
import { IonButton, IonTextarea } from '@ionic/react';

import BaseTestingPage from '../../components/BaseTestingPage';

const MarkerCustomizations: React.FC = () => {
  const [map, setMap] = useState<GoogleMap | null>(null);
  const [markerIds, setMarkerIds] = useState<string[]>([]);
  const [commandOutput, setCommandOutput] = useState('');

  const apiKey = process.env.REACT_APP_GOOGLE_MAPS_API_KEY;

  const getRandom = (): number => {
    return Math.floor(Math.random() * 256);
  };

  async function createMap() {
    try {
      const element = document.getElementById('map_marker_custom');
      if (element !== null) {
        const newMap = await GoogleMap.create({
          element: element,
          id: 'test-map',
          apiKey: apiKey!,
          config: {
            center: {
              lat: 43.547302,
              lng: -96.728333,
            },
            zoom: 9,
          },
        });

        setMap(newMap);

        setCommandOutput('Map created');
      }
    } catch (err: any) {
      setCommandOutput(err.message);
    }
  }

  const addMarkerColor = async () => {
    try {
      if (!map) {
        throw new Error('map not created');
      }

      const id = await map.addMarker({
        coordinate: {
          lat: 43.581386,
          lng: -96.739025,
        },
        tintColor: {
          r: 41,
          g: 71,
          b: 157,
          a: 1,
        },
      });

      map.setCamera({
        coordinate: {
          lat: 43.547302,
          lng: -96.728333,
        },
        zoom: 9,
      });
      setMarkerIds(markerIds.concat([id]));
      setCommandOutput('1 color marker added');
    } catch (err: any) {
      setCommandOutput(err.message);
    }
  };

  const addMarkerImage = async () => {
    try {
      if (!map) {
        throw new Error('map not created');
      }

      const id = await map.addMarker({
        coordinate: {
          lat: 43.512098,
          lng: -96.739352,
        },
        iconUrl: 'assets/icon/pin.png',
        iconSize: {
          width: 30,
          height: 30,
        },
        iconOrigin: { x: 0, y: 0 },
        iconAnchor: { x: 15, y: 30 },
      });

      map.setCamera({
        coordinate: {
          lat: 43.547302,
          lng: -96.728333,
        },
        zoom: 9,
      });

      setMarkerIds(markerIds.concat([id]));
      setCommandOutput('1 image marker added');
    } catch (err: any) {
      setCommandOutput(err.message);
    }
  };

  const addMarkerSVG = async () => {
    try {
      if (!map) {
        throw new Error('map not created');
      }

      const id = await map.addMarker({
        coordinate: {
          lat: 43.512098,
          lng: -96.739352,
        },
        iconUrl: 'assets/icon/marker.svg',
        iconSize: {
          width: 30,
          height: 30,
        },
        iconOrigin: { x: 0, y: 0 },
        iconAnchor: { x: 15, y: 30 },
      });

      map.setCamera({
        coordinate: {
          lat: 43.547302,
          lng: -96.728333,
        },
        zoom: 9,
      });

      setMarkerIds(markerIds.concat([id]));
      setCommandOutput('1 image marker added');
    } catch (err: any) {
      setCommandOutput(err.message);
    }
  };

  const addMultipleImageMarkers = async () => {
    try {
      if (!map) {
        throw new Error('map not created');
      }
      const markers: Marker[] = [
        {
          coordinate: {
            lat: 47.6,
            lng: -122.33,
          },
          title: 'Title 1',
          snippet: 'Snippet 1',
          iconUrl: 'assets/icon/pin.png',
          iconSize: {
            width: 30,
            height: 30,
          },
        },
        {
          coordinate: {
            lat: 47.6,
            lng: -122.46,
          },
          title: 'Title 2',
          snippet: 'Snippet 2',
          iconUrl: 'assets/icon/pin.png',
          iconSize: {
            width: 30,
            height: 30,
          },
        },
        {
          coordinate: {
            lat: 47.3,
            lng: -122.46,
          },
          title: 'Title 3',
          snippet: 'Snippet 3',
          iconUrl: 'assets/icon/pin.png',
          iconSize: {
            width: 30,
            height: 30,
          },
        },
        {
          coordinate: {
            lat: 47.2,
            lng: -122.23,
          },
          title: 'Title 4',
          snippet: 'Snippet 4',
          iconUrl: 'assets/icon/pin.png',
          iconSize: {
            width: 30,
            height: 30,
          },
        },
      ];

      const ids = await map.addMarkers(markers);
      map.setCamera({
        animate: true,
        coordinate: {
          lat: 47.6,
          lng: -122.33,
        },
        zoom: 7,
      });
      setMarkerIds(markerIds.concat(ids));
      setCommandOutput(`${ids.length} image markers added`);
    } catch (err: any) {
      setCommandOutput(err.message);
    }
  };

  const addMultipleColorMarkers = async () => {
    try {
      if (!map) {
        throw new Error('map not created');
      }
      const markers: Marker[] = [
        {
          coordinate: {
            lat: 47.6,
            lng: -122.33,
          },
          title: 'Title 1',
          snippet: 'Snippet 1',
          tintColor: {
            r: getRandom(),
            g: getRandom(),
            b: getRandom(),
            a: 1,
          },
        },
        {
          coordinate: {
            lat: 47.6,
            lng: -122.46,
          },
          title: 'Title 2',
          snippet: 'Snippet 2',
          tintColor: {
            r: getRandom(),
            g: getRandom(),
            b: getRandom(),
            a: 1,
          },
        },
        {
          coordinate: {
            lat: 47.3,
            lng: -122.46,
          },
          title: 'Title 3',
          snippet: 'Snippet 3',
          tintColor: {
            r: getRandom(),
            g: getRandom(),
            b: getRandom(),
            a: 1,
          },
        },
        {
          coordinate: {
            lat: 47.2,
            lng: -122.23,
          },
          title: 'Title 4',
          snippet: 'Snippet 4',
          tintColor: {
            r: getRandom(),
            g: getRandom(),
            b: getRandom(),
            a: 1,
          },
        },
      ];

      const ids = await map.addMarkers(markers);
      map.setCamera({
        animate: true,
        coordinate: {
          lat: 47.6,
          lng: -122.33,
        },
        zoom: 7,
      });
      setMarkerIds(markerIds.concat(ids));
      setCommandOutput(`${ids.length} color markers added`);
    } catch (err: any) {
      setCommandOutput(err.message);
    }
  };

  const removeAllMarkers = async () => {
    try {
      if (!map) {
        throw new Error('map not created');
      }

      let count = markerIds.length;

      await map.removeMarkers(markerIds);
      setMarkerIds([]);

      setCommandOutput(`${count} markers removed`);
    } catch (err: any) {
      setCommandOutput(err.message);
    }
  };

  async function destroyMap() {
    setCommandOutput('');
    try {
      if (map) {
        await map.destroy();
        setCommandOutput('Map destroyed');
      }
    } catch (err: any) {
      setCommandOutput(err.message);
    }
  }

  return (
    <BaseTestingPage pageTitle="Marker Customization">
      <div>
        <IonButton id="createMapButton" onClick={createMap} expand="block">
          Create Map
        </IonButton>
        <IonButton id="addMarkerImageButton" onClick={addMarkerImage} expand="block">
          Add Marker with Image
        </IonButton>
        <IonButton id="addMarkerSVGButton" onClick={addMarkerSVG} expand="block">
          Add Marker with SVG
        </IonButton>
        <IonButton id="addMarkerColorButton" onClick={addMarkerColor} expand="block">
          Add Marker with Color
        </IonButton>
        <IonButton
          id="addMultipleImageMarkersButton"
          onClick={addMultipleImageMarkers}
          expand="block"
        >
          Add Multiple Image Markers
        </IonButton>
        <IonButton
          id="addMultipleColorMarkersButton"
          onClick={addMultipleColorMarkers}
          expand="block"
        >
          Add Multiple Color Markers
        </IonButton>
        <IonButton
          id="removeAllMarkersButton"
          expand="block"
          onClick={removeAllMarkers}
        >
          Remove All Markers
        </IonButton>
        <IonButton id="destroyMapButton" expand="block" onClick={destroyMap}>
          Destroy Map
        </IonButton>
      </div>
      <div>
        <IonTextarea id="commandOutput" value={commandOutput}></IonTextarea>
      </div>
      <capacitor-google-map
        id="map_marker_custom"
        style={{
          position: 'absolute',
          top: window.innerHeight - 250,
          left: 0,
          width: window.innerWidth,
          height: 250,
        }}
      ></capacitor-google-map>
    </BaseTestingPage>
  );
};

export default MarkerCustomizations;
