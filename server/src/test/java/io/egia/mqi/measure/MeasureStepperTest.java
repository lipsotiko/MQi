package io.egia.mqi.measure;

import io.egia.mqi.helpers.Helpers;
import io.egia.mqi.patient.PatientData;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class MeasureStepperTest {

    @Test
    public void measureIsSteppedThrough() throws MeasureProcessorException, IOException {
        Measure measure = Helpers.getMeasureFromResource("fixtures", "sampleMeasure.json");
        List<String> testMeasureRules = new ArrayList<>();
        measure.getLogic().getSteps().stream().forEach((step -> testMeasureRules.add(step.getRule())));
        MeasureStepper subject = new MeasureStepper(
                new PatientData(1L), measure, new MeasureResult());
        subject.stepThroughMeasure();

        testMeasureRules.stream().forEach(rule -> {
            assertThat(testMeasureRules).contains(rule);
        });
    }

    @Test
    public void measureWithInfiniteLoopThrowsException() throws IOException {
        Measure measure = Helpers.getMeasureFromResource("fixtures", "measureWithInfiniteLoop.json");
        MeasureStepper subject = new MeasureStepper(
                new PatientData(1L), measure, new MeasureResult());

        assertThatExceptionOfType(MeasureProcessorException.class).isThrownBy(() -> {
                    subject.stepThroughMeasure();
                }
        );
    }

    @Test
    public void measureWithInvalidRuleThrowsException() throws IOException {
        Measure measure = Helpers.getMeasureFromResource("fixtures", "measureWithRuleThatDoesNotExist.json");
        MeasureStepper subject = new MeasureStepper(
                new PatientData(1L), measure, new MeasureResult());

        assertThatExceptionOfType(MeasureProcessorException.class).isThrownBy(() -> {
                    subject.stepThroughMeasure();
                }
        );
    }
}
