package io.egia.mqi.measure;

import io.egia.mqi.patient.Patient;
import io.egia.mqi.patient.PatientData;
import io.egia.mqi.visit.*;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static io.egia.mqi.helpers.Helpers.getMeasureFromResource;
import static io.egia.mqi.visit.CodeSystem.ICD_10;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class MeasureStepperTest {

    private static final long PATIENT_ID = 1L;
    private static final long MEASURE_ID = 11L;
    private static final int RULES_FIRED = 4;

    @Test
    public void measure_is_stepped_through() throws IOException, MeasureProcessorException {
        Measure measure = getMeasureFromResource("fixtures", "sampleMeasure.json");
        List<String> testMeasureRules = new ArrayList<>();
        measure.getMeasureLogic().getSteps().forEach((step -> testMeasureRules.add(step.getRuleName())));

        PatientData patientData = new PatientData(PATIENT_ID);
        Patient patient = new Patient();
        Date dob = new GregorianCalendar(1986, Calendar.APRIL, 28).getTime();
        patient.setDateOfBirth(dob);
        patientData.addPatientRecord(patient);

        VisitCode visitCode = new VisitCode();
        visitCode.setCodeValue("123");
        visitCode.setCodeSystem(ICD_10);
        Visit visit = new Visit();
        visit.setVisitCodes(Collections.singletonList(visitCode));
        patientData.addPatientRecord(visit);

        CodeSetGroup codeSetGroupA = CodeSetGroup.builder().groupName("CODE_SET_A").build();
        CodeSet codeSetA = CodeSet.builder().codeSetGroup(codeSetGroupA).codeSystem(ICD_10).codeValue("123").build();
        MeasureMetaData measureMetaData = new MeasureMetaData(Collections.singletonList(codeSetA));

        MeasureWorkspace measureWorkspace = new MeasureWorkspace(PATIENT_ID, MEASURE_ID);
        MeasureStepper subject = new MeasureStepper(patientData, measure, measureWorkspace, measureMetaData);
        
        assertThat(subject.stepThroughMeasure()).isEqualTo(RULES_FIRED);
        assertThat(subject.getMeasureWorkspace().getRuleTrace().size()).isEqualTo(RULES_FIRED);
        testMeasureRules.forEach(rule -> assertThat(subject.getMeasureWorkspace().getRuleTrace()).contains(rule));
    }

    @Test
    public void measure_with_infinite_loop_throws_exception() throws IOException {
        Measure measure = getMeasureFromResource("fixtures", "measureWithInfiniteLoop.json");
        MeasureWorkspace measureWorkspace = new MeasureWorkspace(PATIENT_ID, MEASURE_ID);
        MeasureStepper subject =
                new MeasureStepper(new PatientData(PATIENT_ID), measure, measureWorkspace, new MeasureMetaData());

        assertThatExceptionOfType(MeasureProcessorException.class).isThrownBy(subject::stepThroughMeasure);
    }

    @Test
    public void measure_with_invalid_rule_throws_exception() throws IOException {
        Measure measure = getMeasureFromResource("fixtures", "measureWithRuleThatDoesNotExist.json");
        MeasureWorkspace measureWorkspace = new MeasureWorkspace(PATIENT_ID, MEASURE_ID);
        MeasureStepper subject =
                new MeasureStepper(new PatientData(PATIENT_ID), measure, measureWorkspace, new MeasureMetaData());

        assertThatExceptionOfType(MeasureProcessorException.class).isThrownBy(subject::stepThroughMeasure);
    }

    @Test
    public void measure_with_no_steps_throws_exception() {
        Measure measure = new Measure();
        MeasureLogic measureLogic = new MeasureLogic();
        measureLogic.setSteps(Collections.emptyList());
        measure.setMeasureLogic(measureLogic);
        MeasureWorkspace measureWorkspace = new MeasureWorkspace(PATIENT_ID, MEASURE_ID);
        MeasureStepper subject =
                new MeasureStepper(new PatientData(PATIENT_ID), measure, measureWorkspace, new MeasureMetaData());

        assertThatExceptionOfType(MeasureProcessorException.class).isThrownBy(subject::stepThroughMeasure);
    }
}
