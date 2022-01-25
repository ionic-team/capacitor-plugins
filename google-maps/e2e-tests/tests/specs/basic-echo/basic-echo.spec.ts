import * as IonicE2E from '@ionic/e2e';
import { waitForLoad, pause, setDevice, switchToWeb, url } from '@ionic/e2e';

import SetAndGetValuePage from '../../pageobjects/basic-echo/basic-echo.page';

describe.skip('Google Maps - Basic Echo', function () {
  before(async function () {
    await waitForLoad();
    await switchToWeb();
    await url('/basic-echo/run-basic-echo');
    await pause(500);
  });

  beforeEach(async function () {
    await setDevice(IonicE2E.Device.Mobile);
    await switchToWeb();
    await SetAndGetValuePage.hideToolBars();
  });

  after(async function () {
    await switchToWeb();
    await SetAndGetValuePage.showToolBars();
    await pause(500);
  });

  it('should run a basic echo from plugin.', async () => {
    const runEchoButton = await SetAndGetValuePage.runEchoButton;
    const commandOutput = await $((await SetAndGetValuePage.commandOutputTextarea).selector).$('textarea');

    await runEchoButton.tap();
    await expect(commandOutput).toHaveValue('WOW!');
  });
});
