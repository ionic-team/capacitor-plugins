import commonjs from '@rollup/plugin-commonjs';

export default {
  input: 'electron/build/electron/src/index.js',
  output: [
    {
      file: 'electron/dist/plugin.js',
      format: 'cjs',
      sourcemap: true,
    },
  ],
  external: ['@capacitor/core'],
  plugins: [commonjs()],
};
