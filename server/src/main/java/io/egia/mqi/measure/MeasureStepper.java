package io.egia.mqi.measure;

import io.egia.mqi.patient.PatientData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

class MeasureStepper {

    private Logger log = LoggerFactory.getLogger(MeasureStepper.class);

    private final PatientData patientData;
    private List<Step> steps;
    private MeasureResult measureResult;
    private MeasureMetaData measureMetaData;
    private int rulesEvaluatedCount;

    MeasureStepper(PatientData patientData,
                   Measure measure,
                   MeasureResult measureResult,
                   MeasureMetaData measureMetaData) {
        this.patientData = patientData;
        this.steps = measure.getMeasureLogic().getSteps();
        this.measureResult = measureResult;
        this.measureMetaData = measureMetaData;
    }

    void stepThroughMeasure() throws MeasureProcessorException {
        int firstStepId = getInitialStepId();
        Step currentStep = getStepById(firstStepId);
        Class<?> ruleClass;

        while (measureResult.getContinueProcessing()) {
            String rule = currentStep.getRuleName();
            List<RuleParam> parameters = currentStep.getParameters();
            log.debug(String.format("Evaluating rule %s", rule));

            Method ruleMethod;
            try {
                ruleClass = Class.forName("io.egia.mqi.measure.rules." + rule);
                ruleMethod = ruleClass.getMethod("evaluate", PatientData.class, List.class, MeasureMetaData.class, MeasureResult.class);
            } catch (NoSuchMethodException | ClassNotFoundException e) {
                throw new MeasureProcessorException(String.format("Could not find method %s", rule), e);
            }

            try {
                measureResult = (MeasureResult) ruleMethod.invoke(ruleClass.newInstance(), patientData, parameters, measureMetaData, measureResult);
            } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
                throw new MeasureProcessorException(String.format("Could not invoke method %s",rule), e);
            }

            measureResult.writeRuleTrace(rule);
            if (measureResult.getContinueProcessing()) {
                currentStep = getNextStep(currentStep.getStepId(), currentStep.getSuccessStepId());
            } else {
                currentStep = getNextStep(currentStep.getStepId(), currentStep.getFailureStepId());
            }

            rulesEvaluatedCount++;
        }
    }

    private Step getNextStep(int currentStepId, int nextStepId) throws MeasureProcessorException {
        preventInfiniteLoops(currentStepId, nextStepId);
        return getStepById(nextStepId);
    }

    private void preventInfiniteLoops(int currentStepId, int nextStepId) throws MeasureProcessorException {
        if ((nextStepId <= currentStepId) && (currentStepId != 99999)) {
            throw new MeasureProcessorException("Measure steps configured for infinite loop");
        }
    }

    private int getInitialStepId() {
        int lowestStepId = steps.get(0).getStepId();
        for (Step step : steps) {
            if (step.getStepId() < lowestStepId) {
                lowestStepId = step.getStepId();
            }
        }
        return lowestStepId;
    }

    private Step getStepById(int stepId) {
        for (Step step : steps) {
            if (step.getStepId() == stepId) {
                return step;
            }
        }
        return stepToExitMeasure();
    }

    private Step stepToExitMeasure() {
        Step step = new Step();
        step.setRuleName("ExitMeasure");
        step.setStepId(99999);
        return step;
    }

    int getRulesEvaluatedCount() {
        return rulesEvaluatedCount;
    }

    MeasureResult getMeasureResult() {
        return measureResult;
    }

}
