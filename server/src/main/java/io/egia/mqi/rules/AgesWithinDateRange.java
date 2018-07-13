package io.egia.mqi.rules;

import io.egia.mqi.measure.MeasureResult;
import io.egia.mqi.measure.Param;
import io.egia.mqi.measure.Rule;
import io.egia.mqi.patient.PatientData;

@Rule(params={
        @Param(name="FROM_AGE", type = "INTEGER")
        , @Param(name="TO_AGE", type = "INTEGER")
        , @Param(name="START_DATE", type = "DATE")
        , @Param(name="END_DATE", type = "DATE")
})
public class AgesWithinDateRange {
    public MeasureResult evaluate(PatientData patientData, MeasureResult measureResult) {
        return measureResult;
    }
}
