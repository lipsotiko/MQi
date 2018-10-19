package io.egia.mqi.measure;

import io.egia.mqi.helpers.Helpers;
import io.egia.mqi.patient.Patient;
import io.egia.mqi.patient.PatientData;
import io.egia.mqi.visit.*;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class MeasureStepperTest {

    @Test
    public void measureIsSteppedThrough() throws IOException, MeasureProcessorException {
        Measure measure = Helpers.getMeasureFromResource("fixtures", "sampleMeasure.json");
        List<String> testMeasureRules = new ArrayList<>();
        measure.getMeasureLogic().getSteps().forEach((step -> testMeasureRules.add(step.getRuleName())));
        PatientData patientData = new PatientData(1L);
        Patient patient = new Patient();
        Date dob = new GregorianCalendar(1986, Calendar.APRIL, 28).getTime();
        patient.setDateOfBirth(dob);
        patientData.addPatientRecord(patient);

        VisitCode visitCode = new VisitCode();
        visitCode.setCodeValue("123");
        visitCode.setCodeSystem(CodeSystem.ICD_10);
        Visit visit = new Visit();
        visit.setVisitCodes(Collections.singletonList(visitCode));
        patientData.addPatientRecord(visit);

        CodeSetGroup codeSetGroupA = CodeSetGroup.builder().groupName("CODE_SET_A").build();
        CodeSet codeSetA = CodeSet.builder().codeSetGroup(codeSetGroupA).codeSystem(CodeSystem.ICD_10).codeValue("123").build();
        MeasureMetaData measureMetaData = new MeasureMetaData(Collections.singletonList(codeSetA));

        MeasureStepper subject = new MeasureStepper(patientData, measure, new MeasureWorkspace(1L, 11L), measureMetaData);
        subject.stepThroughMeasure();

        testMeasureRules.forEach(rule -> assertThat(subject.getMeasureWorkspace().getRuleTrace()).contains(rule));
    }

    @Test
    public void measure_with_infinite_loop_throws_exception() throws IOException {
        Measure measure = Helpers.getMeasureFromResource("fixtures", "measureWithInfiniteLoop.json");
        MeasureStepper subject = new MeasureStepper(
                new PatientData(1L), measure, new MeasureWorkspace(1L, 11L), new MeasureMetaData());

        assertThatExceptionOfType(MeasureProcessorException.class).isThrownBy(subject::stepThroughMeasure);
    }

    @Test
    public void measure_with_invalid_rule_throws_exception() throws IOException {
        Measure measure = Helpers.getMeasureFromResource("fixtures", "measureWithRuleThatDoesNotExist.json");
        MeasureStepper subject = new MeasureStepper(
                new PatientData(1L), measure, new MeasureWorkspace(1L,  11L), new MeasureMetaData());

        assertThatExceptionOfType(MeasureProcessorException.class).isThrownBy(subject::stepThroughMeasure);
    }
}
