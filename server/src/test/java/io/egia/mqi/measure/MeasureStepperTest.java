package io.egia.mqi.measure;

import io.egia.mqi.patient.PatientData;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.egia.mqi.helpers.Helpers.*;
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

        PatientData patientData = patientData(PATIENT_ID, "sampleMeasure.json");
        MeasureMetaData measureMetaData = measureMetaData("sampleMeasure.json");
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
