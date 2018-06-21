package io.egia.mqi.rules;

import io.egia.mqi.Param;
import io.egia.mqi.Rule;
import io.egia.mqi.measure.MeasureResult;
import io.egia.mqi.patient.PatientData;

@Rule(params={
        @Param(name="AGE", type = "INTEGER")
        , @Param(name="START_DATE", type = "DATE")
        , @Param(name="END_DATE", type = "DATE")
})
public class AgeWithinDateRange {
    public MeasureResult evaluate(PatientData patientData, MeasureResult measureResult) {
        return measureResult;
    }
}
