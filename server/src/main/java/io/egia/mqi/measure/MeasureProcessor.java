package io.egia.mqi.measure;

import io.egia.mqi.patient.*;
import io.egia.mqi.visit.Visit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

@Component
public class MeasureProcessor implements Processor {
    private Logger log = LoggerFactory.getLogger(MeasureProcessor.class);
    private List<Measure> measures;
    private Hashtable<Long, PatientData> patientDataHash = new Hashtable<>();
    private int rulesEvaluatedCount;
    private List<MeasureWorkspace> measureWorkspaces = new ArrayList<>();

    MeasureProcessor() {
    }

    @Override
    public void process(List<Measure> measures,
                        List<Patient> patients,
                        List<Visit> visits,
                        MeasureMetaData measureMetaData,
                        ZonedDateTime timeExecuted) {
        this.measures = measures;
        appendToPatientDataHash(patients);
        appendToPatientDataHash(visits);
        this.patientDataHash.forEach((patientId, patientData) ->
                this.measures.forEach((measure) -> {
                    log.debug(String.format("Processing patient id: %s, measure: %s",
                            patientId,
                            measure.getMeasureName()));
                    try {
                        evaluatePatientDataByMeasure(patientData, measure, measureMetaData);
                    } catch (MeasureProcessorException e) {
                        e.printStackTrace();
                    }
                }));
    }

    @Override
    public void clear() {
        this.patientDataHash.clear();
        this.rulesEvaluatedCount = 0;
        this.measureWorkspaces.clear();
    }

    @Override
    public List<PatientMeasureLog> getLog() {
        List<PatientMeasureLog> log = new ArrayList<>();
        measures.forEach(m ->
                patientDataHash.forEach((patientId, data) ->
                        log.add(PatientMeasureLog.builder()
                                .measureId(m.getMeasureId())
                                .patientId(patientId)
                                .lastUpdated(ZonedDateTime.now())
                                .build())
                )
        );

        return log;
    }

    @Override
    public List<MeasureResult> getResults() {
        List<MeasureResult> results = new ArrayList<>();
        measureWorkspaces.stream()
                .filter(mw -> mw.getResultCode() != null)
                .forEach(mw -> results.add(MeasureResult.builder()
                        .patientId(mw.getPatientId())
                        .measureId(mw.getMeasureId())
                        .resultCode(mw.getResultCode())
                        .build())
                );

        return results;
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

    private void evaluatePatientDataByMeasure(PatientData patientData, Measure measure, MeasureMetaData measureMetaData)
            throws MeasureProcessorException {
        MeasureWorkspace measureWorkspace = new MeasureWorkspace(patientData.getPatientId(), measure.getMeasureId());
        MeasureStepper measureStepper = new MeasureStepper(patientData, measure, measureWorkspace, measureMetaData);
        measureStepper.stepThroughMeasure();
        rulesEvaluatedCount = rulesEvaluatedCount + measureStepper.getRulesEvaluatedCount();
        this.measureWorkspaces.add(measureStepper.getMeasureWorkspace());
    }

    Hashtable<Long, PatientData> getPatientDataHash() {
        return this.patientDataHash;
    }

    int getRulesEvaluatedCount() {
        return rulesEvaluatedCount;
    }

}
