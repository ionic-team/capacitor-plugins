# Contributing

See [`CONTRIBUTING.md`](https://github.com/ionic-team/capacitor/blob/HEAD/CONTRIBUTING.md) in the Capacitor repo for more general contribution guidelines.

## Developing Capacitor Plugins

### Local Setup

1. Fork and clone this repo.
2. Install the monorepo dependencies.

    ```shell
    npm install
    ```

3. Install SwiftLint if you're on macOS. Contributions to iOS code will be linted in CI if you don't have macOS.

    ```shell
    brew install swiftlint
    ```

Sometimes, it may be necessary to work on Capacitor in parallel with the plugin(s). In this case, a few extra steps are necessary:

4. Follow the Capacitor repo's [local setup instructions](https://github.com/ionic-team/capacitor/blob/HEAD/CONTRIBUTING.md#local-setup).
5. Toggle each plugin to use your local copy of Capacitor.

    ```shell
    npm install
    npm run toggle-local
    ```

    :bulb: *Remember not to commit unnecessary changes to `package.json` and `package-lock.json`.*

6. Make sure your app is using local copies of the Capacitor plugin and Capacitor core.

    ```shell
    cd my-app/
    npm install ../path/to/capacitor-plugins/<plugin>
    npm install ../path/to/capacitor/core
    npm install ../path/to/capacitor/android
    npm install ../path/to/capacitor/ios
    ```

### Monorepo Scripts

To aid in managing these plugins, this repo has a variety of scripts (located in `scripts/`) that can be run with `npm`.

#### `npm run set-capacitor-version "<version>"`

This script is for setting the version (or version range) of Capacitor packages in each plugin's `package.json`.

#### `npm run toggle-local`

> :memo: Requires [Capacitor](https://github.com/ionic-team/capacitor/) to be cloned in a sibling directory.

This script is for switching between Capacitor packages from npm and Capacitor packages installed locally.

> If you get npm errors, you can try installing from scratch:
>
> 1. Reset the changes in `package.json` files.
> 1. Clear out all `node_modules`.
>
>     ```shell
>     npx lerna exec 'rm -fr package-lock.json && rm -fr node_modules'
>     rm -fr node_modules
>     ```
> 1. Install with local dependencies:
>
>     ```
>     npm run toggle-local
>     ```

#### `npm run apply-patches "<package>"`

This script is for copying git changes from one plugin to all plugins.

To use:

1. Make sure your staging area is clean, e.g. `git reset`
1. Stage the changes from (and only from) your package, e.g. `git add -p -- text-zoom/`
1. Run the script with `<package>` being the npm name of your package, e.g. `npm run apply-patches @capacitor/text-zoom`
