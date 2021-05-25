import { PROJECTS } from './lib/capacitor.mjs';
import { execute } from './lib/cli.mjs';
import { bootstrap } from './lib/lerna.mjs';
import {
  getLatestVersion,
  setLernaPackageDependencies,
} from './lib/version.mjs';

execute(async () => {
  const packages = Object.fromEntries(
    await Promise.all(
      PROJECTS.map(async project => [
        `@capacitor/${project}`,
        `^${await getLatestVersion(`@capacitor/${project}`, 'latest')}`,
      ]),
    ),
  );

  await setLernaPackageDependencies(packages, 'devDependencies');
  await setLernaPackageDependencies(packages, 'peerDependencies');
  await bootstrap();
});
