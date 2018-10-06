package io.egia.mqi.rules;

import io.egia.mqi.measure.MeasureResult;
import io.egia.mqi.measure.Param;
import io.egia.mqi.measure.Rule;
import io.egia.mqi.measure.RuleParam;
import io.egia.mqi.patient.PatientData;

import java.util.List;

@Rule(params={
        @Param(name="RESULT_CODE", type = "TEXT")
})
public class SetResultCode {
    public MeasureResult evaluate(PatientData patientData, List<RuleParam> ruleParams, MeasureResult measureResult) {
        measureResult.setResults(true);
        return measureResult;
    }
}
