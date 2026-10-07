const ionic = require('@ionic/eslint-config/recommended');

module.exports = [
  {
    ignores: [
      '**/build/**',
      '**/dist/**',
      // lint TypeScript only
      '**/*.js',
      '**/*.mjs',
      '**/*.cjs',
    ],
  },
  ...ionic,
];
