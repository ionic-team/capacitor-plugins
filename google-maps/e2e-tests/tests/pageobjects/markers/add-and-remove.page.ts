import { IonicButton, IonicInput } from '@ionic/e2e-components-ionic';

import Page from "../page";

class AddAndRemoveMarkers extends Page {
    get createMapButton() {
        return new IonicButton("#createMapButton")
    }
    get addMarkerButton() {
        return new IonicButton("#addMarkerButton")
    }
    get removeMarkerButton() {
        return new IonicButton("#removeMarkerButton")
    }
    get commandOutputTextarea() {
        return new IonicInput('#commandOutput');
    }
}


export default new AddAndRemoveMarkers();