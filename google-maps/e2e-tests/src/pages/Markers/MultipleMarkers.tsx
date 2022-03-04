import { useState } from 'react';
import { GoogleMap, Marker } from '@capacitor/google-maps';
import { IonButton, IonTextarea } from '@ionic/react';
import BaseTestingPage from '../../components/BaseTestingPage';


const MultipleMarkers: React.FC = () => {
    const [map, setMap] = useState<GoogleMap | null>(null);
    const [markerIds, setMarkerIds] = useState<string[]>([]);
    const [commandOutput, setCommandOutput] = useState('');
    const apiKey = process.env.REACT_APP_GOOGLE_MAPS_API_KEY;

    async function createMap() {
        try {
            const element = document.getElementById('mapBox');
            if (element !== null) {
                const newMap = await GoogleMap.create(element, "test-map", apiKey!, {
                    center: {
                        lat: 47.60,
                        lng: -122.33,
                    },
                    zoom: 5,
                    androidLiteMode: false
                });
                setMap(newMap);

                setCommandOutput("Map created")
            }
        } catch (err: any) {
            setCommandOutput(err.message);
        }
    }

    async function enableClustering() {
        try {
            if (map) {
                await map.enableClustering();
                setCommandOutput("marker clustering enabled")
            }
        } catch (err: any) {
            setCommandOutput(err.message);
        }
    }

    async function addMultipleMarkers() {
        try {
            if (map) {
                const markers: Marker[] = [{
                    coordinate: {
                        lat: 47.60,
                        lng: -122.33,
                    }
                }, {
                    coordinate: {
                        lat: 47.60,
                        lng: -122.46,
                    }
                }, {
                    coordinate: {
                        lat: 47.30,
                        lng: -122.46,
                    }
                }, {
                    coordinate: {
                        lat: 47.20,
                        lng: -122.23,
                    }
                }];

                const ids = await map.addMarkers(markers);
                console.log("@@IDS: ", ids);
                setMarkerIds(ids)
                setCommandOutput(`${ids.length} markers added`)
            }
        } catch (err: any) {
            setCommandOutput(err.message);
        }
    }

    async function removeAllMarkers() {
        try {
            if (map) {
                await map.removeMarkers(markerIds)
                setCommandOutput(`${markerIds.length} markers removed`)
                setMarkerIds([])
            }
        } catch (err: any) {
            setCommandOutput(err.message);
        }
    }

    async function disableClustering() {
        try {
            if (map) {
                await map.disableClustering();
                setCommandOutput("marker clustering disabled")
            }
        } catch (err: any) {
            setCommandOutput(err.message);
        }
    }

    return (
        <BaseTestingPage pageTitle="Multiple Markers">
            <div>
                <IonButton id="createMapButton" onClick={createMap}>
                    Create Map
                </IonButton>
                <IonButton id="addMarkersButton" onClick={addMultipleMarkers}>
                    Add Multiple Markers
                </IonButton>
                <IonButton id="enableClusteringButton" onClick={enableClustering}>
                    Enable Clustering
                </IonButton>
                <IonButton id="disableClusteringButton" onClick={disableClustering}>
                    Disable Clustering
                </IonButton>
                <IonButton id="removeMarkersButton" onClick={removeAllMarkers}>
                    Remove Markers
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

export default MultipleMarkers;