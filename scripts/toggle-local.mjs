import { resolve } from 'path';

import { PROJECTS } from './lib/capacitor.mjs';
import { execute } from './lib/cli.mjs';
import { readJson } from './lib/fs.mjs';
import { bootstrap, getLernaPackagePaths } from './lib/repo.mjs';
import { setLernaPackageDependencies } from './lib/version.mjs';

execute(async () => {
  const [path] = await getLernaPackagePaths();
  const pkg = await readJson(resolve(path, 'package.json'));

  const packages = Object.fromEntries(PROJECTS.map(project => [
    `@capacitor/${project}`,
    pkg.devDependencies[`@capacitor/${project}`].startsWith('file:') ? 'next' : `file:../../capacitor/${project}`,
  ]));

  await setLernaPackageDependencies(packages, 'devDependencies');
  await bootstrap();
});
