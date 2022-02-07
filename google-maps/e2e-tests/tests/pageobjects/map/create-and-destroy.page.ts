import { IonicButton, IonicInput } from '@ionic/e2e-components-ionic';

import Page from '../page';

class CreateAndDestroyMapPage extends Page {
    get createMapButton() {
        return new IonicButton("#createMapButton");
    }
    get destroyMapButton() {
        return new IonicButton("#destroyMapButton");
    }
    get commandOutputTextarea() {
        return new IonicInput('#commandOutput');
      }
}

export default new CreateAndDestroyMapPage();