import { useState } from 'react';
import { GoogleMap } from '@capacitor/google-maps';
import {
  IonButton,
  IonTextarea,
} from '@ionic/react';
import BaseTestingPage from '../../components/BaseTestingPage';

const ResizeMapPage: React.FC = () => {
  const [map, setMap] = useState<GoogleMap | null>();
  const [commandOutput, setCommandOutput] = useState('');
  const [mapWidth, setMapWidth] = useState<number>(window.outerWidth);
  const [mapHeight, setMapHeight] = useState<number>(window.outerWidth);

  const apiKey = process.env.REACT_APP_GOOGLE_MAPS_API_KEY;

  async function createMap() {
    setCommandOutput('');
    setMap(null);
    try {
      const mapRef = document.getElementById('map')!;

      const newMap = await GoogleMap.create({
        element: mapRef,
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
      });

      setMap(newMap);
      setCommandOutput('Map created');
    } catch (err: any) {
      setCommandOutput(err.message);
    }
  }

  async function destroyMap() {
    setCommandOutput('');
    try {
      if (map) {
        await map.destroy();
        setMap(null);
        setCommandOutput('Map destroyed');
      }
    } catch (err: any) {
      setCommandOutput(err.message);
    }
  }

  async function enlargeMap() {
    setMapWidth(window.outerWidth);
    setMapHeight(window.outerWidth);
  }

  async function shrinkMap() {
    setMapWidth(mapWidth / 2);
    setMapHeight(mapHeight / 2);
  }

  return (
    <BaseTestingPage pageTitle="Resize Map">
      <div>
        <IonButton expand="block" id="createMapButton" onClick={createMap}>
          Create Map
        </IonButton>
        <IonButton expand="block" id="destroyMapButton" onClick={destroyMap}>
          Destroy Map
        </IonButton>
        <IonButton expand="block" id="enlargeMap" onClick={enlargeMap}>Enlarge Map</IonButton>
        <IonButton expand="block" id="shrinkMap" onClick={shrinkMap}>Shrink Map</IonButton>
      </div>
      <div>
        <IonTextarea
          id="commandOutput"
          value={commandOutput}
          autoGrow={true}
        ></IonTextarea>
      </div>
      <capacitor-google-map
        id="map"
        style={{
          position: 'absolute',
          top: window.innerHeight - window.outerWidth / 2,
          left: 0,
          width: mapWidth,
          height: mapHeight,
        }}
      ></capacitor-google-map>
    </BaseTestingPage>
  );
};

export default ResizeMapPage;
