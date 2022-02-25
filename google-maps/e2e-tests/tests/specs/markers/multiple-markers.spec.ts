import * as IonicE2E from '@ionic/e2e';
import { waitForLoad, pause, setDevice, switchToWeb, url } from '@ionic/e2e';

import MultipleMarkers from '../../pageobjects/markers/multiple-markers.page';

describe('Google Maps - Multiple Markers', function () {
    before(async function () {
        await waitForLoad();
        await switchToWeb();
        await url('/markers/multiple-markers');
        await pause(500);
      });
    
      beforeEach(async function () {
        await setDevice(IonicE2E.Device.Mobile);
        await switchToWeb();
        await MultipleMarkers.hideToolBars();
      });
    
      after(async function () {
        await switchToWeb();
        await MultipleMarkers.showToolBars();
        await pause(500);
      });

      it("should create a map and add 4 markers", async function() {
        const createMapButton = await MultipleMarkers.createMapButton;
        const addMarkersButton = await MultipleMarkers.addMarkersButton;
        const commandOutput = await $((await MultipleMarkers.commandOutputTextarea).selector).$('textarea');

        await createMapButton.tap();
        await pause(500);
        await expect(commandOutput).toHaveValue('Map created');

        await addMarkersButton.tap();
        await pause(500);
        await expect(commandOutput).toHaveValueContaining('4 markers added');
      })

      it("should enable and disable clustering", async function() {
          const enableClusteringButton = await MultipleMarkers.enableClusteringButton;
          const disableClusteringButton = await MultipleMarkers.disableClusteringButton;
          const commandOutput = await $((await MultipleMarkers.commandOutputTextarea).selector).$('textarea');

          await enableClusteringButton.tap();
          await pause(500);
          await expect(commandOutput).toHaveValue('marker clustering enabled');

          await disableClusteringButton.tap();
          await pause(500);
          await expect(commandOutput).toHaveValue('marker clustering disabled');
    })

    it("should remove 4 markers", async function() {
        const removeMarkersButton = await MultipleMarkers.removeMarkersButton;
        const commandOutput = await $((await MultipleMarkers.commandOutputTextarea).selector).$('textarea');

        await removeMarkersButton.tap()
        await pause(500);
        await expect(commandOutput).toHaveValue('4 markers removed');
    })

    it("should throw error when attempting to remove no markers", async function() {
        const removeMarkersButton = await MultipleMarkers.removeMarkersButton;
        const commandOutput = await $((await MultipleMarkers.commandOutputTextarea).selector).$('textarea');

        await removeMarkersButton.tap()
        await pause(500);
        await expect(commandOutput).toHaveValue('Invalid Arguments Provided: markerIds requires at least one marker id.');
    })
})