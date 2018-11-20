import React, { Component } from 'react';
import './App.css';
import Navigation from './Navigation.js'
import Dashboard from './Dashboard';
import MeasureEditor from './MeasureEditor';


class App extends Component {

  constructor(props) {
    super(props);
    this.state = { currentPage: 'Dashboard' };
    this.setCurrentPage = this.setCurrentPage.bind(this);
  }

  componentDidMount() {
    fetch('/measure_list')
      .then((response) => response.json())
      .then((json) => console.log(json));
  }

  setCurrentPage(page) {
    this.setState({currentPage: page});
  }

  render() {
    return (
      <div className="App">
        <header className="App-header">
          <Navigation navigate={this.setCurrentPage}/>
        </header>
        <div>
        {(() => {
          switch (this.state.currentPage) {
            case "Dashboard":       return <Dashboard />;
            case "MeasureEditor":   return <MeasureEditor />;
            default:                return <Dashboard />;
          }
        })()}
        </div>
      </div>
    );
  }
}


export default App;
