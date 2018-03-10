import * as React from 'react';
import MqiRouter from './MqiRouter';
import './App.css';

const logo = require('./images/logo.svg');

class App extends React.Component {
    render() {
        return (
            <div className="mqi">
                <header className="mqi-header">
                    <img src={logo} className="mqi-logo" alt="logo"/>
                    <h1 className="mqi-title">Medical Quality Informatics</h1>
                </header>
                <MqiRouter />
            </div>
        );
    }
}

export default App;
