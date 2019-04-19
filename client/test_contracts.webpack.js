import "@babel/polyfill";
var context = require.context('./src/contract_tests', true, /contract.js$/);
context.keys().forEach(context);
process.env.TEST = true;
