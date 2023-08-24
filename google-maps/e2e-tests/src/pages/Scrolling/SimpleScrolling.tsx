import { useState } from 'react';
import { GoogleMap } from '@capacitor/google-maps';
import { IonButton, IonContent, IonTextarea } from '@ionic/react';
import BaseTestingPage from '../../components/BaseTestingPage';

const SimpleScrollingPage: React.FC = () => {
  const [map, setMap] = useState<GoogleMap | null>(null);
  const [commandOutput, setCommandOutput] = useState('');
  const apiKey = process.env.REACT_APP_GOOGLE_MAPS_API_KEY;

  async function createMaps() {
    const mapRef = document.getElementById('map')!;
    setCommandOutput('');
    setMap(null);
    try {
      const newMap1 = await GoogleMap.create({
        element: mapRef, 
        id: 'test-map', 
        apiKey: apiKey!, 
        config: {
          center: {
            lat: 33.6,
            lng: -117.9,
          },
          zoom: 8,
        }
      });

      setMap(newMap1);
      setCommandOutput('Maps created');
      console.log('Maps created');
    } catch (err: any) {
      console.log(err.message);
    }
  }

  async function destroyMaps() {
    setCommandOutput('');
    try {
      if (map) {
        await map.destroy();
        console.log('map destroyed');
      }
    } catch (err: any) {
      console.log(err.message);
    }
  }

  return (
    <BaseTestingPage pageTitle="Simple Scrolling">
      <div>
        <IonButton expand="block" id="createMapButton" onClick={createMaps}>
          Create Maps
        </IonButton>
        <IonButton expand="block" id="destroyMapButton" onClick={destroyMaps}>
          Destroy Maps
        </IonButton>
      </div>
      <IonContent style={{ background: 'transparent' }}>
        <div>
          <p>
            Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla
            vulputate eget ipsum vel venenatis. Ut dui enim, elementum vel
            convallis a, malesuada a nibh. Etiam sit amet purus orci. Sed sit amet
            sapien non libero mollis tempus eget non lacus. Proin vel nisi sit
            amet neque luctus hendrerit ac at nisi. Maecenas malesuada neque dui,
            a ornare neque efficitur id. Duis a quam ut lacus euismod ultrices sed
            non libero.
          </p>
          <p>
            Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla
            vulputate eget ipsum vel venenatis. Ut dui enim, elementum vel
            convallis a, malesuada a nibh. Etiam sit amet purus orci. Sed sit amet
            sapien non libero mollis tempus eget non lacus. Proin vel nisi sit
            amet neque luctus hendrerit ac at nisi. Maecenas malesuada neque dui,
            a ornare neque efficitur id. Duis a quam ut lacus euismod ultrices sed
            non libero.
          </p>
          <p>
            Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla
            vulputate eget ipsum vel venenatis. Ut dui enim, elementum vel
            convallis a, malesuada a nibh. Etiam sit amet purus orci. Sed sit amet
            sapien non libero mollis tempus eget non lacus. Proin vel nisi sit
            amet neque luctus hendrerit ac at nisi. Maecenas malesuada neque dui,
            a ornare neque efficitur id. Duis a quam ut lacus euismod ultrices sed
            non libero.
          </p>
          <p>
            Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla
            vulputate eget ipsum vel venenatis. Ut dui enim, elementum vel
            convallis a, malesuada a nibh. Etiam sit amet purus orci. Sed sit amet
            sapien non libero mollis tempus eget non lacus. Proin vel nisi sit
            amet neque luctus hendrerit ac at nisi. Maecenas malesuada neque dui,
            a ornare neque efficitur id. Duis a quam ut lacus euismod ultrices sed
            non libero.
          </p>
          <p>
            Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla
            vulputate eget ipsum vel venenatis. Ut dui enim, elementum vel
            convallis a, malesuada a nibh. Etiam sit amet purus orci. Sed sit amet
            sapien non libero mollis tempus eget non lacus. Proin vel nisi sit
            amet neque luctus hendrerit ac at nisi. Maecenas malesuada neque dui,
            a ornare neque efficitur id. Duis a quam ut lacus euismod ultrices sed
            non libero.
          </p>
          
          
          <capacitor-google-map
            id="map"
            style={{
              display: 'inline-block',
              width: 275,
              height: 400,
              float: 'left',
              marginRight: 20,
              marginBottom: 20
            }}></capacitor-google-map>
          <div
            style={{
              display: 'block',
              width: 75,
              height: 75,
              backgroundColor: 'blue',
              position: 'relative',
              top: -10,
              left: 40,
              zIndex: 1000,
              opacity: 0.5,
            }}
          ></div>
          <p>
            Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla
            vulputate eget ipsum vel venenatis. Ut dui enim, elementum vel
            convallis a, malesuada a nibh. Etiam sit amet purus orci. Sed sit amet
            sapien non libero mollis tempus eget non lacus. Proin vel nisi sit
            amet neque luctus hendrerit ac at nisi. Maecenas malesuada neque dui,
            a ornare neque efficitur id. Duis a quam ut lacus euismod ultrices sed
            non libero.
          </p>
          <p>
            Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla
            vulputate eget ipsum vel venenatis. Ut dui enim, elementum vel
            convallis a, malesuada a nibh. Etiam sit amet purus orci. Sed sit amet
            sapien non libero mollis tempus eget non lacus. Proin vel nisi sit
            amet neque luctus hendrerit ac at nisi. Maecenas malesuada neque dui,
            a ornare neque efficitur id. Duis a quam ut lacus euismod ultrices sed
            non libero.
          </p>
          <p>
            Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla
            vulputate eget ipsum vel venenatis. Ut dui enim, elementum vel
            convallis a, malesuada a nibh. Etiam sit amet purus orci. Sed sit amet
            sapien non libero mollis tempus eget non lacus. Proin vel nisi sit
            amet neque luctus hendrerit ac at nisi. Maecenas malesuada neque dui,
            a ornare neque efficitur id. Duis a quam ut lacus euismod ultrices sed
            non libero.
          </p>
          <p>
            Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla
            vulputate eget ipsum vel venenatis. Ut dui enim, elementum vel
            convallis a, malesuada a nibh. Etiam sit amet purus orci. Sed sit amet
            sapien non libero mollis tempus eget non lacus. Proin vel nisi sit
            amet neque luctus hendrerit ac at nisi. Maecenas malesuada neque dui,
            a ornare neque efficitur id. Duis a quam ut lacus euismod ultrices sed
            non libero.
          </p>
          <p>
            Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla
            vulputate eget ipsum vel venenatis. Ut dui enim, elementum vel
            convallis a, malesuada a nibh. Etiam sit amet purus orci. Sed sit amet
            sapien non libero mollis tempus eget non lacus. Proin vel nisi sit
            amet neque luctus hendrerit ac at nisi. Maecenas malesuada neque dui,
            a ornare neque efficitur id. Duis a quam ut lacus euismod ultrices sed
            non libero.
          </p>
          <p>
            Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla
            vulputate eget ipsum vel venenatis. Ut dui enim, elementum vel
            convallis a, malesuada a nibh. Etiam sit amet purus orci. Sed sit amet
            sapien non libero mollis tempus eget non lacus. Proin vel nisi sit
            amet neque luctus hendrerit ac at nisi. Maecenas malesuada neque dui,
            a ornare neque efficitur id. Duis a quam ut lacus euismod ultrices sed
            non libero.
          </p>
        </div>
      </IonContent>
    </BaseTestingPage>
  );
};

export default SimpleScrollingPage;
