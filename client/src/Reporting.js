import React, { Component } from 'react';
import MeasureList from './MeasureList';
import Footer from './Footer';
import Navigaiton from './Navigation';
import MeasureProgressWsClient from './MeasureProgressWsClient';
import Table from '@material-ui/core/Table';
import TableHead from '@material-ui/core/TableHead';
import TableBody from '@material-ui/core/TableBody';
import TableRow from '@material-ui/core/TableRow';
import TableCell from '@material-ui/core/TableCell';
import { _selectMeasure, _processMeasures } from './Shared'

class Reporting extends Component {

  constructor(props) {
    super(props);
    this.state = {
      measure: null,
      measureSummary: null,
      measureList: [],
    }
  }

  async componentDidMount() {
    const measureList = await this.props.measureRepository._findAllMeasureListItems();
    this.setState({ measureList });
  }


  render() {
    const { measure, measureSummary, measureList } = this.state;

    return (<>
      <Navigaiton currentTab={this.props.currentTab} setTab={this.props.setTab} />
      <form className='content reporting'>
        <aside className='left-aside'>
          <MeasureList
            measuresList={this.state.measureList}
            onClick={this._getMeasureResultSummary()}
            selectMeasure={_selectMeasure(this)}
            displayAddBtn={false}
            selectedMeasureId={this.props.measure ? this.props.measure.measureId : null} />
        </aside>
        <div className='measure center-content' data-testid='reporting'>
          {measureSummary &&
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell className='step-header-2' align="right">Result Code</TableCell>
                  <TableCell className='step-header-1' align="right">Count</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
              {measureSummary.map((item) => {
                return <TableRow key={item.resultCode}>
                  <TableCell className='step-header-2' align="right">{item.resultCode}</TableCell>
                  <TableCell className='step-header-1' align="right">{item.count}</TableCell>
                </TableRow>
              })}
              </TableBody>
            </Table>
          }
          <aside className='right-aside'>
          </aside>
        </div>
        <div className='footer'>
          {measure &&
            <Footer
              measure={measure}
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

  _getMeasureResultSummary() {
    return async (id) => {
      const measure = await this.props.measureRepository._findById(id);
      const measureSummary = await this.props.resultsRepository._summary(id);
      this.setState({ measure, measureSummary });
    }
  }
}

export default Reporting;
