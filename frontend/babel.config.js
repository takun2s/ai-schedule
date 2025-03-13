module.exports = {
  presets: [
    ['@vue/cli-plugin-babel/preset', { useBuiltIns: 'usage', corejs: 3 }]
  ],
  plugins: [
    '@babel/plugin-proposal-class-properties',
    '@babel/plugin-proposal-private-methods',
    '@babel/plugin-proposal-nullish-coalescing-operator',
    '@babel/plugin-proposal-optional-chaining',
    '@babel/plugin-transform-runtime'
  ]
}
