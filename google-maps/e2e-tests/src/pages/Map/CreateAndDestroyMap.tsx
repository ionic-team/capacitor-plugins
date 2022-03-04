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
            const element = document.getElementById('mapBox');
            const element2 = document.getElementById('mapBox2');
            if (element !== null && element2 !== null) {
                const newMap1 = await GoogleMap.create(element, "test-map", apiKey!, {
                    center: {
                        lat: 33.6,
                        lng: -117.9,
                    },
                    zoom: 8,
                    androidLiteMode: false
                });
                const newMap2 = await GoogleMap.create(element2, "test-map2", apiKey!, {
                    center: {
                        lat: 38.8977,
                        lng: -77.0365,
                    },
                    zoom: 10,
                    androidLiteMode: false
                });
        
                setMaps([newMap1, newMap2]);
                setCommandOutput('Maps created');
            }
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
            <div>
                <div id="mapBox" style={{width: 400, height: 400}}></div>
            </div>
            <div style={{marginTop: 20}}>
                <div id="mapBox2" style={{width: 400, height: 400, left: 40}}></div>
            </div>
        </BaseTestingPage>
    )
}

export default CreateAndDestroyMapPage;