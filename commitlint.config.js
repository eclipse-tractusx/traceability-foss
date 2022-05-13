module.exports = {
  extends: ['@commitlint/config-conventional'],
  rules: {
    'header-max-length': [2, 'always', 72],
    'type-enum': [2, 'always', ['chr', 'doc', 'fix', 'ftr', 'rfr', 'rev', 'tst', 'wip']],
  },
  plugins: [
    {
      rules: {
        'jira-issue': (parsed, when = 'always', prefixes = []) => {
          const { body } = parsed;

          const negated = when === 'never';
          const result = body ? prefixes.some(prefix => body.includes(`${prefix}-`)) : false;

          return [
            negated ? !result : result,
            [
              `body must`,
              negated ? `not` : null,
              `include a Jira issue id with one of these prefixes: ${prefixes.join(', ')}`,
            ]
              .filter(Boolean)
              .join(' '),
          ];
        },
      },
    },
  ],
};
