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
                    log.info(String.format("Processing chunkId: %s, patient id: %s, measure: %s", this.chunkId, patientId, measure.getMeasureName()));
                    try {
                        evaluatePatientDataForMeasure(patientData, measure);
                    } catch (IOException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }));
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

    private void evaluatePatientDataForMeasure(PatientData patientData, Measure measure) throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Rules rules = new Rules();
        MeasureResults measureResults = new MeasureResults();
        List<Step> steps = measure.getLogic().getSteps();
        int firstStepId = getFirstStepId(steps);
        Step currentStep = getStepByStepId(firstStepId, steps);

        while (measureResults.getContinueProcessing()) {
            String rule = currentStep.getRule();
            log.debug(String.format("Processing rule %s", rule));
            Method ruleMethod = Rules.class.getMethod(rule, PatientData.class, MeasureResults.class);
            measureResults = (MeasureResults) ruleMethod.invoke(rules, patientData, measureResults);

            if (measureResults.getContinueProcessing()) {
                currentStep = getStepByStepId(currentStep.getSuccess(), steps);
            }

            rulesEvaluatedCount++;
        }

        this.measureResults.add(measureResults);
    }

    private int getFirstStepId(List<Step> steps) {
        int lowestStepId = steps.get(0).getStepId();
        for (Step step : steps) {
            if (step.getStepId() < lowestStepId) {
                lowestStepId = step.getStepId();
            }
        }
        return lowestStepId;
    }

    private Step getStepByStepId(int stepId, List<Step> steps) {
        for (Step step : steps) {
            if (step.getStepId() == stepId) {
                return step;
            }
        }
        return new Step();
    }

    @Override
    public void clear() {
        this.chunkId = null;
        this.measures.clear();
        this.patientDataHash.clear();
        this.rulesEvaluatedCount = 0;
        this.measureResults.clear();
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
