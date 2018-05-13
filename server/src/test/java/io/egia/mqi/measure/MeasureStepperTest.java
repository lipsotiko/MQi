package io.egia.mqi.measure;

import io.egia.mqi.helpers.Helpers;
import io.egia.mqi.patient.PatientData;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MeasureStepperTest {

    private List<String> testMeasureRules = new ArrayList<>();

    @Test
    public void measureIsSteppedThrough() throws MeasureProcessorException, IOException {
        Measure measure = Helpers.getMeasureFromResource("fixtures","sampleMeasure.json");
        measure.getLogic().getSteps().stream().forEach((step -> testMeasureRules.add(step.getRule())));
        MeasureStepper measureStepper = new MeasureStepper(
                new PatientData(1L), measure, new Rules(), new MeasureResult());
        measureStepper.stepThroughMeasure();
        List<String> ruleTrace = measureStepper.getMeasureResult().getRuleTrace();
        for(String rule: testMeasureRules) {
            assertThat(ruleTrace.contains(rule)).isTrue();
        }
    }

    @Test
    public void measureWithInfiniteLoopThrowsException() throws IOException, MeasureProcessorException {
        Measure measure = Helpers.getMeasureFromResource("fixtures","measureWithInfiniteLoop.json");
        MeasureStepper measureStepper = new MeasureStepper(
                new PatientData(1L), measure, new Rules(), new MeasureResult());
        assertMeasureProcessorException(measureStepper, "Measure steps configured for infinite loop");
    }

    @Test
    public void measureWithInvalidRuleThrowsException() throws IOException, MeasureProcessorException {
        Measure measure = Helpers.getMeasureFromResource("fixtures","measureWithRuleThatDoesNotExist.json");
        MeasureStepper measureStepper = new MeasureStepper(
                new PatientData(1L), measure, new Rules(), new MeasureResult());
        assertMeasureProcessorException(measureStepper, "Could not find method thisRuleDoesntExistInMqi");
    }

    private void assertMeasureProcessorException(MeasureStepper measureStepper, String s) {
        boolean exceptionWasCaught = false;
        try {
            measureStepper.stepThroughMeasure();
        } catch (MeasureProcessorException e) {
            exceptionWasCaught = true;
            assertThat(e.getMessage()).contains(s);
        }

        assertThat(exceptionWasCaught).isTrue();
    }
}