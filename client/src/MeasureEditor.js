import React, { Component } from 'react';
import DraggableList from 'react-draggable-list';
import MeasureList from './MeasureList';
import Step from './Step';
import { compare, distinct, ramdomInt, headers } from './Utilities';
import Button from '@material-ui/core/Button';
import SockJsClient from 'react-stomp';

class MeasureEditor extends Component {

  state = {
    ruleParams: [],
    measuresList: [],
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

  componentDidMount() {
    fetch('/rules_params')
      .then((response) => response.json())
      .then((json) => this.setState({ ruleParams: json }))
      .catch((e) => console.error(e));
    fetch('/measure_list')
      .then((response) => response.json())
      .then((json) => this.setState({ measuresList: json }))
      .catch((e) => console.error(e));
  }

  render() {
    const { measure, ruleParams, measuresList } = this.state;
    console.log(measuresList);
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
          measuresList={measuresList}
          getMeasure={this._getMeasure()}
          selectMeasure={this._selectMeasure()}
          selectedMeasureId={measure ? measure.measureId : null}
          addMeasure={this._addMeasure()} />
        <div className='measure'>
          {measure &&
            <>
              <input className='measure-name' type='text'
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
      <SockJsClient url='http://localhost:8080/ws' topics={['/topic/job']}
        onMessage={(job) => {
          console.log(job);
          let measuresList = this.state.measuresList;
          measuresList.map(measureListItem => {
            if (job.measureIds.includes(measureListItem.measureId)) {
              measureListItem.progress = job.progress;
              measureListItem.jobStatus = job.jobStatus;
              measureListItem.jobLastUpdated = job.lastUpdated;
            }
            return measureListItem;
          });

          this.setState({ measuresList });
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
      await fetch(`/measure?measureId=${id}`)
        .then((response) => response.json())
        .then((json) => this._formatMeasureJson(json))
        .then((json) => this.setState({ measure: json }))
        .catch((e) => console.error(e));
    }
  }

  _selectMeasure() {
    return (id, event) => {
      let measuresList = this.state.measuresList;

      if (event.shiftKey || event.ctrlKey) {
        measuresList.map(m => this._selectMeasureById(m, id))
      } else {
        measuresList.map(m => m.selected = false)
        measuresList.map(m => this._selectMeasureById(m, id))
      }

      this.setState({ measuresList });
    }
  }

  _selectMeasureById(m, id) {
    if (m.measureId === id) {
      m.selected = true;
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
      let measure = await this._fetchMeasure('/measure', 'PUT', JSON.stringify(DEFAULT_NEW_MEASURE));
      let { measuresList } = this.state;
      measuresList.push({
        measureId: measure.measureId,
        measureName: measure.measureName,
        jobStatus: 'IDLE'
      });
      measuresList.sort(compare);
      this.setState({ measure, measuresList });
    }
  }

  async _saveMeasure() {
    let measure = await this._fetchMeasure('/measure', 'PUT', JSON.stringify(this.state.measure));
    let measuresList = await fetch('/measure_list')
      .then((response) => response.json())
      .catch((e) => console.error(e));

    this.setState({ measure, measuresList })
    return measure;
  }

  async _fetchMeasure(url, method, body) {
    return await fetch(url, { headers, method, body })
      .then((response) => response.json())
      .then((json) => this._formatMeasureJson(json))
      .catch((e) => console.error(e));
  }

  _formatMeasureJson(json) {
    if (json.measureLogic.steps) {
      json.measureLogic.steps.map(step => {
        step.id = ramdomInt();
        return step;
      });
      return json;
    }
    return json;
  }

  async _processMeasures() {
    let selectedMeasureIds = this._getSelectedMeasures();

    await fetch(`/process`, { headers, method: 'POST', body: JSON.stringify(selectedMeasureIds) })
      .catch((e) => console.error(e));
    let measuresList = this.state.measuresList.map(m => {
      if (selectedMeasureIds.includes(m.measureId)) {
        m.jobStatus = "RUNNING";
        m.selected = false;
        return m;
      };
      return m;
    });
    this.setState({ measuresList, measure: null });
  }

  async _deleteMeasures() {
    let selectedMeasureIds = this._getSelectedMeasures();

    await fetch(`/measure`, { headers, method: 'DELETE', body: JSON.stringify(selectedMeasureIds) })
      .catch((e) => console.error(e));
    let measuresList = this.state.measuresList.filter(m => !selectedMeasureIds.includes(m.measureId));
    this.setState({ measuresList, measure: null });
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
    this.state.measuresList.forEach(measureListItem => {
      if (measureListItem.selected) {
        selectedMeasureIds.push(measureListItem.measureId);
      }
    });
    return selectedMeasureIds;
  }
}

export default MeasureEditor
