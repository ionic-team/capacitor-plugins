import { IonButton, IonTextarea } from '@ionic/react';
import { useState } from 'react';
import { GoogleMap } from '@capacitor/google-maps';
import BaseTestingPage from '../../components/BaseTestingPage';

const PolylineMapPage: React.FC = () => {
  const [map, setMap] = useState<GoogleMap | null>();
  const [commandOutput, setCommandOutput] = useState('');
  const [ids, setIds] = useState<string[]>([]);

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

  async function createPolyline() {
    setCommandOutput('');
    try {
      if (map) {
        const sampleLines: google.maps.LatLngLiteral[] = [
          { lat: 37.772, lng: -122.214 },
          { lat: 21.291, lng: -157.821 },
          { lat: -18.142, lng: 178.431 },
          { lat: -27.467, lng: 153.027 },
        ];

        const createdIds = await map.addPolylines([{
          path: sampleLines,
          strokeColor: "#FF0000",
          strokeOpacity: 1.0,
          strokeWeight: 2,
        }]);

        setIds(createdIds);
        
        setCommandOutput('Polyline created');
      }
    } catch (err: any) {
      setCommandOutput(err.message);
    }
  }

  async function deletePolyline() {
    setCommandOutput('');
    try {
      if (map) {
       await map.removePolylines(ids);
       setIds([]);
      }
    } catch (err: any) {
      setCommandOutput(err.message);
    }
  }

  return (
    <BaseTestingPage pageTitle="Polylines">
      <div>
        <IonButton expand="block" id="createMapButton" onClick={createMap}>
          Create Map
        </IonButton>
        <IonButton expand="block" id="destroyMapButton" onClick={destroyMap}>
          Destroy Map
        </IonButton>

        <IonButton expand="block" id="createMapButton" onClick={createPolyline}>
          Draw Polyline
        </IonButton>

        <IonButton expand="block" id="createMapButton" onClick={deletePolyline}>
          Delete Polyline
        </IonButton>


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
          bottom: 0,
          left: 0,
          width: window.outerWidth,
          height: 500,
        }}
      ></capacitor-google-map>
    </BaseTestingPage>
  );
};

export default PolylineMapPage;
