import React, { Component } from 'react';
import './App.css';

import Reporting from './Reporting';
import MeasureEditor from './MeasureEditor';

class App extends Component {

  constructor(props) {
    super(props);
    this.state = {
      currentTabValue: 0
    }
  }

  render() {
    return <div className='container'>
      <div className='content' data-testid='content'>
        {
          this.state.currentTabValue === 0 && <Reporting
            setTab={this._setTab()}
            currentTab={this.state.currentTabValue}
            measureRepository={this.props.measureRepository}
            resultsRepository={this.props.resultsRepository} />
        }
        {
           this.state.currentTabValue === 1 && <MeasureEditor
            setTab={this._setTab()}
            currentTab={this.state.currentTabValue}
            measureRepository={this.props.measureRepository} />
        }
      </div>
    </div>
  }

  _setTab() {
    return (e, v) => {
      this.setState({ currentTabValue: v })
    }
  }
}

export default App;
