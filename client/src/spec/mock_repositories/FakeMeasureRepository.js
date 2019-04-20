import { ramdomInt } from '../../Utilities';

export class MeasureRepository {

  constructor(spy) {
    this.spy = spy;
  }

  async _deleteMeasures(measureIds) {
    return Promise.resolve(() => { });
  }

  _findAllRuleParams() {
    return [
      {
        ruleParamId: 1,
        paramName: "PARAM_A",
        paramType: "INTEGER",
        paramValue: 1,
        ruleName: "AgeWithinDateRange"
      }, {
        ruleParamId: 2,
        paramName: "PARAM_B",
        paramType: "DATE",
        paramValue: 1,
        ruleName: "AgeWithinDateRange"
      }, {
        ruleParamId: 3,
        paramName: "PARAM_C",
        paramType: "TEXT",
        paramValue: "COOL",
        ruleName: "AgeWithinDateRange"
      }
    ]
  }

  _findAllMeasureListItems() {
    return [
      {
        measureId: 1,
        measureName: "sample measure 1",
        jobStatus: "DONE",
        jobLastUpdated: "2018-12-25T15:08:11.831+0000",
        measureLastUpdated: "2018-12-25T15:08:11.831+0000"
      }, {
        measureId: 2,
        measureName: "sample measure 2",
        jobStatus: null,
        jobLastUpdated: null,
        measureLastUpdated: null
      }
    ]
  }

  async _processMeasures(measureIds) {
    return Promise.resolve(() => { });
  }

  async _saveMeasure(body) {
    this.spy(body);
    return Promise.resolve(body);
  }

  async _findById(id) {
    return Promise.resolve(
      {
        measureId: id,
        measureName: 'sample measure 1',
        lastUpdated: '06-12-2018 11:25:55PM EST',
        measureLogic: {
          description: 'Patients that are two years of age on the first day of the reporting year with an occurrence of a broken leg at some time during the reporting year',
          minimumSystemVersion: '1.0.0',
          steps: [
            {
              id: 1,
              stepId: 100,
              ruleName: 'AgeWithinDateRange',
              parameters: [
                {
                  ruleParamId: 1,
                  ruleName: null,
                  paramName: 'FROM_AGE',
                  paramType: 'INTEGER',
                  paramValue: 28
                }, {
                  ruleParamId: 2,
                  ruleName: null,
                  paramName: 'TO_AGE',
                  paramType: 'INTEGER',
                  paramValue: 32
                }, {
                  ruleParamId: 3,
                  ruleName: null,
                  paramName: 'START_DATE',
                  paramType: 'DATE',
                  paramValue: 19880428
                }, {
                  ruleParamId: 4,
                  ruleName: null,
                  paramName: 'END_DATE',
                  paramType: 'DATE',
                  paramValue: 19880428
                }
              ],
              successStepId: 200,
              failureStepId: 99999
            },
            {
              id: 2,
              stepId: 200,
              ruleName: 'SetResultCode',
              parameters: [
                {
                  ruleParamId: null,
                  ruleName: null,
                  paramName: 'RESULT_CODE',
                  paramType: 'TEXT',
                  paramValue: 'DENOMINATOR'
                }
              ],
              successStepId: 99999,
              failureStepId: 99999
            }
          ]
        }
      }
    )
  }
}
