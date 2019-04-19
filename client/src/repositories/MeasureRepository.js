import { headers, ramdomInt } from '../Utilities';

export class MeasureRepository {

  async _deleteMeasures(measureIds) {
    fetch(`/measure`, { headers, method: 'DELETE', body: JSON.stringify(measureIds) })
      .catch((e) => console.error(e));
  }

  async _findAllRuleParams() {
    return fetch('/rules_params')
      .then((response) => response.json())
      .catch((e) => console.error(e));
  }

  async _findAllMeasureListItems() {
    return fetch('/measure_list')
      .then((response) => response.json())
      .catch((e) => console.error(e));
  }

  async _processMeasures(measureIds) {
    return fetch(`/process`, { headers, method: 'POST', body: JSON.stringify(measureIds) })
      .catch((e) => console.error(e));
  }

  async _saveMeasure(body) {
    return await fetch('/measure', { headers, method: 'PUT', body: JSON.stringify(body) })
      .then((response) => response.json())
      .then((json) => this._formatMeasureJson(json))
      .catch((e) => console.error(e));
  }

  async _findById(id) {
    return await fetch(`/measure?measureId=${id}`)
      .then((response) => response.json())
      .then((json) => this._formatMeasureJson(json))
      .catch((e) => console.error(e));
  }

  _formatMeasureJson(json) {
    if (json.measureLogic.steps) {
      json.measureLogic.steps.map(step => {
        step.id = ramdomInt();
        if(step.parameters) {
          step.parameters.map(param => {
            if(!param.ruleParamId) {
              param.ruleParamId = ramdomInt();
            }
            return param;
          });
        }
        return step;
      });
      return json;
    }
    return json;
  }
}
