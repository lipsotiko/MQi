package io.egia.mqi.measure.rules;

import io.egia.mqi.measure.*;
import io.egia.mqi.patient.PatientData;

import java.util.List;

@RuleParameters(params={
        @Param(name="RESULT_CODE", type = "TEXT")
})
public class SetResultCode implements Rule {
    public MeasureResult evaluate(PatientData patientData,
                                  List<RuleParam> ruleParams,
                                  MeasureMetaData measureMetaData,
                                  MeasureResult measureResult) {
        measureResult.setResults(true);
        return measureResult;
    }
}
