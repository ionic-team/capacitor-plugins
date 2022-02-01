import { useState } from 'react';
import { GoogleMap } from '@capacitor/google-maps';
import { IonButton, IonTextarea } from '@ionic/react';
import BaseTestingPage from '../../components/BaseTestingPage';

const CreateAndDestroyMapPage: React.FC = () => {
    const [map, setMap] = useState<GoogleMap | null>(null);
    const [commandOutput, setCommandOutput] = useState('');

    async function initializeMaps() {
        setCommandOutput("");
        try {
            await GoogleMap.initialize('AIzaSyDmw2qPFsciAv44tfbDDcDt9cU9PYnCmjw');
            setCommandOutput('Map initialized');
        } catch (err: any) {
            setCommandOutput(err.message);
        }
    }

    async function createMap() {
        setCommandOutput("");
        try {
            const newMap = await GoogleMap.create("test-map", {
                center: {
                    lat: 0,
                    lng: 0,
                },
                zoom: 8,
                androidLiteMode: false,
                height: 300,
                width: 300,
                x: 0,
                y: 0,
            });

            const testMap = await GoogleMap.create("test-map-2", {
                center: {
                    lat: 33.6,
                    lng: -117.9,
                },
                zoom: 1,
                androidLiteMode: false,
                height: 300,
                width: 300,
                x: 301,
                y: 301,
            });
    
            setMap(newMap);
            setCommandOutput('Map created');
        } catch(err: any) {
            setCommandOutput(err.message);
        }        
    }

    async function destroyMap() {
        setCommandOutput("");
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
        <BaseTestingPage pageTitle="Create and Destroy Map">
            <div>
                <IonButton expand="block" id="initializeButton" onClick={initializeMaps}>
                    Initialize
                </IonButton>
                <IonButton expand="block" id="createMapButton" onClick={createMap}>
                    Create Map
                </IonButton>
                <IonButton expand="block" id="destroyMapButton" onClick={destroyMap}>
                    Destroy Map
                </IonButton>
            </div>
            <div>
                <IonTextarea id="commandOutput" value={commandOutput}></IonTextarea>
            </div>
        </BaseTestingPage>
    )
}

export default CreateAndDestroyMapPage;