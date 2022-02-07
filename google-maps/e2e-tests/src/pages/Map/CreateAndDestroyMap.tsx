import { useState } from 'react';
import { GoogleMap } from '@capacitor/google-maps';
import { IonButton, IonTextarea } from '@ionic/react';
import BaseTestingPage from '../../components/BaseTestingPage';

const CreateAndDestroyMapPage: React.FC = () => {
    const [maps, setMaps] = useState<GoogleMap[]>([]);
    const [commandOutput, setCommandOutput] = useState('');
    const apiKey = process.env.REACT_APP_GOOGLE_MAPS_API_KEY;


    async function createMaps() {
        setCommandOutput("");
        setMaps([]);
        try {
            const newMap1 = await GoogleMap.create("test-map", apiKey!, {
                center: {
                    lat: 33.6,
                    lng: -117.9,
                },
                zoom: 8,
                androidLiteMode: false,
                height: 300,
                width: 300,
                x: 0,
                y: 200,
            });

            const newMap2 = await GoogleMap.create("test-map2", apiKey!, {
                center: {
                    lat: -33.6,
                    lng: 117.9,
                },
                zoom: 6,
                androidLiteMode: false,
                height: 200,
                width: 200,
                x: 100,
                y: 550,
            });
    
            setMaps([newMap1, newMap2]);
            setCommandOutput('Maps created');
        } catch(err: any) {
            setCommandOutput(err.message);
        }        
    }

    async function destroyMaps() {
        setCommandOutput("");
        try {
            if (maps) {
                for (let map of maps) {
                    await map.destroy();
                }
                //setMaps([]);
                setCommandOutput('Maps destroyed');
            }
        } catch (err: any) {
            setCommandOutput(err.message);
        }
    }

    return (
        <BaseTestingPage pageTitle="Create and Destroy Map">
            <div>
                <IonButton expand="block" id="createMapButton" onClick={createMaps}>
                    Create Maps
                </IonButton>
                <IonButton expand="block" id="destroyMapButton" onClick={destroyMaps}>
                    Destroy Maps
                </IonButton>
            </div>
            <div>
                <IonTextarea id="commandOutput" value={commandOutput}></IonTextarea>
            </div>
        </BaseTestingPage>
    )
}

export default CreateAndDestroyMapPage;