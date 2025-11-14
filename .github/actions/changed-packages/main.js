const { relative, resolve } = require('path');
const core = require('@actions/core');

(async () => {
    const { root } = await import('../../../scripts/lib/repo.mjs');
    const { execute } = await import('../../../scripts/lib/cli.mjs');
    const { ls } = await import('../../../scripts/lib/lerna.mjs');

    execute(async () => {
        const files = JSON.parse(core.getInput('files', '[]')).map(f => resolve(root, f));
        const packages = await ls();
        const changedPackages = packages.filter(pkg => files.some(f => f.startsWith(pkg.location)));
        const paths = JSON.stringify(changedPackages.map(pkg => relative(root, pkg.location)));

        core.info(`Changed package paths: ${paths}`);
        core.setOutput('paths', paths);
    });
})();

