import { resolve } from 'path';

import { PROJECTS } from './lib/capacitor.mjs';
import { readJson, writeJson } from './lib/fs.mjs';
import { root } from './lib/repo.mjs';

const run = async () => {
  const p = resolve(root, 'package.json');
  const pkg = await readJson(p);

  for (const project of PROJECTS) {
    pkg.devDependencies[`@capacitor/${project}`] = pkg.devDependencies[`@capacitor/${project}`].startsWith('file:')
      ? 'next'
      : `file:../capacitor/${project}`;
  }

  await writeJson(p, pkg);
};

run();
