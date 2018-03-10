import * as React from 'react';
import Jobs from './containers/Jobs';
import Measures from './containers/Measures';
import { BrowserRouter as Router, Switch, Route, Link } from 'react-router-dom';

class MqiRouter extends React.Component {
    render() {
        return (
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
        );
    }
}

export default MqiRouter;