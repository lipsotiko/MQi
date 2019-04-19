import "@babel/polyfill";
var context = require.context('./src/spec', true, /spec.js$/);
context.keys().forEach(context);
process.env.TEST = true;
