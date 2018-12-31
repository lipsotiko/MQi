import React, { Component } from 'react';
import DraggableList from 'react-draggable-list';
import MeasureList from './MeasureList';
import Step from './Step';
import { compare, distinct, ramdomInt } from './Utilities';
import Button from '@material-ui/core/Button';
import SockJsClient from 'react-stomp';

class MeasureEditor extends Component {

  state = {
    ruleParams: [],
    measureList: [],
    measure: null,
    _changeStep: this._changeStep.bind(this),
    _changeName: this._changeName.bind(this),
    _changeDescription: this._changeDescription.bind(this),
    _stepReorder: this._stepReorder.bind(this),
    _addMeasure: this._addMeasure.bind(this),
    _deleteMeasures: this._deleteMeasures.bind(this),
    _saveMeasure: this._saveMeasure.bind(this),
    _selectMeasure: this._selectMeasure.bind(this),
    _processMeasures: this._processMeasures.bind(this),
  }

  async componentDidMount() {
    const ruleParams = await this.props.measureRepository._findAllRuleParams();
    const measureList = await this.props.measureRepository._findAllMeasureListItems();
    this.setState({ ruleParams, measureList });
  }

  render() {
    const { measure, ruleParams, measureList } = this.state;
    const rules = this._getUniqueRuleNames(ruleParams);
    let hasSteps = measure && measure.measureLogic && measure.measureLogic.steps
    let steps;

    if (hasSteps) {
      steps = measure.measureLogic.steps.map((step, index) => {
        return {
          step,
          rules,
          index,
          ruleParams,
          key: step.id,
          onChangeStep: this._changeStep(),
          deleteStep: this._deleteStep(),
        }
      });
    }

    return <>
      <form className='content measure-editor'>
        <MeasureList
          measuresList={measureList}
          getMeasure={this._getMeasure()}
          selectMeasure={this._selectMeasure()}
          selectedMeasureId={measure ? measure.measureId : null}
          addMeasure={this._addMeasure()} />
        <div className='measure' data-testid='measure'>
          {measure &&
            <>
              <input className='measure-name' type='text' data-testid='measure-name'
                value={measure.measureName}
                onChange={(e) => this._changeName(e)} />
              <textarea className='measure-description'
                value={measure.measureLogic.description}
                onChange={(e) => this._changeDescription(e)} />
              <Button onClick={async () => { await this._addStep(rules) }}>+ Step</Button>
            </>}
          <div>
            {hasSteps &&
              <DraggableList
                itemKey="key"
                template={Step}
                list={steps}
                onMoveEnd={(newList, movedItem, oldIndex, newIndex) => {
                  this._stepReorder(newList, movedItem, oldIndex, newIndex);
                }}
                container={() => document.body}
              />}
          </div>
        </div>
        <div className='footer'>
          {measure &&
            <>
              <div className='footer-left-section'>
                <p>{`Last Updated: ${measure.lastUpdated}`}</p>
                <p>{`Minimum System Version: ${measure.measureLogic.minimumSystemVersion}`}</p>
              </div>
              <div className='footer-right-section'>
                <Button variant="contained" onClick={() => this._saveMeasure()}>
                  Save
              </Button>
                <Button variant="contained" color="secondary" onClick={() => this._deleteMeasures()}>
                  Delete
              </Button>
                <Button variant="contained" color="primary" onClick={() => this._processMeasures()}>
                  Process
              </Button>
              </div>
            </>}
        </div>
      </form>
      <SockJsClient url={process.env.REACT_APP_WS_URL} topics={['/topic/job']}
        onMessage={(job) => {
          console.log(job);
          let measureList = this.state.measureList;
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

          this.setState({ measureList });
        }} />
    </>
  }

  _changeName(e) {
    let measure = this.state.measure;
    measure.measureName = e.target.value;
    this.setState({ measure });
  }

  _changeDescription(e) {
    let measure = this.state.measure;
    measure.measureLogic.description = e.target.value;
    this.setState({ measure });
  }

