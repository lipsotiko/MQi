package io.egia.mqi.rules;

import io.egia.mqi.measure.MeasureResult;
import io.egia.mqi.measure.Param;
import io.egia.mqi.measure.Rule;
import io.egia.mqi.patient.PatientData;

@Rule(params={
        @Param(name="RESULT_CODE", type = "TEXT")
})
public class SetResultCode {
    public MeasureResult evaluate(PatientData patientData, MeasureResult measureResult) {
        measureResult.setResults(true);
        return measureResult;
    }
}
