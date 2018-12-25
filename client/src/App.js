import React, { Component } from 'react';
import './App.css';
import Dashboard from './Dashboard';
import MeasureEditor from './MeasureEditor';
import { WebMeasureRepository } from './repositories/WebMeasureRepository.js'

class App extends Component {

  pages = [
    { name: 'Dashboard' },
    { name: 'Measure Editor' }
  ]

  state = {
    currentPageName: 'Measure Editor',
    navigate: this.navigate.bind(this)
  }

  navigate(page) {
    this.setState({ currentPageName: page });
  }

  render() {
    return <div className='container'>
      <header className="header">
        <nav className='navigation' data-testid='navigation'>
          {this.pages.map(page => this.navigationMenuItem(page.name))}
        </nav>
      </header>
      <div className='content' data-testid='content'>
        {(() => {
          switch (this.state.currentPageName) {
            case "Dashboard": return <Dashboard />;
            case "Measure Editor": return <MeasureEditor measureRepository={new WebMeasureRepository()} />;
            default: return <Dashboard />;
          }
        })()}
      </div>
    </div>
  }

  navigationMenuItem(pageName) {
    return <div
      key={pageName}
      data-testid={pageName}
      onClick={() => this.navigate(pageName)}
      className={this.state.currentPageName === pageName ? 'selected' : ''}>{pageName}
    </div>
  }
}

export default App;
