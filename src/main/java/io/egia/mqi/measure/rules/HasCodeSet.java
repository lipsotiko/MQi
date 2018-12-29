package io.egia.mqi.measure.rules;

import io.egia.mqi.measure.*;
import io.egia.mqi.patient.PatientData;
import io.egia.mqi.visit.CodeSet;
import io.egia.mqi.visit.Visit;
import io.egia.mqi.visit.VisitCode;

import java.util.List;
import java.util.stream.Collectors;

import static io.egia.mqi.measure.helpers.ParamHelper.getText;

@RuleParams(params = {
        @Param(name = "CODE_SET", type = "TEXT")
})
public class HasCodeSet implements Rule {
    public MeasureWorkspace evaluate(PatientData patientData,
                                     List<RuleParam> ruleParams,
                                     MeasureMetaData measureMetaData,
                                     MeasureWorkspace measureWorkspace) {

        if (measureMetaData == null || measureMetaData.getCodeSets() == null) {
            measureWorkspace.setContinueProcessing(false);
            return measureWorkspace;
        }

        String codeSet = getText(ruleParams, "CODE_SET");

        List<CodeSet> codeSets = measureMetaData.getCodeSets().stream()
                .filter(cs -> cs.getCodeSetGroup().getGroupName().equals(codeSet)).collect(Collectors.toList());

        for(Visit v: patientData.getVisits()) {
            for(VisitCode vc: v.getVisitCodes()) {
                for(CodeSet cs: codeSets) {
                    if (vc.getCodeSystem().equals(cs.getCodeSystem()) &&
                            vc.getCodeValue().equals(cs.getCodeValue())) {
                        return measureWorkspace;
                    }
                }
            }
        }

        measureWorkspace.setContinueProcessing(false);
        return measureWorkspace;
    }

}
