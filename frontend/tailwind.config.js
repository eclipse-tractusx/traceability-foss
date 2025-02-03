// tailwind.config.js
const cofinityxConfig = require('./tailwind.config.cofinityx.js');
const catenaxConfig = require('./tailwind.config.catenax.js');
const theme = process.env.THEME; 

module.exports = theme === 'cofinityx' ? cofinityxConfig : catenaxConfig;
