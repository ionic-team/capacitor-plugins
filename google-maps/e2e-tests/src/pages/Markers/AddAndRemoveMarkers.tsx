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
            const mapRef1 = document.getElementById("markers_map1")!
            const newMap = await GoogleMap.create(mapRef1, "test-map", apiKey!, {
                center: {
                    lat: 33.6,
                    lng: -117.9,
                },
                zoom: 8,
                androidLiteMode: false,                
            });
            setMap(newMap);

                setCommandOutput("Map created");
            
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
                <IonButton id="destroyMapButton" onClick={destroyMap}>
                    Destroy Map
                </IonButton>               
            </div>
            <div>
                <IonTextarea id="commandOutput" value={commandOutput}></IonTextarea>
            </div>
            <div id="markers_map1" style={{
                position: "absolute",
                top: window.innerHeight - 300,
                left: 0,
                width: window.innerWidth,
                height: 300,
            }}></div>
        </BaseTestingPage>
    )
}

export default AddAndRemoveMarkers;