export const extend = (target: any, ...objs: any[]): any => {
  objs.forEach(o => {
    if (o && typeof o === 'object') {
      for (const k in o) {
        if (Object.prototype.hasOwnProperty.call(o, k)) {
          target[k] = o[k];
        }
      }
    }
  });
  return target;
};
