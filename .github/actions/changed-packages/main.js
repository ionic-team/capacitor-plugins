require = require('esm')(module);

const { resolve } = require('path');
const core = require('@actions/core')

const { root } = require('../../../scripts/lib/repo.mjs');
const { execute } = require('../../../scripts/lib/cli.mjs');
const { ls } = require('../../../scripts/lib/lerna.mjs');

execute(async () => {
  const changedFiles = JSON.parse(core.getInput('changed-files', '[]')).map(f => resolve(root, f));
  const packages = await ls();
  const changedPackages = packages.filter(pkg => changedFiles.some(f => f.startsWith(pkg.location)));
  const changedNames = changedPackages.map(pkg => pkg.name);

  core.info(`Changed packages: ${changedNames}`);
  core.setOutput('changed-packages', changedNames);
});
