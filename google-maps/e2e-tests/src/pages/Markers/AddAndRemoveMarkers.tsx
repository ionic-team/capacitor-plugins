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

            setCommandOutput("Map created")
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

    async function addMultipleMarkers() {
        try {
            if (map) {
                map.addMarker({
                    coordinate: {
                        lat: 47.60,
                        lng: -122.33,
                    }
                })
    
                map.addMarker({
                    coordinate: {
                        lat: 47.60,
                        lng: -122.46,
                    }
                })
    
                map.addMarker({
                    coordinate: {
                        lat: 47.30,
                        lng: -122.46,
                    }
                })
    
                map.addMarker({
                    coordinate: {
                        lat: 47.20,
                        lng: -122.23,
                    }
                })

                setCommandOutput(`4 markers added`)
            }
        } catch (err: any) {
            setCommandOutput(err.message);
        }
    }

    async function enableClustering() {
        try {
            if (map) {
                await map.enableClustering();
            }
        } catch (err: any) {
            setCommandOutput(err.message);
        }
    }

    async function disableClustering() {
        try {
            if (map) {
                await map.disableClustering();
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
                <IonButton  id="addMarkersButton" onClick={addMultipleMarkers}>
                    Add Multiple Markers
                </IonButton>  
                <IonButton  id="enableClusteringButton" onClick={enableClustering}>
                    Enable Clustering
                </IonButton>   
                <IonButton id="disableClusteringButton" onClick={disableClustering}>
                    Disable Clustering
                </IonButton>  
            </div>
            <div>
                <IonTextarea id="commandOutput" value={commandOutput}></IonTextarea>
            </div>
        </BaseTestingPage>
    )
}

export default AddAndRemoveMarkers;