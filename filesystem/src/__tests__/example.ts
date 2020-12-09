import { test } from 'uvu';
import * as assert from 'uvu/assert';

test('Example', () => {
  const actual = 2 + 2;
  const expected = 4;
  assert.equal(actual, expected);
});

test.run();
