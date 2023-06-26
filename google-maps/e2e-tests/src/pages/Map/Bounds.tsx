import { useState } from 'react';
import { GoogleMap } from '@capacitor/google-maps';
import {
  IonButton,
  IonCol,
  IonInput,
  IonLabel,
  IonRow,
  IonTextarea,
} from '@ionic/react';
import BaseTestingPage from '../../components/BaseTestingPage';

const BoundsMapPage: React.FC = () => {
  const [map, setMap] = useState<GoogleMap | null>();
  const [commandOutput, setCommandOutput] = useState('');
  const [lat, setLat] = useState(0);
  const [lng, setLng] = useState(0);

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

  async function getBounds() {
    setCommandOutput('');
    try {
      const bounds = await map!.getMapBounds();
      setCommandOutput(JSON.stringify(bounds));
    } catch (err: any) {
      setCommandOutput(err.message);
    }
  }

  async function boundsContainsPoint() {
    setCommandOutput('');
    try {
      const bounds = await map!.getMapBounds();
      const contains = await bounds.contains({
        lat,
        lng,
      });
      setCommandOutput(contains.toString());
    } catch (err: any) {
      setCommandOutput(err.message);
    }
  }

  async function extendBounds() {
    setCommandOutput('');
    try {
      const bounds = await map!.getMapBounds();
      const newBounds = await bounds.extend({
        lat,
        lng,
      });
      setCommandOutput(JSON.stringify(newBounds));
    } catch (err: any) {
      setCommandOutput(err.message);
    }
  }

  return (
    <BaseTestingPage pageTitle="Bounds">
      <div>
        <IonButton expand="block" id="createMapButton" onClick={createMap}>
          Create Map
        </IonButton>
        <IonButton expand="block" id="destroyMapButton" onClick={destroyMap}>
          Destroy Map
        </IonButton>
        <IonButton expand="block" id="getBoundsButton" onClick={getBounds}>
          Get Bounds
        </IonButton>
        <IonRow>
          <IonCol size="3">
            <IonLabel id="latLabel">Lat</IonLabel>
            <IonInput
              id="latInput"
              type="number"
              value={lat}
              onIonChange={e => setLat(Number(e.detail.value!))}
            ></IonInput>
          </IonCol>
          <IonCol size="3">
            <IonLabel id="lngLabel">Lng</IonLabel>
            <IonInput
              id="lngInput"
              type="number"
              value={lng}
              onIonChange={e => setLng(Number(e.detail.value!))}
            ></IonInput>
          </IonCol>
          <IonCol>
            <IonButton
              expand="block"
              id="boundsContainsPointButton"
              onClick={boundsContainsPoint}
            >
              Bounds Contains Point
            </IonButton>
            <IonButton
              expand="block"
              id="extendBoundsButton"
              onClick={extendBounds}
            >
              Extend Bounds
            </IonButton>
          </IonCol>
        </IonRow>
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
          width: window.outerWidth,
          height: window.outerWidth,
        }}
      ></capacitor-google-map>
    </BaseTestingPage>
  );
};

export default BoundsMapPage;
