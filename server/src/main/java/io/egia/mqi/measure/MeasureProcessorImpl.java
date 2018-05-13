package io.egia.mqi.measure;

import io.egia.mqi.patient.Patient;
import io.egia.mqi.patient.PatientData;
import io.egia.mqi.patient.PatientRecordInterface;
import io.egia.mqi.visit.Visit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

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
                    evaluatePatientDataByMeasure(patientData, measure, rules);
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

    private void evaluatePatientDataByMeasure(PatientData patientData, Measure measure, Rules rules) {
        MeasureResults measureResults = new MeasureResults();
        MeasureStepper measureStepper = new MeasureStepper(patientData, measure, rules, measureResults);
        measureStepper.stepThroughMeasure();
        rulesEvaluatedCount = rulesEvaluatedCount + measureStepper.getRulesEvaluatedCount();
        this.measureResults.add(measureStepper.getMeasureResults());
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
