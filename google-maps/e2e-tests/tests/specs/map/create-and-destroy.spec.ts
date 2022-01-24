import * as IonicE2E from '@ionic/e2e';
import { waitForLoad, pause, setDevice, switchToWeb, url } from '@ionic/e2e';

import CreateAndDestroyMapPage from '../../pageobjects/map/create-and-destroy.page';


describe('Google Maps - Create and Destroy Map', function () {
  before(async function () {
    await waitForLoad();
    await switchToWeb();
    await url('/basic-echo/run-basic-echo');
    await pause(500);
  });

  beforeEach(async function () {
    await setDevice(IonicE2E.Device.Mobile);
    await switchToWeb();
    await CreateAndDestroyMapPage.hideToolBars();
  });

  after(async function () {
    await switchToWeb();
    await CreateAndDestroyMapPage.showToolBars();
    await pause(500);
  });

  it('should create and destroy a map', async () => {
    const createMapButton = await CreateAndDestroyMapPage.createMapButton;
    const destroyMapButton = await CreateAndDestroyMapPage.destroyMapButton;

    const commandOutput = await $((await CreateAndDestroyMapPage.commandOutputTextarea).selector).$('textarea');

    await createMapButton.tap();
    await expect(commandOutput).toHaveValue('Map created');

    await destroyMapButton.tap();
    await expect(commandOutput).toHaveValue('Map destroyed');
  });

  it('should throw when attempting to destroy a non-existent map', async () => {
    const destroyMapButton = await CreateAndDestroyMapPage.destroyMapButton;
    const commandOutput = await $((await CreateAndDestroyMapPage.commandOutputTextarea).selector).$('textarea');

    await destroyMapButton.tap();
    await expect(commandOutput).toHaveValue('Map not found for provided id.');
  });


});
