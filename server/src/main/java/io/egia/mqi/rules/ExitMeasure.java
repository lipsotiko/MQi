package io.egia.mqi.rules;

import io.egia.mqi.measure.MeasureResult;
import io.egia.mqi.patient.PatientData;

public class ExitMeasure {
    public MeasureResult evaluate(PatientData patientData, MeasureResult measureResult) {
        measureResult.setContinueProcessing(false);
        return measureResult;
    }
}
