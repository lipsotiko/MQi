import * as React from 'react';
import MqiRouter from './MqiRouter';

class App extends React.Component {
    render() {
        return (
            <div className="mqi">
                <header className="mqi-header">
                    <h1 className="mqi-logo">MQi</h1>
                </header>
                <MqiRouter />
            </div>
        );
    }
}

export default App;
