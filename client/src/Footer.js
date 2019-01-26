import Button from '@material-ui/core/Button';
import React, { Component } from 'react';

class Footer extends Component {
  render() {
    const { measure, saveMeasure, deleteMeasures, processMeasures } = this.props;

    return <>
      <div className='footer-left-section'>
        <p>{`Last Updated: ${measure.lastUpdated}`}</p>
        <p>{`Minimum System Version: ${measure.measureLogic.minimumSystemVersion}`}</p>
      </div>
      <div className='footer-right-section'>
        <Button variant="contained" color="inherit" onClick={() => saveMeasure()}>Save</Button>
        <Button variant="contained" color="secondary" onClick={() => deleteMeasures()}>Delete</Button>
        <Button variant="contained" color="primary" onClick={() => processMeasures()}>Process</Button>
      </div>
    </>
  }
}

export default Footer
