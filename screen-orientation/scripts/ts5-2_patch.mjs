/* global console */
// @ts-check
/// <reference types="node" />
import {
  readdir,
  mkdir,
  copyFile,
  unlink,
  rmdir,
  access,
  readFile,
  writeFile,
} from 'node:fs/promises';
import { join, extname } from 'node:path';

const sourceDir = './dist/esm';
const targetDir = './dist/esm/TS_5-2';

const replaceInFile = async (filePath, regex, replacement) => {
  try {
    const data = await readFile(filePath, 'utf8');
    const result = data.replace(regex, replacement);
    await writeFile(filePath, result, 'utf8');
  } catch (err) {
    console.error(`Error processing file ${filePath}: ${err}`);
  }
};

async function deleteDirectory(dir) {
  let entries = await readdir(dir, { withFileTypes: true });

  await Promise.all(
    entries.map(entry => {
      let fullPath = join(dir, entry.name);
      return entry.isDirectory() ? deleteDirectory(fullPath) : unlink(fullPath);
    }),
  );

  await rmdir(dir);
}

async function copyDtsFilesAndPatch() {
  try {
    // Delete target directory if it exists
    if (
      await access(targetDir)
        .then(() => true)
        .catch(() => false)
    ) {
      await deleteDirectory(targetDir);
    }
    // Create target directory if it doesn't exist
    await mkdir(targetDir, { recursive: true });

    const files = await readdir(sourceDir);

    for (const file of files) {
      if (extname(file) === '.ts') {
        const sourceFile = join(sourceDir, file);
        const targetFile = join(targetDir, file);

        await copyFile(sourceFile, targetFile);
      }
    }

    const regex =
      /import\stype\s\{\sPluginListenerHandle\s\}\sfrom\s'@capacitor\/core';/;
    const replacement = `import type { PluginListenerHandle } from '@capacitor/core';
declare global {
\ttype OrientationLockType =
\t\t| 'any'
\t\t| 'natural'
\t\t| 'landscape'
\t\t| 'portrait'
\t\t| 'portrait-primary'
\t\t| 'portrait-secondary'
\t\t| 'landscape-primary'
\t\t| 'landscape-secondary';
}`;
    const fileToPatch = join(targetDir, 'definitions.d.ts');
    await replaceInFile(fileToPatch, regex, replacement);

    console.log('TypeScript declaration files for TS 5.2+ Patched.');
  } catch (err) {
    console.error('Error occurred:', err);
  }
}

copyDtsFilesAndPatch();
