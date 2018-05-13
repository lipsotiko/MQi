package io.egia.mqi.measure;

import io.egia.mqi.patient.PatientData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class MeasureStepper {

    private Logger log = LoggerFactory.getLogger(MeasureStepper.class);

    private final PatientData patientData;
    private final Measure measure;
    private final Rules rules;
    private MeasureResults measureResults;
    private int rulesEvaluatedCount;

    public MeasureStepper(PatientData patientData, Measure measure, Rules rules, MeasureResults measureResults) {
        this.patientData = patientData;
        this.measure = measure;
        this.rules = rules;
        this.measureResults = measureResults;
    }

    public void stepThroughMeasure() {
        List<Step> steps = null;

        try {
            steps = measure.getLogic().getSteps();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int firstStepId = getInitialStepId(steps);
        Step currentStep = getStepById(firstStepId, steps);

        while (measureResults.getContinueProcessing()) {
            String rule = currentStep.getRule();
            log.debug(String.format("Processing rule %s", rule));
            Method ruleMethod = null;
            try {
                ruleMethod = Rules.class.getMethod(rule, PatientData.class, MeasureResults.class);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            try {
                assert ruleMethod != null;
                measureResults = (MeasureResults) ruleMethod.invoke(rules, patientData, measureResults);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }

            if (measureResults.getContinueProcessing()) {
                measureResults.writeRuleTrace(rule);
                try {
                    currentStep = getNextStep(steps, currentStep.getStepId(), currentStep.getSuccess());
                } catch (MeasureProcessorException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    currentStep = getNextStep(steps, currentStep.getStepId(), currentStep.getFailure());
                } catch (MeasureProcessorException e) {
                    e.printStackTrace();
                }
            }

            rulesEvaluatedCount++;
        }

    }

    private Step getNextStep(List<Step> steps, int currentStepId, int nextStepId) throws MeasureProcessorException {
        preventInfiniteLoops(currentStepId, nextStepId);
        return getStepById(nextStepId, steps);
    }

    private void preventInfiniteLoops(int currentStepId, int nextStepId) throws MeasureProcessorException {
        if ((nextStepId <= currentStepId) && (currentStepId != 99999)) {
            throw new MeasureProcessorException("Error: Measure steps configured for infinite loop");
        }
    }

    private int getInitialStepId(List<Step> steps) {
        int lowestStepId = steps.get(0).getStepId();
        for (Step step : steps) {
            if (step.getStepId() < lowestStepId) {
                lowestStepId = step.getStepId();
            }
        }
        return lowestStepId;
    }

    private Step getStepById(int stepId, List<Step> steps) {
        for (Step step : steps) {
            if (step.getStepId() == stepId) {
                return step;
            }
        }
        return stepToExitMeasure();
    }

    private Step stepToExitMeasure() {
        Step step = new Step();
        step.setRule("exitMeasure");
        step.setStepId(99999);
        return step;
    }

    public int getRulesEvaluatedCount() {
        return rulesEvaluatedCount;
    }

    public MeasureResults getMeasureResults() {
        return measureResults;
    }

}
