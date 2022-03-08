import { useState } from 'react';
import { GoogleMap } from '@capacitor/google-maps';
import { IonButton, IonTextarea } from '@ionic/react';
import BaseTestingPage from '../../components/BaseTestingPage';

const CreateAndDestroyMapPage: React.FC = () => {
    const [maps, setMaps] = useState<GoogleMap[]>([]);
    const [commandOutput, setCommandOutput] = useState('');
    const apiKey = process.env.REACT_APP_GOOGLE_MAPS_API_KEY;

    const onMapReady = (data: any) => {
        setCommandOutput(`MAP (${data.id}) IS READY`)
    }

    const onMapClick = (data: any) => {
        setCommandOutput(`MAP (${data.id}) CLICKED @ (${data.latitude}, ${data.longitude})`);
    }

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
            }, true, onMapReady);

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
            }, true, onMapReady);
    
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

    async function setOnMapClickListeners() {
        setCommandOutput("");
        try {
            if (maps) {
                for (let map of maps) {
                    map.setOnMapClickListener(onMapClick);
                }
                setCommandOutput('Map Click Listeners Set');
            }
        } catch (err: any) {
            setCommandOutput(err.message);
        }
    }

    async function removeOnMapClickListeners() {
        setCommandOutput("");
        try {
            if (maps) {
                for (let map of maps) {
                    map.setOnMapClickListener();
                }

                setCommandOutput('Map Click Listeners Destroyed');
            }
        } catch (err: any) {
            setCommandOutput(err.message);
        }
    }

    return (
        <BaseTestingPage pageTitle="Create and Destroy Map">
            <div>
                <IonTextarea id="commandOutput" value={commandOutput}></IonTextarea>
            </div>
            <div>
                <IonButton expand="block" id="createMapButton" onClick={createMaps}>
                    Create Maps
                </IonButton>
                <IonButton expand="block" id="setOnMapClickButton" onClick={setOnMapClickListeners}>
                    Set On Map Click Listeners
                </IonButton>
                <IonButton expand="block" id="removeOnMapClickButton" onClick={removeOnMapClickListeners}>
                    Remove On Map Click Listeners
                </IonButton>
                <IonButton expand="block" id="destroyMapButton" onClick={destroyMaps}>
                    Destroy Maps
                </IonButton>
            </div>
        </BaseTestingPage>
    )
}

export default CreateAndDestroyMapPage;