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

Sometimes, it may be necessary to work on Capacitor in parellel with the plugin(s). In this case, a few extra steps are necessary:

4. Follow the Capacitor repo's [local setup instructions](https://github.com/ionic-team/capacitor/blob/HEAD/CONTRIBUTING.md#local-setup).
5. Toggle each plugin to use your local copy of Capacitor.

    ```shell
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

## Publishing Capacitor Plugins

Capacitor packages are published using [Lerna](https://github.com/lerna/lerna) with independent versioning.

During Capacitor 3 development, the following workflow is used to create dev releases:

1. Create the next development version. The following command will:
    * Create a release commit with a generated changelog
    * Create a git tag
    * Push to the `main` branch
    * Create a GitHub release

    <br>

    ```shell
    npx lerna version
    ```

1. Wait for CI to publish the new tagged version.
