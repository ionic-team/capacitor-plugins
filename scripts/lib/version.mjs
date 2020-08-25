import { resolve } from 'path';

import { readJson, writeJson } from './fs.mjs';
import { getLernaPackagePaths } from './repo.mjs';

export const setPackageJsonDependencies = async (path, packages, key = 'dependencies') => {
  const pkg = await readJson(path);

  for (const [dep, version] of Object.entries(packages)) {
    pkg[key][dep] = version;
  }

  await writeJson(path, pkg);
};

export const setLernaPackageDependencies = async (packages, key = 'dependencies') => {
  const paths = await getLernaPackagePaths();

  for (const path of paths) {
    await setPackageJsonDependencies(resolve(path, 'package.json'), packages, key);
  }
};
