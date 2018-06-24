package io.egia.mqi.rules;

import io.egia.mqi.measure.MeasureResult;
import io.egia.mqi.measure.Param;
import io.egia.mqi.measure.Rule;
import io.egia.mqi.patient.PatientData;

@Rule(params={
        @Param(name="CONTINUE", type = "BOOLEAN")
})
public class ExitMeasure {
    public MeasureResult evaluate(PatientData patientData, MeasureResult measureResult) {
        measureResult.setContinueProcessing(false);
        return measureResult;
    }
}
