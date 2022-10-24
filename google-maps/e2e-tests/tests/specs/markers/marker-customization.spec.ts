import * as IonicE2E from '@ionic/e2e';
import { waitForLoad, pause, setDevice, switchToWeb, url } from '@ionic/e2e';

import MarkerCustomizations from '../../pageobjects/markers/marker-customization.page';

describe('Google Maps - Marker Customization', function () {
  before(async function () {
    await waitForLoad();
    await switchToWeb();
    await url('/markers/customizations');
    await pause(500);
  });

  beforeEach(async function () {
    await setDevice(IonicE2E.Device.Mobile);
    await switchToWeb();
    await MarkerCustomizations.hideToolBars();
  });

  after(async function () {
    await switchToWeb();
    await MarkerCustomizations.showToolBars();
    await pause(500);
  });

  it('should create a map and add 1 color marker', async function () {
    const createMapButton = await MarkerCustomizations.createMapButton;
    const removeAllMarkersButton = await MarkerCustomizations.removeAllMarkersButton;
    const addColorMarkerButton = await MarkerCustomizations.addMarkerColorButton;
    const commandOutput = await $(
      (
        await MarkerCustomizations.commandOutputTextarea
      ).selector,
    ).$('textarea');

    await createMapButton.tap();
    await pause(500);
    await expect(commandOutput).toHaveValue('Map created');

    await addColorMarkerButton.tap();
    await pause(500);
    await expect(commandOutput).toHaveValueContaining('1 color marker added');

    await removeAllMarkersButton.tap();
    await pause(500);
    await expect(commandOutput).toHaveValueContaining('1 markers removed');
  });

  it('should create a map and add 1 image marker', async function () {
    const createMapButton = await MarkerCustomizations.createMapButton;
    const removeAllMarkersButton = await MarkerCustomizations.removeAllMarkersButton;
    const addImageMarkerButton = await MarkerCustomizations.addMarkerImageButton;
    const commandOutput = await $(
      (
        await MarkerCustomizations.commandOutputTextarea
      ).selector,
    ).$('textarea');

    await createMapButton.tap();
    await pause(500);
    await expect(commandOutput).toHaveValue('Map created');

    await addImageMarkerButton.tap();
    await pause(500);
    await expect(commandOutput).toHaveValueContaining('1 image marker added');

    await removeAllMarkersButton.tap();
    await pause(500);
    await expect(commandOutput).toHaveValueContaining('1 markers removed');
  });

  it('should add 1 image marker', async function () {
    const removeAllMarkersButton = await MarkerCustomizations.removeAllMarkersButton;
    const addImageMarkerButton = await MarkerCustomizations.addMarkerImageButton;
    const commandOutput = await $(
      (
        await MarkerCustomizations.commandOutputTextarea
      ).selector,
    ).$('textarea');

    await addImageMarkerButton.tap();
    await pause(500);
    await expect(commandOutput).toHaveValueContaining('1 image marker added');

    await removeAllMarkersButton.tap();
    await pause(500);
    await expect(commandOutput).toHaveValueContaining('1 markers removed');
  });

  it('should add 4 image markers', async function () {
    const removeAllMarkersButton = await MarkerCustomizations.removeAllMarkersButton;
    const addMultipleImageMarkersButton = await MarkerCustomizations.addMultipleImageMarkersButton;
    const commandOutput = await $(
      (
        await MarkerCustomizations.commandOutputTextarea
      ).selector,
    ).$('textarea');

    await addMultipleImageMarkersButton.tap();
    await pause(500);
    await expect(commandOutput).toHaveValueContaining('4 image markers added');

    await removeAllMarkersButton.tap();
    await pause(500);
    await expect(commandOutput).toHaveValueContaining('4 markers removed');
  });

  it('should add 4 color markers', async function () {
    const removeAllMarkersButton = await MarkerCustomizations.removeAllMarkersButton;
    const addMultipleColorMarkersButton = await MarkerCustomizations.addMultipleColorMarkersButton;
    const commandOutput = await $(
      (
        await MarkerCustomizations.commandOutputTextarea
      ).selector,
    ).$('textarea');

    await addMultipleColorMarkersButton.tap();
    await pause(500);
    await expect(commandOutput).toHaveValueContaining('4 color markers added');

    await removeAllMarkersButton.tap();
    await pause(500);
    await expect(commandOutput).toHaveValueContaining('4 markers removed');
  });
});
