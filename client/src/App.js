import React, { Component } from 'react';
import './App.css';
import Dashboard from './Dashboard';
import MeasureEditor from './MeasureEditor';

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
        <div className='navigation'>
          {this.pages.map(page => this.navigationMenuItem(page.name))}
        </div>
      </header>
      <div className='content'>
        {(() => {
          switch (this.state.currentPageName) {
            case "Dashboard": return <Dashboard />;
            case "Measure Editor": return <MeasureEditor />;
            default: return <Dashboard />;
          }
        })()}
      </div>
    </div>
  }

  navigationMenuItem(pageName) {
    return <nav
      key={pageName}
      onClick={() => this.navigate(pageName)}
      className={this.state.currentPageName === pageName ? 'selected' : ''}>{pageName}
    </nav>
  }
}

export default App;
