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
    private MeasureResult measureResult;
    private int rulesEvaluatedCount;

    public MeasureStepper(PatientData patientData, Measure measure, MeasureResult measureResult) {
        this.patientData = patientData;
        this.measure = measure;
        this.measureResult = measureResult;
    }

    public void stepThroughMeasure() throws MeasureProcessorException {
        List<Step> steps = null;

        try {
            steps = measure.getLogic().getSteps();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int firstStepId = getInitialStepId(steps);
        Step currentStep = getStepById(firstStepId, steps);
        Class<?> ruleClass;

        while (measureResult.getContinueProcessing()) {
            String rule = currentStep.getRule();
            Method ruleMethod;
            try {
                assert rule != null;
                log.debug(String.format("Evaluating rule %s", rule));
                ruleClass = Class.forName("io.egia.mqi.rules." + rule);
                ruleMethod = ruleClass.getMethod("evaluate", PatientData.class, MeasureResult.class);
            } catch (NoSuchMethodException | ClassNotFoundException e) {
                throw new MeasureProcessorException(String.format("Could not find method %s", rule), e);
            }

            try {
                assert ruleMethod != null;
                measureResult = (MeasureResult) ruleMethod.invoke(ruleClass.newInstance(), patientData, measureResult);
            } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
                throw new MeasureProcessorException(String.format("Could not invoke method %s",rule), e);
            }

            measureResult.writeRuleTrace(rule);

            if (measureResult.getContinueProcessing()) {
                currentStep = getNextStep(steps, currentStep.getStepId(), currentStep.getSuccessStepId());
            } else {
                currentStep = getNextStep(steps, currentStep.getStepId(), currentStep.getFailureStepId());
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
            throw new MeasureProcessorException("Measure steps configured for infinite loop");
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
        step.setRule("ExitMeasure");
        step.setStepId(99999);
        return step;
    }

    public int getRulesEvaluatedCount() {
        return rulesEvaluatedCount;
    }

    public MeasureResult getMeasureResult() {
        return measureResult;
    }

}
