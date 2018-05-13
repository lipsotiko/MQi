package io.egia.mqi.measure;

import io.egia.mqi.patient.PatientData;

public class Rules {

    public MeasureResult age_x_asOf_thru(PatientData patientData, MeasureResult measureResult) {
        return measureResult;
    }

    public MeasureResult setDenominatorResult(PatientData patientData, MeasureResult measureResult) {
        measureResult.setResults(true);
        return measureResult;
    }

    public MeasureResult exitMeasure(PatientData patientData, MeasureResult measureResult) {
        measureResult.setContinueProcessing(false);
        return measureResult;
    }
}
