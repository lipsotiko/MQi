import * as React from 'react';
import './App.css';
import Jobs from './components/Jobs';
import Measures from './components/Measures';
import { BrowserRouter as Router, Switch, Route, Link } from 'react-router-dom';

const logo = require('./logo.svg');

class App extends React.Component {
    render() {
        return (
            <div className="App">
                <header className="App-header">
                    <img src={logo} className="App-logo" alt="logo"/>
                    <h1 className="App-title">Medical Quality Informatics</h1>
                </header>
                <Router>
                    <div>
                        <ul>
                            <li><Link to={'/jobs'}>Jobs</Link></li>
                            <li><Link to={'/measures'}>Measures</Link></li>
                        </ul>
                        <hr/>
                        <Switch>
                            <Route path="/jobs" component={Jobs}/>
                            <Route path="/measures" component={Measures}/>
                        </Switch>
                    </div>
                </Router>

            </div>
        );
    }
}

export default App;
