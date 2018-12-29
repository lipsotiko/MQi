package io.egia.mqi.measure.rules;

import io.egia.mqi.measure.*;
import io.egia.mqi.patient.PatientData;

import java.util.List;

@RuleParams(params={
        @Param(type = "INVISIBLE")
})
public class ExitMeasure implements Rule {
    public MeasureWorkspace evaluate(PatientData patientData,
                                     List<RuleParam> ruleParams,
                                     MeasureMetaData measureMetaData,
                                     MeasureWorkspace measureWorkspace) {
        measureWorkspace.setContinueProcessing(false);
        return measureWorkspace;
    }
}
