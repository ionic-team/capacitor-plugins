import { basename } from 'path';
import { Readable } from 'stream';

import { execute } from './lib/cli.mjs';
import * as lerna from './lib/lerna.mjs';
import { confirmOrExit } from './lib/prompts.mjs';
import { root } from './lib/repo.mjs';
import { exec, spawn, wait } from './lib/subprocess.mjs';

execute(async () => {
  if (!process.argv[2]) {
    console.error('ERR: Supply lerna package for source of patch as first argument.');
    process.exit(1);
  }

  const packages = await lerna.ls();
  const source = packages.find(p => p.name === process.argv[2]);
  const targets = packages.filter(p => p !== source);

  if (!source) {
    console.error(`ERR: ${source} is not a valid lerna package name.`);
    process.exit(1);
  }

  const [diff, coloredDiff] = (await Promise.all([
    exec(`git diff --cached -- ${source.location}`, { cwd: root }),
    exec(`git diff --cached --color -- ${source.location}`, { cwd: root }),
  ])).map(result => result.stdout);

  const sourceDirectory = basename(source.location);

  console.log(customizePatch(coloredDiff, sourceDirectory, '<plugin>'));

  await confirmOrExit('Make the changes above to all plugins?');

  for (const target of targets) {
    const targetDirectory = basename(target.location);
    const s = Readable.from(customizePatch(diff, sourceDirectory, targetDirectory));
    const p = spawn('git', ['apply'], { cwd: root, stdio: ['pipe', 'inherit', 'inherit'] });
    s.pipe(p.stdin);
    await wait(p);
  }

  console.log('Done!');
});

const customizePatch = (diff, source, target) => {
  const re = new RegExp(` (a|b)\\/${source}`, 'gm');

  return diff.replace(re, ` $1/${target}`);
};
