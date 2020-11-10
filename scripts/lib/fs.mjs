import * as fs from 'fs';
import * as util from 'util';
import { dirname, join } from 'path';

export const readFile = util.promisify(fs.readFile);
export const readJson = async p => JSON.parse(await readFile(p));
export const writeFile = util.promisify(fs.writeFile);
export const writeJson = async (p, contents, space = 2) =>
  writeFile(p, JSON.stringify(contents, undefined, space) + '\n');
export const stat = util.promisify(fs.stat);
export const readdir = util.promisify(fs.readdir);
export const mkdir = util.promisify(fs.mkdir);
export const rmdir = util.promisify(fs.rmdir);

const copyFile = util.promisify(fs.copyFile);

export async function copy(src, dest) {
  const s = await stat(src);
  if (s.isDirectory()) {
    const items = await readdir(src);
    await Promise.all(
      items.map(async item => {
        const srcItem = join(src, item);
        const destItem = join(dest, item);
        await copy(srcItem, destItem);
      }),
    );
  } else if (s.isFile()) {
    await mkdir(dirname(dest), { recursive: true });
    await copyFile(src, dest);
  }
}
