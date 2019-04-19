import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './App';
import * as serviceWorker from './serviceWorker';
import 'typeface-roboto';
import { MeasureRepository } from './repositories/WebMeasureRepository'
import { ResultsRepository } from './repositories/WebResultsRepository'

ReactDOM.render(<App measureRepository={new MeasureRepository()} resultsRepository={new ResultsRepository()} />,
    document.getElementById('root'));

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: http://bit.ly/CRA-PWA
serviceWorker.unregister();
