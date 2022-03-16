import { useState } from 'react';
import { GoogleMap, Marker } from '@capacitor/google-maps';
import { IonButton, IonTextarea } from '@ionic/react';
import BaseTestingPage from '../../components/BaseTestingPage';


const MultipleMarkers: React.FC = () => {
    const [map, setMap] = useState<GoogleMap | null>(null);
    const [markerIds, setMarkerIds] = useState<string[]>([]);
    const [commandOutput, setCommandOutput] = useState('');
    const [commandOutput2, setCommandOutput2] = useState('');
    const apiKey = process.env.REACT_APP_GOOGLE_MAPS_API_KEY;

    const onCameraIdle = (data: any) => {
        setCommandOutput(`CAMERA IDLE:  ${JSON.stringify(data)}`);
    }

    const onCameraMoveStarted = (data: any) => {
        setCommandOutput(`CAMERA MOVE STARTED:  ${JSON.stringify(data)}`);
    }

    const onClusterClick = (data: any) => {
        setCommandOutput2(`CLUSTER CLICKED:  ${JSON.stringify(data)}`);
    }

    const onMapClick = (data: any) => {
        setCommandOutput(`MAP CLICKED:  ${JSON.stringify(data)}`);
        setCommandOutput2("");
    }

    const onMarkerClick = (data: any) => {
        setCommandOutput2(`MARKER CLICKED:  ${JSON.stringify(data)}`);
    }

    const onMyLocationButtonClick = (data: any) => {
        setCommandOutput2(`MY LOCATION BUTTON CLICKED:  ${JSON.stringify(data)}`);
    }

    const onMyLocationClick = (data: any) => {
        setCommandOutput2(`MY LOCATION CLICKED:  ${JSON.stringify(data)}`);
    }

    async function createMap() {
        try {
            const element = document.getElementById('multipleMarkers_map1');
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

                setCommandOutput("Map created");
                setCommandOutput2("");
            }
        } catch (err: any) {
            setCommandOutput(err.message);
            setCommandOutput2("");
        }
    }

    async function setEventListeners() {
        map?.setOnCameraIdleListener(onCameraIdle);
        map?.setOnCameraMoveStartedListener(onCameraMoveStarted);
        map?.setOnClusterClickListener(onClusterClick);
        map?.setOnMapClickListener(onMapClick);
        map?.setOnMarkerClickListener(onMarkerClick);
        map?.setOnMyLocationButtonClickListener(onMyLocationButtonClick);
        map?.setOnMyLocationClickListener(onMyLocationClick);
        setCommandOutput('Set Event Listeners!');
        setCommandOutput2("");
    }

    async function removeEventListeners() {
        map?.removeAllMapListeners();
        setCommandOutput('Removed Event Listeners!');
        setCommandOutput2("");
    }

    async function enableClustering() {
        try {
            if (map) {
                await map.enableClustering();
                setCommandOutput("marker clustering enabled")
                setCommandOutput2("");
            }
        } catch (err: any) {
            setCommandOutput(err.message);
            setCommandOutput2("");
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
                setCommandOutput2("");
            }
        } catch (err: any) {
            setCommandOutput(err.message);
            setCommandOutput2("");
        }
    }

    async function removeAllMarkers() {
        try {
            if (map) {
                await map.removeMarkers(markerIds)
                setCommandOutput(`${markerIds.length} markers removed`)
                setCommandOutput2("");
                setMarkerIds([])
            }
        } catch (err: any) {
            setCommandOutput(err.message);
            setCommandOutput2("");
        }
    }

    async function disableClustering() {
        try {
            if (map) {
                await map.disableClustering();
                setCommandOutput("marker clustering disabled")
                setCommandOutput2("");
            }
        } catch (err: any) {
            setCommandOutput(err.message);
            setCommandOutput2("");
        }
    }

    async function destroyMap() {
        setCommandOutput("");
        try {
            if (map) {
                await map.destroy();
                setCommandOutput('Map destroyed');
                setCommandOutput2("");
            }
        } catch (err: any) {
            setCommandOutput(err.message);
            setCommandOutput2("");
        }
    }

    async function showCurrentLocation() {
        await map?.setCamera({               
            zoom: 1,
            animate: true,
            animationDuration: 50,
        })
        await map?.enableCurrentLocation(true);
    }

    return (
        <BaseTestingPage pageTitle="Multiple Markers">
            <div>
                <IonButton id="createMapButton" onClick={createMap}>
                    Create Map
                </IonButton>
                <IonButton  id="setOnMarkerClickButton" onClick={setEventListeners}>
                    Set Event Listeners
                </IonButton>
                <IonButton  id="removeOnMarkerClickButton" onClick={removeEventListeners}>
                    Remove Event Listeners
                </IonButton>
                <IonButton  id="showCurrentLocationButton" onClick={showCurrentLocation}>
                    Show Current Location
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
                <IonButton id="destroyMapButton" onClick={destroyMap}>
                    Destroy Map
                </IonButton> 
            </div>
            <div>
                <IonTextarea id="commandOutput" value={commandOutput}></IonTextarea>
                <IonTextarea id="commandOutput2" value={commandOutput2}></IonTextarea>
            </div>
            <div id="multipleMarkers_map1" style={{
                position: "absolute",
                top: window.innerHeight - 150,
                left: 0,
                width: window.innerWidth,
                height: 150,
            }}></div>
        </BaseTestingPage>
    )
}

export default MultipleMarkers;