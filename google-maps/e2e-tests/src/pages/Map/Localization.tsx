import { useState } from 'react';
import { GoogleMap } from '@capacitor/google-maps';
import { IonButton, IonTextarea } from '@ionic/react';
import BaseTestingPage from '../../components/BaseTestingPage';

const LocalizationPage: React.FC = () => {
  const [maps, setMaps] = useState<GoogleMap[]>([]);
  const [commandOutput, setCommandOutput] = useState('');
  const apiKey = process.env.REACT_APP_GOOGLE_MAPS_API_KEY;

  const onMapReady = (data: any) => {
    setCommandOutput(`MAP (${data.mapId}) IS READY`);
  };

  const onMapClick = (data: any) => {
    setCommandOutput(
      `MAP (${data.mapId}) CLICKED @ (${data.latitude}, ${data.longitude})`,
    );
  };

  const onMapBoundsChanged = (data: any) => {
    setCommandOutput(
      `MAP (${data.mapId}) BOUNDS CHANGED @ (${JSON.stringify(data.bounds)})`,
    );
  };

  async function createMaps() {
    setCommandOutput('');
    setMaps([]);
    try {
      const mapRef1 = document.getElementById('map1')!;
      const mapRef2 = document.getElementById('map2')!;

      const newMap1 = await GoogleMap.create(
        {
          element: mapRef1,
          id: 'test-map',
          apiKey: apiKey!,
          config: {
            center: {
              lat: 33.6,
              lng: -117.9,
            },
            zoom: 8,
          },
          forceCreate: true,
          region: 'JP',
          language: 'ja',
        },
        onMapReady,
      );

      const newMap2 = await GoogleMap.create(
        {
          element: mapRef2,
          id: 'test-map2',
          apiKey: apiKey!,
          config: {
            center: {
              lat: -33.6,
              lng: 117.9,
            },
            zoom: 6,
          },
          forceCreate: true,
        },
        onMapReady,
      );

      setMaps([newMap1, newMap2]);
      setCommandOutput('Maps created');
    } catch (err: any) {
      setCommandOutput(err.message);
    }
  }

  async function setOnMapClickListeners() {
    setCommandOutput('');
    try {
      if (maps) {
        for (let map of maps) {
          map.setOnMapClickListener(onMapClick);
        }
        setCommandOutput('Map Click Listeners Set');
      }
    } catch (err: any) {
      setCommandOutput(err.message);
    }
  }

  async function setOnMapBoundsChangedListeners() {
    setCommandOutput('');
    try {
      if (maps) {
        for (let map of maps) {
          map.setOnBoundsChangedListener(onMapBoundsChanged);
        }
        setCommandOutput('Map Bounds Changed Listeners Set');
      }
    } catch (err: any) {
      setCommandOutput(err.message);
    }
  }

  async function removeOnMapClickListeners() {
    setCommandOutput('');
    try {
      if (maps) {
        for (let map of maps) {
          map.removeAllMapListeners();
        }

        setCommandOutput('Map Click Listeners Destroyed');
      }
    } catch (err: any) {
      setCommandOutput(err.message);
    }
  }

  async function destroyMaps() {
    setCommandOutput('');
    try {
      if (maps) {
        for (let map of maps) {
          await map.destroy();
        }
        setMaps([]);
        setCommandOutput('Maps destroyed');
      }
    } catch (err: any) {
      setCommandOutput(err.message);
    }
  }

  async function getBounds() {
    setCommandOutput('');
    try {
      const bounds = await maps[0].getMapBounds();
      setCommandOutput(JSON.stringify(bounds));
    } catch (err: any) {
      setCommandOutput(err.message);
    }
  }

  return (
    <BaseTestingPage pageTitle="Map Localization">
      <div>
        <IonButton expand="block" id="createMapButton" onClick={createMaps}>
          Create Maps
        </IonButton>
        <IonButton
          expand="block"
          id="setOnMapClickButton"
          onClick={setOnMapClickListeners}
        >
          Set On Map Click Listeners
        </IonButton>
        <IonButton
          expand="block"
          id="setOnMapBoundsButton"
          onClick={setOnMapBoundsChangedListeners}
        >
          Set On Map Bounds Changed Listeners
        </IonButton>
        <IonButton expand="block" id="getMapBounds" onClick={getBounds}>
          Get Current Bounds
        </IonButton>
        <IonButton
          expand="block"
          id="removeOnMapClickButton"
          onClick={removeOnMapClickListeners}
        >
          Remove On Map Click Listeners
        </IonButton>
        <IonButton expand="block" id="destroyMapButton" onClick={destroyMaps}>
          Destroy Maps
        </IonButton>
      </div>
      <div>
        <IonTextarea id="commandOutput" value={commandOutput}></IonTextarea>
      </div>
      <capacitor-google-map
        id="map1"
        style={{
          position: 'absolute',
          top: window.innerHeight - window.outerWidth / 2,
          left: 0,
          width: window.outerWidth / 2,
          height: window.outerWidth / 2,
        }}
      ></capacitor-google-map>
      <capacitor-google-map
        id="map2"
        style={{
          position: 'absolute',
          top: window.innerHeight - window.outerWidth / 2,
          left: window.outerWidth / 2,
          width: window.outerWidth / 2,
          height: window.outerWidth / 2,
        }}
      ></capacitor-google-map>
    </BaseTestingPage>
  );
};

export default LocalizationPage;
