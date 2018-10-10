package io.egia.mqi.measure.rules;

import io.egia.mqi.measure.*;
import io.egia.mqi.patient.PatientData;
import io.egia.mqi.visit.CodeSet;
import io.egia.mqi.visit.Visit;
import io.egia.mqi.visit.VisitCode;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RuleParameters(params = {
        @Param(name = "CODE_SET", type = "TEXT")
})
public class HasCodeSet implements Rule {
    public MeasureResult evaluate(PatientData patientData,
                                  List<RuleParam> ruleParams,
                                  MeasureMetaData measureMetaData,
                                  MeasureResult measureResult) {

        if (measureMetaData == null || measureMetaData.getCodeSets() == null) {
            measureResult.setContinueProcessing(false);
            return measureResult;
        }

        String codeSet = getText(ruleParams, "CODE_SET");

        List<CodeSet> codeSets = measureMetaData.getCodeSets().stream()
                .filter(cs -> cs.getCodeSetGroup().getGroupName().equals(codeSet)).collect(Collectors.toList());

        for(Visit v: patientData.getVisits()) {
            for(VisitCode vc: v.getVisitCodes()) {
                for(CodeSet cs: codeSets) {
                    if (vc.getCodeSystem().equals(cs.getCodeSystem()) &&
                            vc.getCodeValue().equals(cs.getCodeValue())) {
                        return measureResult;
                    }
                }
            }
        }

        measureResult.setContinueProcessing(false);
        return measureResult;
    }

    private String getText(List<RuleParam> ruleParams, String param) {
        RuleParam intParam = ruleParams.stream().filter(ruleParam ->
                ruleParam.getParamName().equals(param)).collect(Collectors.toList()).get(0);
        return intParam.getParamValue();
    }

}
