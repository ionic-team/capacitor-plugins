import { useState } from 'react';
import { GoogleMap } from '@capacitor/google-maps';
import { IonButton, IonTextarea } from '@ionic/react';
import BaseTestingPage from '../../components/BaseTestingPage';

const AddAndRemoveMarkers: React.FC = () => {
    const [map, setMap] = useState<GoogleMap | null>(null);
    const [markerId, setMarkerId] = useState<string>("");
    const [commandOutput, setCommandOutput] = useState('');
    const apiKey = process.env.REACT_APP_GOOGLE_MAPS_API_KEY;

    async function createMap() {
        try {
            const element = document.getElementById('mapBox');
            if (element !== null) {
                const newMap = await GoogleMap.create(element, "test-map", apiKey!, {
                    center: {
                        lat: 33.6,
                        lng: -117.9,
                    },
                    zoom: 8,
                    androidLiteMode: false,
                });
                setMap(newMap);

                setCommandOutput("Map created");
            }
        } catch (err: any) {
            setCommandOutput(err.message);
        }
    }

    async function addMarker() {
        try {
            if (!map) {
                throw new Error("map not created");
            }

            const id = await map.addMarker({
                coordinate: {
                    lat: 33.6,
                    lng: -117.9,
                },
                title: "Hello world",
            });

            setMarkerId(id);    
            setCommandOutput(`Marker added: ${id}`)
            
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
            setCommandOutput(`Marker removed: ${markerId}`)
        } catch (err: any) {
            setCommandOutput(err.message);
        }
    }

    return (
        <BaseTestingPage pageTitle="Add and Remove Markers">
            <div>
                <IonButton  id="createMapButton" onClick={createMap}>
                    Create Map
                </IonButton>
                <IonButton  id="addMarkerButton" onClick={addMarker}>
                    Add 1 Marker
                </IonButton>    
                <IonButton id="removeMarkerButton" onClick={removeMarker}>
                    Remove Marker
                </IonButton>                
            </div>
            <div>
                <IonTextarea id="commandOutput" value={commandOutput}></IonTextarea>
            </div>
            <div>
                <div id="mapBox" style={{width: 400, height: 400}}></div>
            </div>
        </BaseTestingPage>
    )
}

export default AddAndRemoveMarkers;