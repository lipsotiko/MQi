package io.egia.mqi.measure.rules;

import io.egia.mqi.RuleTest;
import io.egia.mqi.measure.MeasureMetaData;
import io.egia.mqi.measure.MeasureWorkspace;
import io.egia.mqi.measure.RuleParam;
import io.egia.mqi.patient.PatientData;
import io.egia.mqi.visit.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RuleTest
public class HasCodeSetTest {

    private HasCodeSet hasCodeSet;
    private List<RuleParam> ruleParams = new ArrayList<>();
    private MeasureWorkspace measureWorkspace;
    private List<CodeSet> codeSets = new ArrayList<>();

    @Before
    public void setUp() {
        hasCodeSet = new HasCodeSet();

        RuleParam ruleParam1 = new RuleParam("", "CODE_SET", "TEXT");
        ruleParam1.setParamValue("CODE_SET_A");
        ruleParams.add(ruleParam1);

        CodeSetGroup codeSetGroupA = CodeSetGroup.builder().groupName("CODE_SET_A").build();
        CodeSet codeSetA = CodeSet.builder().codeSetGroup(codeSetGroupA).codeSystem(CodeSystem.ICD_10).codeValue("123").build();
        codeSets.add(codeSetA);

        CodeSetGroup codeSetGroupB = CodeSetGroup.builder().groupName("CODE_SET_B").build();
        CodeSet codeSetB = CodeSet.builder().codeSetGroup(codeSetGroupB).codeSystem(CodeSystem.ICD_10).codeValue("789").build();
        codeSets.add(codeSetB);

        measureWorkspace = new MeasureWorkspace();
    }

    @Test
    public void patientHasCodeSetA() {
        PatientData patientData = getPatientData("123", CodeSystem.ICD_10);
        hasCodeSet.evaluate(patientData, ruleParams, new MeasureMetaData(codeSets), measureWorkspace);
        assertThat(measureWorkspace.getContinueProcessing()).isEqualTo(true);
    }

    @Test
    public void patientDoesNotHaveCodeSetA() {
        PatientData patientData = getPatientData("789", CodeSystem.ICD_10);
        hasCodeSet.evaluate(patientData, ruleParams, new MeasureMetaData(codeSets), measureWorkspace);
        assertThat(measureWorkspace.getContinueProcessing()).isEqualTo(false);
    }

    private PatientData getPatientData(String codeValue, CodeSystem codeSystem) {
        VisitCode visitCode = new VisitCode();
        visitCode.setCodeValue(codeValue);
        visitCode.setCodeSystem(codeSystem);

        Visit visit = new Visit();
        visit.setVisitCodes(Collections.singletonList(visitCode));

        PatientData patientData = new PatientData(1L);
        patientData.addPatientRecord(visit);
        return patientData;
    }
}
