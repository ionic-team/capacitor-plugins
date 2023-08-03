// port code from https://github.com/dankogai/js-base64

const _hasatob = typeof atob === 'function';
const _hasbtoa = typeof btoa === 'function';
const _TD = typeof TextDecoder === 'function' ? new TextDecoder() : undefined;
const _TE = typeof TextEncoder === 'function' ? new TextEncoder() : undefined;
const b64ch =
  'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=';
const b64chs = Array.prototype.slice.call(b64ch);
const b64tab = ((a: string[]) => {
  const tab: Record<string, number> = {};
  a.forEach((c, i) => (tab[c] = i));
  return tab;
})(b64chs);
const b64re =
  /^(?:[A-Za-z\d+/]{4})*?(?:[A-Za-z\d+/]{2}(?:==)?|[A-Za-z\d/]{3}=?)?$/;
const _fromCC = String.fromCharCode.bind(String);
const _U8Afrom =
  typeof Uint8Array.from === 'function'
    ? Uint8Array.from.bind(Uint8Array)
    : (it: ArrayLike<number>) => new Uint8Array(Array.prototype.slice.call(it, 0));
const _mkUriSafe = (src: string) =>
  src.replace(/=/g, '').replace(/[+/]/g, (m0: string) => (m0 == '+' ? '-' : '_'));
const _tidyB64 = (s: string) => s.replace(/[^A-Za-z0-9+\\/]/g, '');
/**
 * polyfill version of `btoa`
 */
const btoaPolyfill = (bin: string) => {
  // console.log('polyfilled');
  let u32,
    c0,
    c1,
    c2,
    asc = '';
  const pad = bin.length % 3;
  for (let i = 0; i < bin.length; ) {
    if (
      (c0 = bin.charCodeAt(i++)) > 255 ||
      (c1 = bin.charCodeAt(i++)) > 255 ||
      (c2 = bin.charCodeAt(i++)) > 255
    )
      throw new TypeError('invalid character found');
    u32 = (c0 << 16) | (c1 << 8) | c2;
    asc +=
      b64chs[(u32 >> 18) & 63] +
      b64chs[(u32 >> 12) & 63] +
      b64chs[(u32 >> 6) & 63] +
      b64chs[u32 & 63];
  }
  return pad ? asc.slice(0, pad - 3) + '==='.substring(pad) : asc;
};
/**
 * does what `window.btoa` of web browsers do.
 * @param {String} bin binary string
 * @returns {string} Base64-encoded string
 */
const _btoa = _hasbtoa ? (bin: string) => btoa(bin) : btoaPolyfill;
const _fromUint8Array = (u8a: Uint8Array) => {
  // cf. https://stackoverflow.com/questions/12710001/how-to-convert-uint8-array-to-base64-encoded-string/12713326#12713326
  const maxargs = 0x1000;
  let str = '';
  for (let i = 0, l = u8a.length; i < l; i += maxargs) {
    // eslint-disable-next-line prefer-spread
    str += _fromCC.apply(
      null,
      u8a.subarray(i, i + maxargs) as unknown as number[],
    );
  }
  return _btoa(str);
};
// This trick is found broken https://github.com/dankogai/js-base64/issues/130
// const utob = (src: string) => unescape(encodeURIComponent(src));
// reverting good old fationed regexp
const cb_utob = (c: string) => {
  if (c.length < 2) {
    const cc = c.charCodeAt(0);
    return cc < 0x80
      ? c
      : cc < 0x800
      ? _fromCC(0xc0 | (cc >>> 6)) + _fromCC(0x80 | (cc & 0x3f))
      : _fromCC(0xe0 | ((cc >>> 12) & 0x0f)) +
        _fromCC(0x80 | ((cc >>> 6) & 0x3f)) +
        _fromCC(0x80 | (cc & 0x3f));
  } else {
    const cc =
      0x10000 + (c.charCodeAt(0) - 0xd800) * 0x400 + (c.charCodeAt(1) - 0xdc00);
    return (
      _fromCC(0xf0 | ((cc >>> 18) & 0x07)) +
      _fromCC(0x80 | ((cc >>> 12) & 0x3f)) +
      _fromCC(0x80 | ((cc >>> 6) & 0x3f)) +
      _fromCC(0x80 | (cc & 0x3f))
    );
  }
};
// eslint-disable-next-line no-control-regex
const re_utob = /[\uD800-\uDBFF][\uDC00-\uDFFFF]|[^\x00-\x7F]/g;
/**
 * @deprecated should have been internal use only.
 * @param {string} src UTF-8 string
 * @returns {string} UTF-16 string
 */
