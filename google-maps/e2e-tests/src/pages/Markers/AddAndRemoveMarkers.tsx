import { useState } from 'react';
import { GoogleMap } from '@capacitor/google-maps';
import { IonButton, IonTextarea } from '@ionic/react';
import BaseTestingPage from '../../components/BaseTestingPage';

const AddAndRemoveMarkers: React.FC = () => {
    const [map, setMap] = useState<GoogleMap | null>(null);
    const [markerId, setMarkerId] = useState<string>("");
    const [commandOutput, setCommandOutput] = useState('');
    const apiKey = process.env.REACT_APP_GOOGLE_MAPS_API_KEY;

    async function addMarker() {
        try {
            const newMap = await GoogleMap.create("test-map", apiKey!, {
                center: {
                    lat: 33.6,
                    lng: -117.9,
                },
                zoom: 8,
                androidLiteMode: false,
                height: 300,
                width: window.innerWidth,
                x: 0,
                y: window.innerHeight - 300
            });
            setMap(newMap);

            const id = await newMap.addMarker({
                coordinate: {
                    lat: 33.6,
                    lng: -117.9,
                }
            });

            setMarkerId(id);    
            setCommandOutput(`Marker ${id} added`)
        } catch (err: any) {
            setCommandOutput(err.message);
        }
    }

    async function removeMarker() {
        try {
            if (markerId === "") {
                throw new Error("marker id not set");
            }

            if (!map) {
                throw new Error("map not created");
            }

            await map.removeMarker(markerId);
            setCommandOutput(`Marker ${markerId} removed`)
        } catch (err: any) {
            setCommandOutput(err.message);
        }
    }

    return (
        <BaseTestingPage pageTitle="Add and Remove Markers">
            <div>
                <IonButton expand="block" id="addMarkerButton" onClick={addMarker}>
                    Create Map and Add 1 Marker
                </IonButton>    
                <IonButton expand="block" id="removeMarkerButton" onClick={removeMarker}>
                    Remove Marker
                </IonButton>               
            </div>
            <div>
                <IonTextarea id="commandOutput" value={commandOutput}></IonTextarea>
            </div>
        </BaseTestingPage>
    )
}

export default AddAndRemoveMarkers;