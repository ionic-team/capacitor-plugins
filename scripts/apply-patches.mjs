import { basename } from 'path';
import { Readable } from 'stream';

import { execute } from './lib/cli.mjs';
import * as lerna from './lib/lerna.mjs';
import { confirmOrExit } from './lib/prompts.mjs';
import { root } from './lib/repo.mjs';
import { exec, spawn, wait } from './lib/subprocess.mjs';

execute(async () => {
  if (!process.argv[2]) {
    process.stderr.write('ERR: Supply lerna package for source of patch as first argument.\n');
    process.exit(1);
  }

  const packages = await lerna.ls();
  const source = packages.find((p) => p.name === process.argv[2]);
  const targets = packages.filter((p) => p !== source);

  if (!source) {
    process.stderr.write(`ERR: ${source} is not a valid lerna package name.\n`);
    process.exit(1);
  }

  const [diff, coloredDiff] = (
    await Promise.all([
      exec(`git diff --cached -- ${source.location}`, { cwd: root }),
      exec(`git diff --cached --color -- ${source.location}`, { cwd: root }),
    ])
  ).map((result) => result.stdout);

  const sourceDirectory = basename(source.location);

  process.stdout.write(customizePatch(coloredDiff, sourceDirectory, '<plugin>')) + '\n';

  await confirmOrExit('Make the changes above to all plugins?');

  const succeeded = [];
  const failed = [];

  for (const target of targets) {
    try {
      const targetDirectory = basename(target.location);
      const s = Readable.from(customizePatch(diff, sourceDirectory, targetDirectory));
      const p = spawn('git', ['apply'], {
        cwd: root,
        stdio: ['pipe', 'inherit', 'inherit'],
      });
      s.pipe(p.stdin);
      await wait(p);
      succeeded.push(target);
    } catch (e) {
      await confirmOrExit(`Could not apply patch to ${target.name}. Skip and continue?`);
      failed.push(target);
    }
  }

  process.stdout.write(
    `Successfully applied the patch to:\n` +
      succeeded.map((target) => ` - ${target.name}`).join('\n') +
      `\nCould not apply the patch to:\n` +
      failed.map((target) => ` - ${target.name}`).join('\n'),
  );
});

const customizePatch = (diff, source, target) => {
  const re = new RegExp(` (a|b)\\/${source}`, 'gm');

  return diff.replace(re, ` $1/${target}`);
};
