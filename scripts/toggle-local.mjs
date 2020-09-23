import { resolve } from 'path';

import { PROJECTS } from './lib/capacitor.mjs';
import { execute } from './lib/cli.mjs';
import { readJson } from './lib/fs.mjs';
import { bootstrap, ls } from './lib/lerna.mjs';
import { setLernaPackageDependencies } from './lib/version.mjs';

execute(async () => {
  const [path] = (await ls()).map(p => p.location);
  const pkg = await readJson(resolve(path, 'package.json'));

  const packages = Object.fromEntries(PROJECTS.map(project => [
    `@capacitor/${project}`,
    pkg.devDependencies[`@capacitor/${project}`].startsWith('file:') ? '^3.0.0-alpha.4' : `file:../../capacitor/${project}`,
  ]));

  await setLernaPackageDependencies(packages, 'devDependencies');
  await bootstrap();
});
