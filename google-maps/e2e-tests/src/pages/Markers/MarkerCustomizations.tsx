import { useState } from 'react';
import { GoogleMap, Marker } from '@capacitor/google-maps';
import { IonButton, IonTextarea } from '@ionic/react';

import BaseTestingPage from '../../components/BaseTestingPage';

const MarkerCustomizations: React.FC = () => {
  const [map, setMap] = useState<GoogleMap | null>(null);
  const [markerIds, setMarkerIds] = useState<string[]>([]);
  const [commandOutput, setCommandOutput] = useState('');

  const apiKey = process.env.REACT_APP_GOOGLE_MAPS_API_KEY;

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
            zoom: 12,
          },
        });

        setMap(newMap);

        setCommandOutput('Map created');
      }
    } catch (err: any) {
      setCommandOutput(err.message);
    }
  }

  const addMarker = async () => {
    try {
      if (!map) {
        throw new Error('map not created');
      }

      await map.addMarker({
        coordinate: {
          lat: 43.512098,
          lng: -96.739352,
        },
        iconUrl: "assets/icon/pin.png",
        iconSize: {
          width: 30,
          height: 30,
        },
        iconOrigin: {x: 0, y:0},
        iconAnchor: {x: 15, y:30},
      });
    } catch (err: any) {
      setCommandOutput(err.message);
    }
  };

  return (
    <BaseTestingPage pageTitle="Marker Customization">
      <div>
        <IonButton id="createMapButton" onClick={createMap}>
          Create Map
        </IonButton>
        <IonButton id="addMarkerButton" onClick={addMarker}>
          Add Styled Marker
        </IonButton>

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
