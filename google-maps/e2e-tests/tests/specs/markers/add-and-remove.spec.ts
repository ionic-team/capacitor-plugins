import * as IonicE2E from '@ionic/e2e';
import { waitForLoad, pause, setDevice, switchToWeb, url } from '@ionic/e2e';

import AddAndRemoveMarkers from '../../pageobjects/markers/add-and-remove.page';


describe('Google Maps - Add and Remove Marker', function () {
  let createdMarkerId = "";

  before(async function () {
    await waitForLoad();
    await switchToWeb();
    await url('/markers/add-and-remove');
    await pause(500);
  });

  beforeEach(async function () {
    await setDevice(IonicE2E.Device.Mobile);
    await switchToWeb();
    await AddAndRemoveMarkers.hideToolBars();
  });

  after(async function () {
    await switchToWeb();
    await AddAndRemoveMarkers.showToolBars();
    await pause(500);
  });

  it("should create a map and add a marker", async function() {
    const createMapButton = await AddAndRemoveMarkers.createMapButton;
    const addMarkerButton = await AddAndRemoveMarkers.addMarkerButton;
    const getCommandOutputText = async function() {
      return (await AddAndRemoveMarkers.commandOutputTextarea).getValue();
    }

    await createMapButton.tap();
    await pause(500);
    await expect(await getCommandOutputText()).toBe('Map created');

    await addMarkerButton.tap();
    await pause(500);
    await expect(await getCommandOutputText()).toContain('Marker added: ');

    const markerId = (await getCommandOutputText()).replace("Marker added: ", "");  
    await expect(markerId).not.toBeFalsy();

    createdMarkerId = markerId;
  });

  it("should remove the created marker", async function() {
    const removeMarkerButton = await AddAndRemoveMarkers.removeMarkerButton;
    const commandOutput = await $((await AddAndRemoveMarkers.commandOutputTextarea).selector).$('textarea');

    await removeMarkerButton.tap();
    await pause(500);
    await expect(commandOutput).toHaveValueContaining(`Marker removed: ${createdMarkerId}`);
  });

  it("should throw when attempting to remove a non-existent marker", async function() {
    const removeMarkerButton = await AddAndRemoveMarkers.removeMarkerButton;
    const commandOutput = await $((await AddAndRemoveMarkers.commandOutputTextarea).selector).$('textarea');

    await removeMarkerButton.tap();
    await pause(500);
    await expect(commandOutput).toHaveValueContaining(`Marker not found for provided id.`);
  });
});