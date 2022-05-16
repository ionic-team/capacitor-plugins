import { IonicButton, IonicInput } from '@ionic/e2e-components-ionic';

import Page from '../page';

class BasicEchoPage extends Page {
  get runEchoButton() {
    return new IonicButton('#runEchoButton');
  }
  get commandOutputTextarea() {
    return new IonicInput('#commandOutput');
  }
}

export default new BasicEchoPage();
