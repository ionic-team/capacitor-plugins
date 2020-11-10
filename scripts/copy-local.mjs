import { join } from 'path';

import { PROJECTS } from './lib/capacitor.mjs';
import { copy, readJson, readdir, rmdir } from './lib/fs.mjs';
import { execute } from './lib/cli.mjs';
import { root } from './lib/repo.mjs';

execute(async () => {
  const dirItems = await readdir(root);
  for (const dirItem of dirItems) {
    for (const projectName of PROJECTS) {
      const pkgModule = join(
        root,
        dirItem,
        'node_modules',
        '@capacitor',
        projectName,
      );
      try {
        await rmdir(pkgModule, { recursive: true });
      } catch (e) {}
    }
  }

  for (const corePkgName of PROJECTS) {
    const src = join(root, '..', 'capacitor', corePkgName);
    const dest = join(root, 'node_modules', '@capacitor', corePkgName);
    await copyLocalPackage(src, dest);
  }
});

async function copyLocalPackage(src, dest) {
  console.log(`copy ${src} to ${dest}`);

  await rmdir(dest, { recursive: true });

  const srcPkgJsonPath = join(src, 'package.json');
  const destPkgJsonPath = join(dest, 'package.json');

  console.log(`  - ${srcPkgJsonPath} to ${destPkgJsonPath}`);
  await copy(srcPkgJsonPath, destPkgJsonPath);

  const pkgJson = await readJson(srcPkgJsonPath);
  if (pkgJson.files) {
    await Promise.all(
      pkgJson.files.map(async f => {
        const srcFile = join(src, f);
        const destFile = join(dest, f);
        console.log(`  - ${srcFile} to ${destFile}`);
        await copy(srcFile, destFile);
      }),
    );
  }

  console.log('');
}
