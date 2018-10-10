package io.egia.mqi.measure;

import io.egia.mqi.measure.MeasureMetaData;
import io.egia.mqi.measure.MeasureResult;
import io.egia.mqi.measure.RuleParam;
import io.egia.mqi.patient.PatientData;

import java.util.List;

public interface Rule {
    MeasureResult evaluate(PatientData patientData,
                           List<RuleParam> ruleParams,
                           MeasureMetaData measureMetaData,
                           MeasureResult measureResult);
}
