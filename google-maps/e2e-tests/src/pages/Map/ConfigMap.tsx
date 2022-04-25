import { useState } from 'react';
import { GoogleMap } from '@capacitor/google-maps';
import { MapType } from '@capacitor/google-maps';
import { IonButton, IonTextarea } from '@ionic/react';
import BaseTestingPage from '../../components/BaseTestingPage';

const ConfigMapPage: React.FC = () => {
    const [maps, setMaps] = useState<GoogleMap[]>([]);
    const [commandOutput, setCommandOutput] = useState('');
    const apiKey = process.env.REACT_APP_GOOGLE_MAPS_API_KEY;


    async function createMaps() {
        setCommandOutput("");
        setMaps([]);
        try {            
            const mapRef1 = document.getElementById("config_map1")!
            const mapRef2 = document.getElementById("config_map2")!
            
            const newMap1 = await GoogleMap.create({
                element: mapRef1,
                id: "test-map",
                apiKey: apiKey!,
                config: {
                    center: {
                        lat: 33.6,
                        lng: -117.9,
                    },
                    zoom: 8
                }
            });

            const newMap2 = await GoogleMap.create({
                element: mapRef2, 
                id: "test-map2", 
                apiKey: apiKey!, 
                config: {
                    center: {
                        lat: -33.6,
                        lng: 117.9,
                    },
                    zoom: 6,                
                }
            });
    
            setMaps([newMap1, newMap2]);
            setCommandOutput('Maps created');
        } catch(err: any) {
            setCommandOutput(err.message);
        }        
    }

    async function moveCameras() {
        setCommandOutput("");
        try {
            const map1 = maps[0];
            await map1.setCamera({
                coordinate: {
                    lat: 36.716871,
                    lng: -119.767456
                },
                angle: 45,
                bearing: 200,
                zoom: 12,
                animate: true,
                animationDuration: 50,
            })

            const map2 = maps[1];
            await map2.setCamera({
                coordinate: {
                    lat: -31.931186,
                    lng: 115.856323
                },
                angle: 45,
                bearing: 200,
                zoom: 12,
                animate: true,
                animationDuration: 50,
            })
        } catch(err: any) {
            setCommandOutput(err.message);
        }
    }

    async function setMapType() {
        setCommandOutput("");
        try {
            const map1 = maps[0];
            await map1.setMapType(MapType.Terrain)

            const map2 = maps[1];
            await map2.setMapType(MapType.Satellite)
        } catch(err: any) {
            setCommandOutput(err.message);
        }
    }

    async function enableIndoorMaps() {
        setCommandOutput("");
        try {
            const map1 = maps[0];
            await map1.setCamera({
                coordinate: {
                    lat: 44.854682,
                    lng: -93.241997
                },
                angle: 0,
                bearing: 0,
                zoom: 17,
                animate: true,
                animationDuration: 50,
            })
            await map1.enableIndoorMaps(true);
        } catch(err: any) {
            setCommandOutput(err.message);
        }
    }

    async function enableTrafficLayer() {
        setCommandOutput("");
        try {
            const map1 = maps[0];           
            await map1.enableTrafficLayer(true);
            await map1.setCamera({               
                zoom: 10,
                animate: true,
                animationDuration: 50,
            })

            const map2 = maps[0];           
            await map2.enableTrafficLayer(true);
        } catch(err: any) {
            setCommandOutput(err.message);
        }
    }

    async function disableTrafficLayer() {
        setCommandOutput("");
        try {
            const map1 = maps[0];           
            await map1.enableTrafficLayer(false);

            const map2 = maps[0];           
            await map2.enableTrafficLayer(false);
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
                setCommandOutput('Maps destroyed');
            }
        } catch (err: any) {
            setCommandOutput(err.message);
        }
    }

    async function showCurrentLocation() {
        setCommandOutput("");
        try {
            const map1 = maps[0];                       
            await map1.setCamera({               
                zoom: 1,
                animate: true,
                animationDuration: 50,
            })
            await map1.enableCurrentLocation(true);

            const map2 = maps[1];                       
            await map2.setCamera({               
                zoom: 1,
                animate: true,
                animationDuration: 50,
            });
            await map2.enableCurrentLocation(true);
        } catch(err: any) {
            setCommandOutput(err.message);
        }
    }

    async function hideCurrentLocation() {
        setCommandOutput("");
        try {
            const map1 = maps[0];                                   
            await map1.enableCurrentLocation(false);

            const map2 = maps[1];                                 
            await map2.enableCurrentLocation(false);
        } catch(err: any) {
            setCommandOutput(err.message);
        }
    }

    async function showAccessibilityElements() {
        setCommandOutput("");
        try {
            const map1 = maps[0];                                   
            await map1.enableAccessibilityElements(true);

            const map2 = maps[1];                                 
            await map2.enableAccessibilityElements(true);
        } catch(err: any) {
            setCommandOutput(err.message);
        }
    }

    async function hideAccessibilityElements() {
        setCommandOutput("");
        try {
            const map1 = maps[0];                                   
            await map1.enableAccessibilityElements(false);

            const map2 = maps[1];                                 
            await map2.enableAccessibilityElements(false);
        } catch(err: any) {
            setCommandOutput(err.message);
        }
    }

    return (
        <BaseTestingPage pageTitle="Map Configuration">
            <div>
                <IonButton expand="block" id="createMapButton" onClick={createMaps}>
                    Create Maps
                </IonButton>
                <IonButton expand="block" id="destroyMapButton" onClick={destroyMaps}>
                    Destroy Maps
                </IonButton>
                <IonButton id="moveCameraButton" onClick={moveCameras}>Move Camera</IonButton>
                <IonButton id="setMapTypeButton" onClick={setMapType}>Set Map Type</IonButton>
                <IonButton id="enableIndoorMapButton" onClick={enableIndoorMaps}>Enable Indoor Maps</IonButton>
                <IonButton id="enableTrafficButton" onClick={enableTrafficLayer}>Enable Traffic Layer</IonButton>
                <IonButton id="disableTrafficButton" onClick={disableTrafficLayer}>Disable Traffic Layer</IonButton>
                <IonButton id="showLocationButton" onClick={showCurrentLocation}>Show Current Location</IonButton>
                <IonButton id="hideLocationButton" onClick={hideCurrentLocation}>Hide Current Location</IonButton>
                <IonButton id="showAccessibilityButton" onClick={showAccessibilityElements}>Show Acc. Elements</IonButton>
                <IonButton id="hideAccessibilityButton" onClick={hideAccessibilityElements}>Hide Acc. Elements</IonButton>
            </div>
            <div>
                <IonTextarea id="commandOutput" value={commandOutput}></IonTextarea>
            </div>
            <capacitor-google-map id="config_map1" style={{
                position: "absolute",
                top: window.innerHeight - (window.outerWidth / 2),
                left: 0,
                width: (window.outerWidth / 2),
                height: (window.outerWidth / 2),
            }}></capacitor-google-map>
            <capacitor-google-map id="config_map2" style={{
                position: "absolute",
                top: window.innerHeight - (window.outerWidth / 2),
                left: window.outerWidth / 2,
                width: (window.outerWidth / 2),
                height: (window.outerWidth / 2),
            }}></capacitor-google-map>
        </BaseTestingPage>
    )
}

export default ConfigMapPage;