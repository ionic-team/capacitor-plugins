import { resolve } from 'path';

import { PROJECTS } from './lib/capacitor.mjs';
import { readJson, writeJson } from './lib/fs.mjs';
import { getLernaPackagePaths } from './lib/repo.mjs';

const updatePackageJson = async (path) => {
  const pkg = await readJson(path);

  for (const project of PROJECTS) {
    pkg.devDependencies[`@capacitor/${project}`] = pkg.devDependencies[`@capacitor/${project}`].startsWith('file:')
      ? 'next'
      : `file:../../capacitor/${project}`;
  }

  await writeJson(path, pkg);
};

const run = async () => {
  const paths = await getLernaPackagePaths();

  for (const path of paths) {
    await updatePackageJson(resolve(path, 'package.json'));
  }
};

run();
