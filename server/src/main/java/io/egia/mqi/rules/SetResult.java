package io.egia.mqi.rules;

import io.egia.mqi.measure.MeasureResult;
import io.egia.mqi.patient.PatientData;

public class SetResult {
    public MeasureResult evaluate(PatientData patientData, MeasureResult measureResult) {
        measureResult.setResults(true);
        return measureResult;
    }
}