require = require('esm')(module);

const core = require('@actions/core')
const { execute } = require('../../../scripts/lib/cli.mjs');
const { ls } = require('../../../scripts/lib/lerna.mjs');

execute(async () => {
  const changedFiles = core.getInput('changed-files', []);
  const packages = await ls();

  console.log(changedFiles, packages);
});
