import React, { Component } from 'react';
import MeasureList from './MeasureList';
import Footer from './Footer';
import Navigaiton from './Navigation';
import MeasureProgressWsClient from './MeasureProgressWsClient';
import { _selectMeasure, _deleteMeasures, _processMeasures } from './Shared'

class Reporting extends Component {

  state = {
    measure: null,
    measureList: [],
  }

  async componentDidMount() {
    const measureList = await this.props.measureRepository._findAllMeasureListItems();
    this.setState({ measureList });
  }

  render = () => {
    const { measure, measureList } = this.state;

    return (<>
      <Navigaiton currentTab={this.props.currentTab} setTab={this.props.setTab} />
      <form className='content reporting'>
        <aside className='left-aside'>
          <MeasureList
            measuresList={this.state.measureList}
            getMeasure={() => { }}
            selectMeasure={_selectMeasure(this)}
            displayAddBtn={false}
            selectedMeasureId={this.props.measure ? this.props.measure.measureId : null} />
        </aside>
        <div className='measure center-content' data-testid='reporting'>
          <aside className='right-aside'>
          </aside>
        </div>
        <div className='footer'>
          {measure &&
            <Footer
              measure={measure}
              deleteMeasures={async () => await _deleteMeasures(this)}
              processMeasures={async () => await _processMeasures(this)} />
          }
        </div>
      </form>
      <MeasureProgressWsClient
        measureList={measureList}
        updateMeasureList={(measureList) => {
          this.setState({ measureList })
        }} />
    </>)
  }
}

export default Reporting;