const utob = (u: string) => u.replace(re_utob, cb_utob);
//
const _encode = _TE ? (s: string) => _fromUint8Array(_TE.encode(s)) : (s: string) => _btoa(utob(s));
/**
 * converts a UTF-8-encoded string to a Base64 string.
 * @param {boolean} [urlsafe] if `true` make the result URL-safe
 * @returns {string} Base64 string
 */
const encode = (src: string, urlsafe = false): string =>
  urlsafe ? _mkUriSafe(_encode(src)) : _encode(src);
// This trick is found broken https://github.com/dankogai/js-base64/issues/130
// const btou = (src: string) => decodeURIComponent(escape(src));
// reverting good old fationed regexp
const re_btou =
  /[\xC0-\xDF][\x80-\xBF]|[\xE0-\xEF][\x80-\xBF]{2}|[\xF0-\xF7][\x80-\xBF]{3}/g;
const cb_btou = (cccc: string) => {
  switch (cccc.length) {
    case 4:
      // eslint-disable-next-line no-case-declarations
      const cp =
        ((0x07 & cccc.charCodeAt(0)) << 18) |
        ((0x3f & cccc.charCodeAt(1)) << 12) |
        ((0x3f & cccc.charCodeAt(2)) << 6) |
        (0x3f & cccc.charCodeAt(3));
      // eslint-disable-next-line no-case-declarations
      const offset = cp - 0x10000;
      return (
        _fromCC((offset >>> 10) + 0xd800) + _fromCC((offset & 0x3ff) + 0xdc00)
      );
    case 3:
      return _fromCC(
        ((0x0f & cccc.charCodeAt(0)) << 12) |
          ((0x3f & cccc.charCodeAt(1)) << 6) |
          (0x3f & cccc.charCodeAt(2)),
      );
    default:
      return _fromCC(
        ((0x1f & cccc.charCodeAt(0)) << 6) | (0x3f & cccc.charCodeAt(1)),
      );
  }
};
/**
 * @deprecated should have been internal use only.
 * @param {string} src UTF-16 string
 * @returns {string} UTF-8 string
 */
const btou = (b: string) => b.replace(re_btou, cb_btou);
/**
 * polyfill version of `atob`
 */
const atobPolyfill = (asc: string) => {
  // console.log('polyfilled');
  asc = asc.replace(/\s+/g, '');
  if (!b64re.test(asc)) throw new TypeError('malformed base64.');
  asc += '=='.slice(2 - (asc.length & 3));
  let u24,
    bin = '',
    r1,
    r2;
  for (let i = 0; i < asc.length; ) {
    u24 =
      (b64tab[asc.charAt(i++)] << 18) |
      (b64tab[asc.charAt(i++)] << 12) |
      ((r1 = b64tab[asc.charAt(i++)]) << 6) |
      (r2 = b64tab[asc.charAt(i++)]);
    bin +=
      r1 === 64
        ? _fromCC((u24 >> 16) & 255)
        : r2 === 64
        ? _fromCC((u24 >> 16) & 255, (u24 >> 8) & 255)
        : _fromCC((u24 >> 16) & 255, (u24 >> 8) & 255, u24 & 255);
  }
  return bin;
};
/**
 * does what `window.atob` of web browsers do.
 * @param {String} asc Base64-encoded string
 * @returns {string} binary string
 */
const _atob = _hasatob ? (asc: string) => atob(_tidyB64(asc)) : atobPolyfill;
//
const _toUint8Array = (a: string) =>
  _U8Afrom(
    _atob(a)
      .split('')
      .map(c => c.charCodeAt(0)),
  );
//
const _decode = _TD ? (a: string) => _TD.decode(_toUint8Array(a)) : (a: string) => btou(_atob(a));
const _unURI = (a: string) => _tidyB64(a.replace(/[-_]/g, (m0: string) => (m0 == '-' ? '+' : '/')));
/**
 * converts a Base64 string to a UTF-8 string.
 * @param {String} src Base64 string.  Both normal and URL-safe are supported
 * @returns {string} UTF-8 string
 */
const decode = (src: string): string => _decode(_unURI(src));
//

export { decode as fromBase64 };
export { encode as toBase64 };
