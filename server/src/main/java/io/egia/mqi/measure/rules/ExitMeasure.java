package io.egia.mqi.measure.rules;

import io.egia.mqi.measure.*;
import io.egia.mqi.patient.PatientData;

import java.util.List;

@RuleParameters(params={
        @Param(type = "INVISIBLE")
})
public class ExitMeasure implements Rule {
    public MeasureResult evaluate(PatientData patientData,
                                  List<RuleParam> ruleParams,
                                  MeasureMetaData measureMetaData,
                                  MeasureResult measureResult) {
        measureResult.setContinueProcessing(false);
        return measureResult;
    }
}
