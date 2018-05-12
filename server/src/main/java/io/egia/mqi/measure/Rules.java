package io.egia.mqi.measure;

import io.egia.mqi.patient.PatientData;

public class Rules {

    public MeasureResults age_x_asOf_thru(PatientData patientData, MeasureResults measureResults) {
        return measureResults;
    }

    public MeasureResults setDenominatorResult(PatientData patientData, MeasureResults measureResults) {
        measureResults.setResults(true);
        return measureResults;
    }

    public MeasureResults exitMeasure(PatientData patientData, MeasureResults measureResults) {
        measureResults.setContinueProcessing(false);
        return measureResults;
    }
}
