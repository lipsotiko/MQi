import React, { Component } from 'react';
import SockJsClient from 'react-stomp';

class MeasureProgressWsClient extends Component {
  render() {
    if(process.env.TEST) return <></>

    return <SockJsClient url={'/ws'} topics={['/topic/job']}
    onMessage={(job) => {
      console.log(job);
      let measureList = this.props.measureList;
      measureList.map(measureListItem => {
        if (job.measureIds.includes(measureListItem.measureId) &&
          !(job.jobStatus === 'RUNNING' && measureListItem.jobStatus !== 'RUNNING')
        ) {
          measureListItem.progress = job.progress;
          measureListItem.jobStatus = job.jobStatus;
          measureListItem.jobLastUpdated = job.lastUpdated;
        }
        return measureListItem;
      });

      this.props.updateMeasureList(measureList);
    }} />
  }
}

export default MeasureProgressWsClient
