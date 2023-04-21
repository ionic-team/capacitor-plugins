import { IonButton, IonTextarea } from '@ionic/react';
import { useState } from 'react';
import { GoogleMap } from '@capacitor/google-maps';
import BaseTestingPage from '../../components/BaseTestingPage';

const PolylineMapPage: React.FC = () => {
  const [map, setMap] = useState<GoogleMap | null>();
  const [commandOutput, setCommandOutput] = useState('');
  const [ids, setIds] = useState<string[]>([]);

  const apiKey = process.env.REACT_APP_GOOGLE_MAPS_API_KEY;

  const onPolylineClick = (data: any) => {
    setCommandOutput(
      `POLYLINE (${data.polylineId}) WAS CLICKED ON MAP (${
        data.mapId
      }) WITH TAG (${data.tag ?? ''})`,
    );
  };

  const createMap = async () => {
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
  };

  const destroyMap = async () => {
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
  };

  const createPolyline = async () => {
    setCommandOutput('');
    try {
      if (map) {
        const sampleLines: google.maps.LatLngLiteral[] = [
          { lat: 37.772, lng: -122.214 },
          { lat: 21.291, lng: -157.821 },
          { lat: -18.142, lng: 178.431 },
          { lat: -27.467, lng: 153.027 },
        ];

        const createdIds = await map.addPolylines([
          {
            path: sampleLines,
            strokeColor: '#ffdd00',
            strokeOpacity: 1.0,
            strokeWeight: 2,
            geodesic: true,
            tag: 'my_polyline',
            clickable: true,
            styleSpans: [
              { color: '#85892D' },
              { color: '#0000FF' },
              { color: '#FFF700' },
              { color: '#FF99CC' },
            ],
          },
        ]);

        setIds(createdIds);

        setCommandOutput('Polyline created');
      }
    } catch (err: any) {
      setCommandOutput(err.message);
    }
  };

  const deletePolyline = async () => {
    setCommandOutput('');
    try {
      if (map) {
        await map.removePolylines(ids);
        setIds([]);
      }
    } catch (err: any) {
      setCommandOutput(err.message);
    }
  };

  const setOnPolylineClickButton = async () => {
    map?.setOnPolylineClickListener(onPolylineClick);
    setCommandOutput('Set On Polyline Click Listeners!');
  };

  const removeOnPolylineClickButton = async () => {
    map?.setOnPolylineClickListener();
    setCommandOutput('Removed On Polyline Click Listeners!');
  };

  return (
    <BaseTestingPage pageTitle="Polylines">
      <div>
        <IonButton id="createMapButton" onClick={createMap}>
          Create Map
        </IonButton>
        <IonButton id="destroyMapButton" onClick={destroyMap}>
          Destroy Map
        </IonButton>
        <IonButton
          id="setOnPolylineClickButton"
          onClick={setOnPolylineClickButton}
        >
          Set Click Listeners
        </IonButton>
        <IonButton
          id="removeOnPolylineClickButton"
          onClick={removeOnPolylineClickButton}
        >
          Remove Click Listeners
        </IonButton>

        <IonButton id="drawPolylineButton" onClick={createPolyline}>
          Draw Polyline
        </IonButton>

        <IonButton id="deletePolylineButton" onClick={deletePolyline}>
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
