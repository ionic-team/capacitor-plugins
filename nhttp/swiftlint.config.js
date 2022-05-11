/* eslint-disable no-undef */
module.exports = {
  ...require('@ionic/swiftlint-config'),
  // eslint-disable-next-line no-template-curly-in-string
  included: ['${PWD}/ios/Plugin'],
  identifier_name: {
    min_length: 1,
  },
};
