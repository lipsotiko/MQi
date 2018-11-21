import React, { Component } from 'react';
import Button from '@material-ui/core/Button';
import CircularProgress from '@material-ui/core/CircularProgress';

class MeasureList extends Component {

  render() {
    const { measuresList } = this.props;

    console.log(measuresList);

    return <div className='measures-list'>
      <Button onClick={async () => await this.props.addMeasure()}>+ Measure</Button>
      {measuresList && measuresList.sort().map((measureItem) => {
        const { measureId, measureName, selected, jobStatus } = measureItem;

        return <li
          key={measureId}
          className={selected ? 'selected' : ''}
          onClick={(e) => {
            this.props.getMeasure(measureId);
            this.props.selectMeasure(measureId, e);
          }}>
          <span>{measureName}</span>
          {measureItem.progress === 0 &&
            <CircularProgress
              className={`circular-progress ${jobStatus ? jobStatus : 'IDLE'}`}
            />
          }
          {measureItem.progress !== 0 &&
            <CircularProgress
              className={`circular-progress ${jobStatus ? jobStatus : 'IDLE'}`}
              variant="determinate"
              value={jobStatus === 'RUNNING' ? measureItem.progress : 100}
            />}
        </li>
      })}
    </div>
  }
}

export default MeasureList
