import { resolve } from 'path';

import { PROJECTS } from './lib/capacitor.mjs';
import { execute } from './lib/cli.mjs';
import { readJson } from './lib/fs.mjs';
import { bootstrap, ls } from './lib/lerna.mjs';
import {
  getLatestVersion,
  setLernaPackageDependencies,
} from './lib/version.mjs';

execute(async () => {
  const [path] = (await ls()).map(p => p.location);
  const pkg = await readJson(resolve(path, 'package.json'));

  const entries = new Map();

  for (const project of PROJECTS) {
    if (pkg.devDependencies[`@capacitor/${project}`]) {
      entries.set(
        `@capacitor/${project}`,
        pkg.devDependencies[`@capacitor/${project}`].startsWith('file:')
          ? `^${await getLatestVersion(`@capacitor/${project}`, 'next')}`
          : `file:../../capacitor/${project}`,
      );
    }
  }

  const packages = Object.fromEntries(entries);

  await setLernaPackageDependencies(packages, 'devDependencies');
  await bootstrap();
});
