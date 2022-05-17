import * as IonicE2E from '@ionic/e2e';
import { waitForLoad, pause, setDevice, switchToWeb, url } from '@ionic/e2e';

import CreateAndDestroyMapPage from '../../pageobjects/map/create-and-destroy.page';


describe('Google Maps - Create and Destroy Map', function () {
  before(async function () {
    await waitForLoad();
    await switchToWeb();
    await url('/maps/create-and-destroy');
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

  it('should create and destroy a map', async function() {
    const createMapButton = await CreateAndDestroyMapPage.createMapButton;
    const destroyMapButton = await CreateAndDestroyMapPage.destroyMapButton;
    const getCommandOutputText = async function() {
      return (await CreateAndDestroyMapPage.commandOutputTextarea).getValue();
    }

    await createMapButton.tap();
    await pause(500);
    await expect(await getCommandOutputText()).toBe('Maps created');

    await destroyMapButton.tap();
    await pause(500);
    await expect(await getCommandOutputText()).toBe('Maps destroyed');
  });

  it('should throw when attempting to destroy a non-existent map', async function() {
    const destroyMapButton = await CreateAndDestroyMapPage.destroyMapButton;
    const getCommandOutputText = async function() {
      return (await CreateAndDestroyMapPage.commandOutputTextarea).getValue();
    }

    await destroyMapButton.tap();
    await pause(100);
    await expect(await getCommandOutputText()).toBe('Map not found for provided id.');
  });


});
