package io.egia.mqi.rules;

import io.egia.mqi.measure.MeasureResult;
import io.egia.mqi.measure.Param;
import io.egia.mqi.measure.Rule;
import io.egia.mqi.measure.RuleParam;
import io.egia.mqi.patient.PatientData;

import java.util.List;

@Rule(params={
        @Param(name="", type = "INVISIBLE")
})
public class ExitMeasure {
    public MeasureResult evaluate(PatientData patientData, List<RuleParam> ruleParams, MeasureResult measureResult) {
        measureResult.setContinueProcessing(false);
        return measureResult;
    }
}
