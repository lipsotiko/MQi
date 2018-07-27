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
    private List<Measure> measures;
    private Hashtable<Long, PatientData> patientDataHash = new Hashtable<>();
    private int rulesEvaluatedCount;
    private List<MeasureResult> measureResults = new ArrayList<>();

    @Override
    public void initProcessor(List<Measure> measures, List<Patient> patients, List<Visit> visits) {
        this.measures = measures;
        appendToPatientDataHash(patients);
        appendToPatientDataHash(visits);
    }

    @Override
    public void process() {
        this.patientDataHash.forEach((patientId, patientData) ->
                this.measures.forEach((measure) -> {
                    log.debug(String.format("Processing patient id: %s, measure: %s",
                            patientId,
                            measure.getMeasureName()));
                    try {
                        evaluatePatientDataByMeasure(patientData, measure);
                    } catch (MeasureProcessorException e) {
                        e.printStackTrace();
                    }
                }));
    }

    @Override
    public void clear() {
        this.measures.clear();
        this.patientDataHash.clear();
        this.rulesEvaluatedCount = 0;
        this.measureResults.clear();
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

    private void evaluatePatientDataByMeasure(PatientData patientData, Measure measure)
            throws MeasureProcessorException {
        MeasureStepper measureStepper = new MeasureStepper(patientData, measure, new MeasureResult());
        measureStepper.stepThroughMeasure();
        rulesEvaluatedCount = rulesEvaluatedCount + measureStepper.getRulesEvaluatedCount();
        this.measureResults.add(measureStepper.getMeasureResult());
    }


    public Hashtable<Long, PatientData> getPatientDataHash() {
        return this.patientDataHash;
    }

    public List<Measure> getMeasures() {
        return measures;
    }

    public int getRulesEvaluatedCount() {
        return rulesEvaluatedCount;
    }

    public List<MeasureResult> getMeasureResults() {
        return measureResults;
    }
}
