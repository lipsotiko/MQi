package io.egia.mqi.measure;

import io.egia.mqi.patient.PatientData;

import java.util.List;

public interface Rule {
    MeasureWorkspace evaluate(PatientData patientData,
                              List<RuleParam> ruleParams,
                              MeasureMetaData measureMetaData,
                              MeasureWorkspace measureWorkspace);
}
