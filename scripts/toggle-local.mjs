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

  const packages = Object.fromEntries(
    await Promise.all(
      PROJECTS.map(async project => [
        `@capacitor/${project}`,
        pkg.devDependencies[`@capacitor/${project}`].startsWith('file:')
          ? `^${await getLatestVersion(`@capacitor/${project}`, 'next')}`
          : `file:../../capacitor/${project}`,
      ]),
    ),
  );

  await setLernaPackageDependencies(packages, 'devDependencies');
  await bootstrap();
});
