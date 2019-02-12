import React, { Component } from 'react';
import DraggableList from 'react-draggable-list';
import MeasureList from './MeasureList';
import Footer from './Footer';
import Step from './Step';
import { compare, distinct, ramdomInt } from './Utilities';
import { _selectMeasure, _deleteMeasures, _processMeasures } from './Shared'
import Button from '@material-ui/core/Button';
import Table from '@material-ui/core/Table';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import TableCell from '@material-ui/core/TableCell';
import TextField from '@material-ui/core/TextField';
import Navigaiton from './Navigation';
import MeasureProgressWsClient from './MeasureProgressWsClient';

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
    _saveMeasure: this._saveMeasure.bind(this),
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
      <Navigaiton
        currentTab={this.props.currentTab}
        setTab={this.props.setTab}
        rightButtons={
          <>
            {measure && <Button
              color="inherit" className='add-step'
              onClick={async () => await this._addStep(rules)}>+ Step</Button>}
            <Button color="inherit"
              onClick={this._addMeasure()}>+ Measure</Button>
          </>
        }
      />
      <form className='content measure-editor'>
        <aside className='left-aside'>
          <MeasureList
            measuresList={measureList}
            onClick={this._getMeasure()}
            selectMeasure={_selectMeasure(this)}
            selectedMeasureId={measure ? measure.measureId : null}
            addMeasure={this._addMeasure()} />
        </aside>
        <div className='measure center-content' data-testid='measure'>
          {measure &&
            <>
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell className='step-header-1' align="right">Step ID</TableCell>
                    <TableCell className='step-header-2' align="right">Rule</TableCell>
                    <TableCell className='step-header-1' align="right">Success Step ID</TableCell>
                    <TableCell className='step-header-1' align="right">Failure Step ID</TableCell>
                  </TableRow>
                </TableHead>
              </Table>
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
        {measure &&
          <aside className='right-aside'>
            <TextField
              label="Measure Name"
              data-testid='measure-name'
              value={measure.measureName}
              onChange={(e) => this._changeName(e)}
              margin="normal" />
            <TextField
              label="Description"
              multiline
              rowsMax="20"
              value={measure.measureLogic.description}
              onChange={(e) => this._changeDescription(e)}
              margin="normal"
              variant="outlined"
            />
          </aside>}
        <div className='footer'>
          {measure &&
            <Footer
              measure={measure}
              saveMeasure={async () => await this._saveMeasure()}
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

  _getUniqueRuleNames(ruleParams) {
    let ruleNames = ['(select)'];
    ruleParams.forEach(ruleParam => ruleNames.push(ruleParam.ruleName));
    return distinct(ruleNames).sort()
  }

  _addMeasure() {
    const DEFAULT_NEW_MEASURE = {
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

  _deleteStep() {
    return (id) => {
      let steps = this.state.measure.measureLogic.steps.filter(s => s.id !== id);
      let measure = this.state.measure;
      measure.measureLogic.steps = steps;
      this.setState({ measure });
    }
  }
}

export default MeasureEditor
