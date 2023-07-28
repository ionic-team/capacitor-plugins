/* eslint-disable no-undef */
module.exports = {
  // eslint-disable-next-line no-template-curly-in-string
  included: ['${PWD}/ios/Plugin'],
  identifier_name: {
    min_length: {
      warning: 3,
      error: 1,
    },
  },
  excluded: ['node_modules', 'ios/Pods'],
  opt_in_rules: [
    'implicitly_unwrapped_optional',
    'file_name_no_space',
    'force_unwrapping',
    'function_default_parameter_at_end',
    'lower_acl_than_parent',
    'modifier_order',
    'overridden_super_call',
    'unowned_variable_capture',
    'unused_import',
  ],
  line_length: {
    warning: 150,
    error: 300,
    ignores_function_declarations: true,
    ignores_comments: true,
    ignores_interpolated_strings: true,
    ignores_urls: true,
  },
};
