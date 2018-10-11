package io.egia.mqi.measure.rules;

import io.egia.mqi.measure.*;
import io.egia.mqi.patient.PatientData;

import java.util.List;

import static io.egia.mqi.measure.helpers.ParamHelper.getText;

@RuleParams(params={
        @Param(name="RESULT_CODE", type = "TEXT")
})
public class SetResultCode implements Rule {
    public MeasureWorkspace evaluate(PatientData patientData,
                                     List<RuleParam> ruleParams,
                                     MeasureMetaData measureMetaData,
                                     MeasureWorkspace measureWorkspace) {
        String resultCode = getText(ruleParams, "RESULT_CODE");
        measureWorkspace.setResultCode(resultCode);
        return measureWorkspace;
    }
}
