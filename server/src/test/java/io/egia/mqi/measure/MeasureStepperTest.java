package io.egia.mqi.measure;

import io.egia.mqi.helpers.Helpers;
import io.egia.mqi.patient.Patient;
import io.egia.mqi.patient.PatientData;
import org.junit.Before;
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

        MeasureStepper subject = new MeasureStepper(patientData, measure, new MeasureResult());
        subject.stepThroughMeasure();

        testMeasureRules.forEach(rule -> assertThat(subject.getMeasureResult().getRuleTrace()).contains(rule));
    }

    @Test
    public void measureWithInfiniteLoopThrowsException() throws IOException, MeasureProcessorException {
        Measure measure = Helpers.getMeasureFromResource("fixtures", "measureWithInfiniteLoop.json");
        MeasureStepper subject = new MeasureStepper(
                new PatientData(1L), measure, new MeasureResult());

        assertThatExceptionOfType(MeasureProcessorException.class).isThrownBy(subject::stepThroughMeasure);
    }

    @Test
    public void measureWithInvalidRuleThrowsException() throws IOException, MeasureProcessorException {
        Measure measure = Helpers.getMeasureFromResource("fixtures", "measureWithRuleThatDoesNotExist.json");
        MeasureStepper subject = new MeasureStepper(
                new PatientData(1L), measure, new MeasureResult());

        assertThatExceptionOfType(MeasureProcessorException.class).isThrownBy(subject::stepThroughMeasure);
    }
}
