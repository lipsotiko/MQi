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

    private List<String> testMeasureRules = new ArrayList<>();

    @Test
    public void measureIsSteppedThrough() throws MeasureProcessorException, IOException {
        Measure measure = Helpers.getMeasureFromResource("fixtures", "sampleMeasure.json");
        measure.getLogic().getSteps().stream().forEach((step -> testMeasureRules.add(step.getRule())));
        MeasureStepper measureStepper = new MeasureStepper(
                new PatientData(1L), measure, new MeasureResult());
        measureStepper.stepThroughMeasure();
        List<String> ruleTrace = measureStepper.getMeasureResult().getRuleTrace();
        for (String rule : testMeasureRules) {
            assertThat(ruleTrace.contains(rule)).isTrue();
        }
    }

    @Test
    public void measureWithInfiniteLoopThrowsException() throws IOException, MeasureProcessorException {
        Measure measure = Helpers.getMeasureFromResource("fixtures", "measureWithInfiniteLoop.json");
        MeasureStepper measureStepper = new MeasureStepper(
                new PatientData(1L), measure, new MeasureResult());

        assertThatExceptionOfType(MeasureProcessorException.class).isThrownBy(() -> {
                    measureStepper.stepThroughMeasure();
                }
        );
    }

    @Test
    public void measureWithInvalidRuleThrowsException() throws IOException, MeasureProcessorException {
        Measure measure = Helpers.getMeasureFromResource("fixtures", "measureWithRuleThatDoesNotExist.json");
        MeasureStepper measureStepper = new MeasureStepper(
                new PatientData(1L), measure, new MeasureResult());
        assertThatExceptionOfType(MeasureProcessorException.class).isThrownBy(() -> {
                    measureStepper.stepThroughMeasure();
                }
        );
    }
}
