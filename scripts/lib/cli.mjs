export const execute = (fn) => {
  fn().catch((e) => {
    process.stderr.write(e.stack ? e.stack : String(e));
    process.exit(1);
  });
};