  _changeStep() {
    return (step) => {
      let { measure } = this.state;
      measure.measureLogic.steps[step.index] = step;
      this.setState({ measure });
      this.forceUpdate();
    }
  }

  _stepReorder(newList, movedItem, oldIndex, newIndex) {
    let { measure } = this.state;
    measure.measureLogic.steps = newList.map((item) => JSON.parse(JSON.stringify(item.step)));
    this.setState({ measure });
  }

  _addStep(rules) {
    let measure = this.state.measure;
    if (!measure.measureLogic.steps) {
      measure.measureLogic.steps = [];
    }

    measure.measureLogic.steps.push({
      rules,
      id: ramdomInt(),
      ruleName: "(select)",
      parameters: [],
      stepId: 0,
      successStepId: 0,
      failureStepId: 0
    })
    this.setState({ measure });
  }

  _getMeasure() {
    return async (id) => {
      const measure = await this.props.measureRepository._findById(id);
      this.setState({ measure });
    }
  }

  _selectMeasure() {
    return (measureId, event) => {
      let measureList = this.state.measureList;

      if (event.shiftKey || event.ctrlKey) {
        measureList.map(measureListItem => {
          if (measureListItem.measureId === measureId) {
            measureListItem.selected = true;
            return measureListItem;            
          }
          return measureListItem;
        })
      } else {
        measureList.map(measureListItem => measureListItem.selected = false)
        measureList.map(measureListItem => {
          if (measureListItem.measureId === measureId) {
            measureListItem.selected = true;
            return measureListItem;
          }
          return measureListItem;
        })
      }

      this.setState({ measureList });
    }
  }

  _getUniqueRuleNames(ruleParams) {
    let ruleNames = ['(select)'];
    ruleParams.forEach(ruleParam => ruleNames.push(ruleParam.ruleName));
    return distinct(ruleNames).sort()
  }

  _addMeasure() {
    const DEFAULT_NEW_MEASURE = {
      measureId: -1,
      measureName: 'New Measure',
      measureLogic: {
        description: 'Describe...'
      }
    }

    return async () => {
      let measure = await this.props.measureRepository._saveMeasure(DEFAULT_NEW_MEASURE);
      let { measureList } = this.state;
      measureList.push({
        measureId: measure.measureId,
        measureName: measure.measureName,
        jobStatus: 'IDLE'
      });
      measureList.sort(compare);
      this.setState({ measure, measureList });
    }
  }

  async _saveMeasure() {
    let measure = await this.props.measureRepository._saveMeasure(this.state.measure);
    let measureList = await this.props.measureRepository._findAllMeasureListItems();
    measureList.map(measureListItem => {
      if (measureListItem.measureId === measure.measureId) {
        measureListItem.selected = true;
        return measureListItem;
      }
      return measureListItem;
    });
    this.setState({ measure, measureList })
    return measure;
  }

  async _processMeasures() {
    let selectedMeasureIds = this._getSelectedMeasures();
    await this.props.measureRepository._processMeasures(selectedMeasureIds);
    let measureList = this.state.measureList.map(m => {
      if (selectedMeasureIds.includes(m.measureId)) {
        m.jobStatus = "RUNNING";
        m.selected = false;
        return m;
      };
      return m;
    });
    this.setState({ measureList, measure: null });
  }

  async _deleteMeasures() {
    let selectedMeasureIds = this._getSelectedMeasures();
    await this.props.measureRepository._deleteMeasures(selectedMeasureIds);
    let measureList = this.state.measureList.filter(m => !selectedMeasureIds.includes(m.measureId));
    this.setState({ measureList, measure: null });
  }

  _deleteStep() {
    return (id) => {
      let steps = this.state.measure.measureLogic.steps.filter(s => s.id !== id);
      let measure = this.state.measure;
      measure.measureLogic.steps = steps;
      this.setState({ measure });
    }
  }

  _getSelectedMeasures() {
    let selectedMeasureIds = [];
    this.state.measureList.forEach(measureListItem => {
      if (measureListItem.selected) {
        selectedMeasureIds.push(measureListItem.measureId);
      }
    });
    return selectedMeasureIds;
  }
}

export default MeasureEditor
