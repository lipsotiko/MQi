package io.egia.mqi.measure;

import io.egia.mqi.patient.Patient;
import io.egia.mqi.patient.PatientData;
import io.egia.mqi.patient.PatientRecordInterface;
import io.egia.mqi.visit.Visit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

@Component
public class MeasureProcessorImpl implements MeasureProcessor {
    private Logger log = LoggerFactory.getLogger(MeasureProcessorImpl.class);
    private Long chunkId;
    private List<Measure> measures;
    private Hashtable<Long, PatientData> patientDataHash = new Hashtable<>();
    private Rules rules;
    private int rulesEvaluatedCount;
    private List<MeasureResults> measureResults = new ArrayList<>();

    @Override
    public void setChunkId(Long chunkId) {
        this.chunkId = chunkId;
    }

    @Override
    public void setMeasures(List<Measure> measures) {
        this.measures = measures;
    }

    @Override
    public void setPatientData(List<Patient> patients, List<Visit> visits) {
        appendToPatientDataHash(patients);
        appendToPatientDataHash(visits);
    }

    @Override
    public void process() {
        this.patientDataHash.forEach((patientId, patientData) ->
                this.measures.forEach((measure) -> {
                    log.info(String.format("Processing chunkId: %s, patient id: %s, measure: %s",
                            this.chunkId,
                            patientId,
                            measure.getMeasureName()));
                    try {
                        evaluatePatientDataByMeasure(patientData, measure, rules);
                    } catch (IOException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | MeasureProcessorException e) {
                        e.printStackTrace();
                    }
                }));
    }

    @Override
    public void clear() {
        this.chunkId = null;
        this.measures.clear();
        this.patientDataHash.clear();
        this.rulesEvaluatedCount = 0;
        this.measureResults.clear();
    }

    @Override
    public void setRules(Rules rules) {
        this.rules = rules;
    }

    private <T extends PatientRecordInterface> void appendToPatientDataHash(List<T> patientRecords) {
        for (T patientRecord : patientRecords) {
            PatientData patientData = this.patientDataHash.get(patientRecord.getPatientId());
            if (patientData == null) {
                patientData = new PatientData(patientRecord.getPatientId());
            }

            patientRecord.updatePatientData(patientData);
            this.patientDataHash.put(patientRecord.getPatientId(), patientData);
        }
    }

    private void evaluatePatientDataByMeasure(PatientData patientData, Measure measure, Rules rules)
            throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, MeasureProcessorException {
        MeasureResults measureResults = new MeasureResults();
        List<Step> steps = measure.getLogic().getSteps();
        int firstStepId = getInitialStepId(steps);
        Step currentStep = getStepById(firstStepId, steps);

        while (measureResults.getContinueProcessing()) {
            String rule = currentStep.getRule();
            log.debug(String.format("Processing rule %s", rule));
            Method ruleMethod = Rules.class.getMethod(rule, PatientData.class, MeasureResults.class);
            measureResults = (MeasureResults) ruleMethod.invoke(rules, patientData, measureResults);

            if (measureResults.getContinueProcessing()) {
                measureResults.writeRuleTrace(rule);
                currentStep = getNextStep(steps, currentStep.getStepId(), currentStep.getSuccess());
            } else {
                currentStep = getNextStep(steps, currentStep.getStepId(), currentStep.getFailure());
            }

            rulesEvaluatedCount++;
        }

        this.measureResults.add(measureResults);
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

    public Hashtable<Long, PatientData> getPatientDataHash() {
        return this.patientDataHash;
    }

    public Long getChunkId() {
        return chunkId;
    }

    public List<Measure> getMeasures() {
        return measures;
    }

    public int getRulesEvaluatedCount() {
        return rulesEvaluatedCount;
    }

    public List<MeasureResults> getMeasureResults() {
        return measureResults;
    }
}
