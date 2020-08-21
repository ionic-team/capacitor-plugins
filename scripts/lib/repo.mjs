import { fileURLToPath } from 'url';
import { dirname, resolve, delimiter, sep } from 'path';

import { readJson } from './fs.mjs';
import { pipe } from './fn.mjs';
import { run } from './subprocess.mjs';

export const root = pipe(fileURLToPath, ...Array(3).fill(dirname))(import.meta.url);

export const readLernaConfig = async () => await readJson(resolve(root, 'lerna.json'));
export const getLernaPackagePaths = async () => (await readLernaConfig()).packages.map(p => resolve(root, p))

const stdio = 'inherit';
const cwd = root;
const env = { ...process.env, PATH: `${root}${sep}node_modules${sep}.bin${delimiter}${process.env.PATH}` };
export const bootstrap = async () => run('lerna', ['bootstrap'], { stdio, cwd, env });
