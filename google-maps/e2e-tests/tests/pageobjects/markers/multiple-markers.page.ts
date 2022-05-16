import { IonicButton, IonicTextarea } from '@ionic/e2e-components-ionic';

import Page from "../page";

class MultipleMarkers extends Page {
    get createMapButton() {
        return new IonicButton("#createMapButton")
    }
    get addMarkersButton() {
        return new IonicButton("#addMarkersButton")
    }
    get enableClusteringButton() {
        return new IonicButton("#enableClusteringButton")
    }
    get disableClusteringButton() {
        return new IonicButton("#disableClusteringButton")
    }
    get removeMarkersButton() {
        return new IonicButton("#removeMarkersButton")
    }
    get commandOutputTextarea() {
        return new IonicTextarea('#commandOutput');
    }
}

export default new MultipleMarkers();