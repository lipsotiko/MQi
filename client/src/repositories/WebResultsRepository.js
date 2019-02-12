export class ResultsRepository {

  async _summary(measureId) {
    return fetch(`/results_summary?measureId=${measureId}`)    
      .then((response) => response.json())
      .catch((e) => console.error(e));
  }

  async _detail(measureId) {
    return fetch(`/results_detail?measureId=${measureId}`) 
      .then((response) => response.json())
      .catch((e) => console.error(e));
  }

  async _ruleTrace(measureId) {
    return fetch(`/rule_trace?measureId=${measureId}`) 
      .then((response) => response.json())
      .catch((e) => console.error(e));
  }
}
