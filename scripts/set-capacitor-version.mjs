import { PROJECTS, PEERPROJECTS } from './lib/capacitor.mjs';
import { execute } from './lib/cli.mjs';
import { root } from './lib/repo.mjs';
import { run } from './lib/subprocess.mjs';
import { setLernaPackageDependencies } from './lib/version.mjs';

execute(async () => {
  const packages = Object.fromEntries(
    PROJECTS.map(project => [`@capacitor/${project}`, process.argv[2]]),
  );
  const peerPackages = Object.fromEntries(
    PEERPROJECTS.map(project => [`@capacitor/${project}`, process.argv[2]]),
  );

  await setLernaPackageDependencies(packages, 'devDependencies');
  await setLernaPackageDependencies(peerPackages, 'peerDependencies');
  await run('npm', ['install'], { cwd: root, stdio: 'inherit' });
});
