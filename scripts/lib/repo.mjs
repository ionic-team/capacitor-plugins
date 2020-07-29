import { fileURLToPath } from 'url';
import { dirname, resolve } from 'path';

import { readJson } from './fs.mjs';
import { pipe } from './fn.mjs';

export const root = pipe(fileURLToPath, ...Array(3).fill(dirname))(import.meta.url);

export const readLernaConfig = async () => await readJson(resolve(root, 'lerna.json'));
export const getLernaPackagePaths = async () => (await readLernaConfig()).packages.map(p => resolve(root, p))
