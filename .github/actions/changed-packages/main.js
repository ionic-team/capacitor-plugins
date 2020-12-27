require = require('esm')(module);

const { relative, resolve } = require('path');
const core = require('@actions/core')

const { root } = require('../../../scripts/lib/repo.mjs');
const { execute } = require('../../../scripts/lib/cli.mjs');
const { ls } = require('../../../scripts/lib/lerna.mjs');

execute(async () => {
  const changedFiles = JSON.parse(core.getInput('changed-files', '[]')).map(f => resolve(root, f));
  const packages = (await ls()).map(pkg => ({ ...pkg, relativeLocation: relative(root, pkg.location) }));
  const changedPackages = packages.filter(pkg => changedFiles.some(f => f.startsWith(pkg.location)));
  const changedPackagesFormatted = JSON.stringify(changedPackages);

  core.info(`Changed packages: ${changedPackagesFormatted}`);
  core.setOutput('changed-packages', changedPackagesFormatted);
});
