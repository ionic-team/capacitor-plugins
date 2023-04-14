import { IonButton, IonTextarea } from '@ionic/react';
import { useState } from 'react';
import { Circle, GoogleMap } from '@capacitor/google-maps';
import BaseTestingPage from '../../components/BaseTestingPage';

const CircleMapPage: React.FC = () => {
  const [map, setMap] = useState<GoogleMap | null>();
  const [commandOutput, setCommandOutput] = useState('');
  const [ids, setIds] = useState<string[]>([]);

  const apiKey = process.env.REACT_APP_GOOGLE_MAPS_API_KEY;

  const onCircleClick = (data: any) => {
    setCommandOutput(
      `CIRCLE (${data.circleId}) WAS CLICKED ON MAP (${data.mapId}) WITH TAG (${
        data.tag ?? ''
      })`,
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
          center: { lat: 37.09, lng: -95.712 },
          zoom: 4,
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

  const createCircle = async () => {
    setCommandOutput('');
    try {
      if (map) {
        interface City {
          center: google.maps.LatLngLiteral;
          population: number;
        }

        const citymap: Record<string, City> = {
          chicago: {
            center: { lat: 41.878, lng: -87.629 },
            population: 2714856,
          },
          newyork: {
            center: { lat: 40.714, lng: -74.005 },
            population: 8405837,
          },
          losangeles: {
            center: { lat: 34.052, lng: -118.243 },
            population: 3857799,
          },
          vancouver: {
            center: { lat: 49.25, lng: -123.1 },
            population: 603502,
          },
        };

        const allCircles: Circle[] = [];

        for (const city in citymap) {          
          allCircles.push({
            strokeColor: '#FF0000',
            strokeOpacity: 0.8,
            strokeWeight: 2,
            fillColor: '#FF0000',
            fillOpacity: 0.35,
            center: citymap[city].center,
            radius: Math.sqrt(citymap[city].population) * 100,
            tag: city,
            title: city,
            clickable: true,
          });
        }

        const createdIds = await map.addCircles(allCircles);

        const newIds = createdIds.concat(ids);
        setIds(newIds);

        setCommandOutput('Circles created');
      }
    } catch (err: any) {
      setCommandOutput(err.message);
    }
  };

  const deleteCircle = async () => {
    setCommandOutput('');
    try {
      if (map) {
        await map.removeCircles(ids);
        setIds([]);

        setCommandOutput('Circles removed');
      }
    } catch (err: any) {
      setCommandOutput(err.message);
    }
  };

  const setOnCircleClickButton = async () => {
    map?.setOnCircleClickListener(onCircleClick);
    setCommandOutput('Set On Circle Click Listeners!');
  };

  const removeOnCircleClickButton = async () => {
    map?.setOnCircleClickListener();
    setCommandOutput('Removed On Circle Click Listeners!');
  };

  return (
    <BaseTestingPage pageTitle="Circles">
      <div>
        <IonButton id="createMapButton" onClick={createMap}>
          Create Map
        </IonButton>
        <IonButton id="destroyMapButton" onClick={destroyMap}>
          Destroy Map
        </IonButton>
        <IonButton
          id="setOnCircleClickButton"
          onClick={setOnCircleClickButton}
        >
          Set Click Listeners
        </IonButton>
        <IonButton
          id="removeOnCircleClickButton"
          onClick={removeOnCircleClickButton}
        >
          Remove Click Listeners
        </IonButton>

        <IonButton id="drawCircleButton" onClick={createCircle}>
          Draw Circles
        </IonButton>

        <IonButton id="deleteCirclesButton" onClick={deleteCircle}>
          Delete Circles
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

export default CircleMapPage;
