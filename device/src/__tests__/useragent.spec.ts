import { test } from 'uvu';
import * as assert from 'uvu/assert';

import { DeviceWeb } from '../web';

const web = new DeviceWeb();

test('Chrome', () => {
  // Mock empty navigator/window objects
  (global as any).navigator = {};
  (global as any).window = { chrome: true };

  const userAgents = [
    'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36', // Chrome 87 on Windows
    'Mozilla/5.0 (Macintosh; Intel Mac OS X 11_0_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36', // Chrome 87 MacOS
    'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36', // Chrome 87 on Linux
    'Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.86 Mobile Safari/537.36', // Chrome 87 Android
    'Mozilla/5.0 (Linux; Android 10; SM-A205U) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.86 Mobile Safari/537.36', // Chrome 87 on Android (Samsung)
    'Mozilla/5.0 (iPhone; CPU iPhone OS 14_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/87.0.4280.77 Mobile/15E148 Safari/604.1', // "ChromeRevision" 87 on iPhone
  ];

  const expected = [
    '87.0.4280.88',
    '87.0.4280.88',
    '87.0.4280.88',
    '87.0.4280.86',
    '87.0.4280.86',
    '87.0.4280.77',
  ];

  for (const [index, ua] of userAgents.entries()) {
    const parsed = web.parseUa(ua);
    const actual = parsed.browserVersion;
    assert.is(actual, expected[index]);
  }
});

test('Firefox', () => {
  (global as any).navigator = {};
  (global as any).window = { InstallTrigger: true };
  const userAgents = [
    'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:83.0) Gecko/20100101 Firefox/83.0', // Firefox 83 on Windows
    'Mozilla/5.0 (Macintosh; Intel Mac OS X 11.0; rv:83.0) Gecko/20100101 Firefox/83.0', // Firefox 83 on MacOS
    'Mozilla/5.0 (X11; Linux i686; rv:83.0) Gecko/20100101 Firefox/83.0', // Firefox 83 on Linux
    'Mozilla/5.0 (Android 11; Mobile; rv:68.0) Gecko/68.0 Firefox/83.0', // Firefox 83 on Android
    'Mozilla/5.0 (iPhone; CPU iPhone OS 11_0_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) FxiOS/29.0 Mobile/15E148 Safari/605.1.15', // Firefox Mobile 29 on iPhone
  ];

  const expected = ['83.0', '83.0', '83.0', '83.0', '29.0'];

  for (const [index, ua] of userAgents.entries()) {
    const parsed = web.parseUa(ua);
    const actual = parsed.browserVersion;
    assert.is(actual, expected[index]);
  }
});

test('Safari', () => {
  (global as any).navigator = {};
  (global as any).window = { ApplePaySession: true };
  const userAgents = [
    'Mozilla/5.0 (Macintosh; Intel Mac OS X 11_0_1) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0.1 Safari/605.1.15', // Safari 14 on MacOS
    'Mozilla/5.0 (iPhone; CPU iPhone OS 14_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0 Mobile/15E148 Safari/604.1', // Safari 14 on iPhone
  ];

  const expected = ['14.0.1', '14.0'];

  for (const [index, ua] of userAgents.entries()) {
    const parsed = web.parseUa(ua);
    const actual = parsed.browserVersion;
    assert.is(actual, expected[index]);
  }
});

test('Edge', () => {
  // this is true on early chromium edge, and false for older and latest versions. It should pass for both true or false
  (global as any).window = { chrome: true };
  (global as any).navigator = {};

  const userAgents = [
    'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.140 Safari/537.36 Edge/18.17763', // Edge 18 on Windows 10
    'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.140 Safari/537.36 Edge/17.17134', // Edge 17 on Windows 10
    'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.121 Safari/537.36 Edg/85.0.564.63', // Edge 85 on Windows 10
    'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36 Edg/87.0.664.57', // Edge 87 on Windows 10
    'Mozilla/5.0 (Macintosh; Intel Mac OS X 11_0_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36 Edg/87.0.664.57', // Edge 87 on MacOS
    'Mozilla/5.0 (iPhone; CPU iPhone OS 14_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0 EdgiOS/45.11.1 Mobile/15E148 Safari/605.1.15', // Edge 45 on iOS
  ];

  const expected = [
    '18.17763',
    '17.17134',
    '85.0.564.63',
    '87.0.664.57',
    '87.0.664.57',
    '45.11.1',
  ];

  for (const [index, ua] of userAgents.entries()) {
    const parsed = web.parseUa(ua);
    const actual = parsed.browserVersion;
    assert.is(actual, expected[index]);
  }

  (global as any).window = { chrome: false };
  (global as any).navigator = {};
  for (const [index, ua] of userAgents.entries()) {
    const parsed = web.parseUa(ua);
    const actual = parsed.browserVersion;
    assert.is(actual, expected[index]);
  }
});

test.run();
