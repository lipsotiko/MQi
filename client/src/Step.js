import React, { Component } from 'react';
import cx from 'classnames';
import DeleteTwoToneIcon from '@material-ui/icons/DeleteTwoTone';

class Step extends Component {


  constructor(props) {
    super(props)
    this.changeRule = this.changeRule.bind(this);
    this.changeId = this.changeId.bind(this);
    this.changeParam = this.changeParam.bind(this);
  }

  render() {
    const { step, rules, deleteStep } = this.props.item;
    const { itemSelected, dragHandle } = this.props;
    const scale = itemSelected * 0.05 + 1;
    const shadow = itemSelected * 15 + 1;
    const dragged = itemSelected !== 0;
    return <div
      className={cx('step', { dragged })}
      style={{
        transform: `scale(${scale})`,
        boxShadow: `rgba(0, 0, 0, 0.3) 0px ${shadow}px ${2 * shadow}px 0px`
      }}
    >
      {dragHandle(<div className="dragHandle" />)}
      <input type='text' defaultValue={step.stepId} onChange={(e) => { this.changeId(e, "stepId") }} />
      <select value={step.ruleName} onChange={(e) => this.changeRule(e)}>
        {rules.map((ruleName) => <option key={ruleName}> {ruleName} </option>)}
      </select>
      <input type='text' defaultValue={step.successStepId} onChange={(e) => { this.changeId(e, "successStepId") }} />
      <input type='text' defaultValue={step.failureStepId} onChange={(e) => { this.changeId(e, "failureStepId") }} />
      <DeleteTwoToneIcon className='delete-step-icon' onClick={() => { deleteStep(step.id) }} />
      <div className='parameters'>
        {step.parameters.map((param, index) => {
          return (<div key={param.ruleParamId} className='param-editor'>
            <div className='param-name'>{param.paramName}</div>
            <input
              className='param-value'
              type='text' defaultValue={param.paramValue} placeholder={this._getPlaceHolder(param)}
              onChange={(e) => this.changeParam(e, index)} />
          </div>)
        })}
      </div>
    </div>
  }

  changeRule(e) {
    let { step, ruleParams, onChangeStep } = this.props.item;
    step.ruleName = e.target.value;
    step.parameters = ruleParams.filter(ruleParam => ruleParam.paramName && ruleParam.ruleName === e.target.value);
    onChangeStep(step);
  }

  changeId(e, field) {
    let { step, onChangeStep } = this.props.item;
    step[field] = parseInt(e.target.value);
    onChangeStep(step);
  }

  changeParam(e, index) {
    let { step, onChangeStep } = this.props.item;
    step.parameters[index].paramValue = e.target.value;
    onChangeStep(step);
  }

  getDragHeight() {
    const DRAG_HEIGHT = 30;
    return DRAG_HEIGHT;
  }

  _getPlaceHolder(param) {
    if(param.paramType === 'DATE') return 'YYYYMMDD'
    if(param.paramType === 'INTEGER') return '###'
  }

}

export default Step;
