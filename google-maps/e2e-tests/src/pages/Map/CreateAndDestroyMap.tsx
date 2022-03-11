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
            const mapRef1 = document.getElementById("map1")!
            const mapRef2 = document.getElementById("map2")!
            
            const newMap1 = await GoogleMap.create(mapRef1, "test-map", apiKey!, {
                center: {
                    lat: 33.6,
                    lng: -117.9,
                },
                zoom: 8,
                androidLiteMode: false,                
            });

            const newMap2 = await GoogleMap.create(mapRef2, "test-map2", apiKey!, {
                center: {
                    lat: -33.6,
                    lng: 117.9,
                },
                zoom: 6,
                androidLiteMode: false,                
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
                setMaps([]);
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
            <div id="map1" style={{
                position: "absolute",
                top: window.innerHeight - (window.outerWidth / 2),
                left: 0,
                width: (window.outerWidth / 2),
                height: (window.outerWidth / 2),
            }}></div>
            <div id="map2" style={{
                position: "absolute",
                top: window.innerHeight - (window.outerWidth / 2),
                left: window.outerWidth / 2,
                width: (window.outerWidth / 2),
                height: (window.outerWidth / 2),
            }}></div>
        </BaseTestingPage>
    )
}

export default CreateAndDestroyMapPage;