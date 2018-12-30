import React, { Component } from 'react';
import './App.css';
import AppBar from '@material-ui/core/AppBar';
import Tabs from '@material-ui/core/Tabs';
import Tab from '@material-ui/core/Tab';
import Dashboard from './Dashboard';
import MeasureEditor from './MeasureEditor';
import { MeasureRepository } from './repositories/WebMeasureRepository'
// import { MeasureRepository } from './spec/repositories/FakeMeasureRepository'

class App extends Component {

  state = {
    currentTabValue: 1,
  }

  render() {
    return <div className='container'>
      <nav data-testid='navigation'>
        <AppBar>
          <Tabs
            classes={{ root: 'navigation' }}
            data-testid='navigation'
            value={this.state.currentTabValue} onChange={(e, v) => {
              this.setState({ currentTabValue: v })
            }}>
            <Tab data-testid='dashboard' label='Dashboard' />
            <Tab data-testid='measure-editor' label='Measure Editor' />
          </Tabs>
        </AppBar>
      </nav>
      <div className='content' data-testid='content'>
        {(() => {
          switch (this.state.currentTabValue) {
            case 0: return <Dashboard />;
            case 1: return <MeasureEditor measureRepository={new MeasureRepository()} />;
            default: return <Dashboard />;
          }
        })()}
      </div>
    </div>
  }
}

export default App;
