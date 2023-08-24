import { PROJECTS, PEERPROJECTS } from './lib/capacitor.mjs';
import { execute } from './lib/cli.mjs';
import { setLernaPackageDependencies } from './lib/version.mjs';
import { bootstrap } from './lib/lerna.mjs';

execute(async () => {
  const packages = Object.fromEntries(
    PROJECTS.map(project => [`@capacitor/${project}`, process.argv[2]]),
  );
  const peerPackages = Object.fromEntries(
    PEERPROJECTS.map(project => [`@capacitor/${project}`, process.argv[2]]),
  );

  await setLernaPackageDependencies(packages, 'devDependencies');
  await setLernaPackageDependencies(peerPackages, 'peerDependencies');
  await bootstrap();
});
