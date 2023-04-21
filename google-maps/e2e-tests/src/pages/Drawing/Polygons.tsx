import { IonButton, IonTextarea } from '@ionic/react';
import { useState } from 'react';
import { GoogleMap } from '@capacitor/google-maps';
import BaseTestingPage from '../../components/BaseTestingPage';

const PolygonMapPage: React.FC = () => {
  const [map, setMap] = useState<GoogleMap | null>();
  const [commandOutput, setCommandOutput] = useState('');
  const [ids, setIds] = useState<string[]>([]);

  const apiKey = process.env.REACT_APP_GOOGLE_MAPS_API_KEY;

  const onPolygonClick = (data: any) => {
    setCommandOutput(
      `POLYGON (${data.polygonId}) WAS CLICKED ON MAP (${
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
            lat: 24.886,
            lng: -70.268,
          },
          zoom: 5,
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

  const createHollowPolygon = async () => {
    setCommandOutput('');

    try {
      if (map) {
        // hollow polygon outer shape
        const outerCoords = [
          { lat: 25.774, lng: -80.19 },
          { lat: 18.466, lng: -66.118 },
          { lat: 32.321, lng: -64.757 },
        ];

        // polygon hole shape
        const innerCoords = [
          { lat: 28.745, lng: -70.579 },
          { lat: 29.57, lng: -67.514 },
          { lat: 27.339, lng: -66.668 },
        ];

        const createdIds = await map.addPolygons([
          {
            paths: [outerCoords, innerCoords],
            strokeColor: '#FFC107',
            strokeOpacity: 0.8,
            strokeWeight: 2,
            fillColor: '#FFC107',
            fillOpacity: 0.35,
            tag: 'my_test_hollow_polygon',
            clickable: true,
          },
        ]);

        const newIds = createdIds.concat(ids);

        setIds(newIds);
      }
    } catch (err: any) {
      setCommandOutput(err.message);
    }
  };

  const createPolygon = async () => {
    setCommandOutput('');
    try {
      if (map) {
        const triangleCoords = [
          { lat: 25.774, lng: -80.19 },
          { lat: 18.466, lng: -66.118 },
          { lat: 32.321, lng: -64.757 },
          { lat: 25.774, lng: -80.19 },
        ];

        const createdIds = await map.addPolygons([
          {
            paths: triangleCoords,
            strokeColor: '#FF0000',
            strokeOpacity: 0.8,
            strokeWeight: 2,
            fillColor: '#FF0000',
            fillOpacity: 0.35,
            tag: 'my_test_polygon',
            clickable: true,
          },
        ]);

        const newIds = createdIds.concat(ids);
        setIds(newIds);

        setCommandOutput('Polygons created');
      }
    } catch (err: any) {
      setCommandOutput(err.message);
    }
  };

  const deletePolygon = async () => {
    setCommandOutput('');
    try {
      if (map) {
        await map.removePolygons(ids);
        setIds([]);

        setCommandOutput('Polygons removed');
      }
    } catch (err: any) {
      setCommandOutput(err.message);
    }
  };

  const setOnPolygonClickButton = async () => {
    map?.setOnPolygonClickListener(onPolygonClick);
    setCommandOutput('Set On Polygon Click Listeners!');
  };

  const removeOnPolygonClickButton = async () => {
    map?.setOnPolygonClickListener();
    setCommandOutput('Removed On Polygon Click Listeners!');
  };

  return (
    <BaseTestingPage pageTitle="Polygons">
      <div>
        <IonButton id="createMapButton" onClick={createMap}>
          Create Map
        </IonButton>
        <IonButton id="destroyMapButton" onClick={destroyMap}>
          Destroy Map
        </IonButton>
        <IonButton
          id="setOnPolygonClickButton"
          onClick={setOnPolygonClickButton}
        >
          Set Click Listeners
        </IonButton>
        <IonButton
          id="removeOnPolygonClickButton"
          onClick={removeOnPolygonClickButton}
        >
          Remove Click Listeners
        </IonButton>

        <IonButton id="drawPolygonButton" onClick={createPolygon}>
          Draw Polygon
        </IonButton>

        <IonButton id="drawHollowPolygonButton" onClick={createHollowPolygon}>
            Draw Hollow Polygon
        </IonButton>

        <IonButton id="deletePolygonButton" onClick={deletePolygon}>
          Delete Polygons
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

export default PolygonMapPage;
