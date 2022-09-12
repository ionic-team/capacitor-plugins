import { IonicButton, IonicTextarea } from '@ionic/e2e-components-ionic';

import Page from '../page';

class MarkerCustomizations extends Page {
  get createMapButton() {
    return new IonicButton('#createMapButton');
  }

  get addMarkerImageButton() {
    return new IonicButton('#addMarkerImageButton');
  }

  get addMarkerColorButton() {
    return new IonicButton('#addMarkerColorButton');
  }

  get addMultipleImageMarkersButton() {
    return new IonicButton('#addMultipleImageMarkersButton');
  }

  get addMultipleColorMarkersButton() {
    return new IonicButton('#addMultipleColorMarkersButton');
  }

  get removeAllMarkersButton() {
    return new IonicButton('#removeAllMarkersButton');
  }

  get destroyMapButton() {
    return new IonicButton('#destroyMapButton');
  }

  get commandOutputTextarea() {
    return new IonicTextarea('#commandOutput');
}
}

export default new MarkerCustomizations();
