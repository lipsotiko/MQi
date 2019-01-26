import React, { Component } from 'react';
import MeasureList from './MeasureList';
import Footer from './Footer';
import Navigaiton from './Navigation';
import MeasureProgressWsClient from './MeasureProgressWsClient';
import { selectMeasureListItemById } from './Shared'

class Reporting extends Component {

  state = {
    measure: null,
    measureList: [],
    _getMeasure: this._getMeasure.bind(this),
    _selectMeasure: this._selectMeasure.bind(this),
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
            getMeasure={this._getMeasure()}
            selectMeasure={this._selectMeasure()}
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
              saveMeasure={async () => await this._saveMeasure()}
              deleteMeasures={async () => await this._deleteMeasures()}
              processMeasures={async () => await this._processMeasures()} />
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

  _getMeasure() {
    return async (id) => {
      const measure = await this.props.measureRepository._findById(id);
      this.setState({ measure });
    }
  }

  _selectMeasure() {
    let measureList = this.state.measureList;
    return (measureId, event) => {
      const updatedMeasureList = selectMeasureListItemById(measureId, measureList);
      this.setState({ measureList: updatedMeasureList });
    }
  }
}

export default Reporting;
