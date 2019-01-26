import React, { Component } from 'react';
import './App.css';

import Reporting from './Reporting';
import MeasureEditor from './MeasureEditor';
import { MeasureRepository } from './repositories/WebMeasureRepository'
import { ResultsRepository } from './repositories/WebResultsRepository'


class App extends Component {

  state = {
    currentTabValue: 1,
  }

  render() {
    return <div className='container'>
      <div className='content' data-testid='content'>
        {(() => {
          switch (this.state.currentTabValue) {
            case 0: return <Reporting
              setTab={this._setTab()}
              currentTab={this.state.currentTabValue}
              measureRepository={new MeasureRepository()} />;
            case 1: return <MeasureEditor
              setTab={this._setTab()}
              currentTab={this.state.currentTabValue}
              measureRepository={new MeasureRepository()} />;
            default: return <Reporting
              measureRepository={new MeasureRepository()}
              resultsRepository={new ResultsRepository()} />;
          }
        })()}
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
