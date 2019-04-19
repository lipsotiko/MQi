import "@babel/polyfill";
var context = require.context('./src/spec/contract_tests', true, /contract.js$/);
context.keys().forEach(context);
process.env.TEST = true;
