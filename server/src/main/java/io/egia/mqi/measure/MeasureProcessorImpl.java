package io.egia.mqi.measure;

import io.egia.mqi.patient.Patient;
import io.egia.mqi.patient.PatientData;
import io.egia.mqi.patient.PatientRecordInterface;
import io.egia.mqi.visit.Visit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;

@Component
public class MeasureProcessorImpl implements MeasureProcessor {
    private Logger log = LoggerFactory.getLogger(MeasureProcessorImpl.class);
    private Long chunkId;
    private List<Measure> measures;
    private Hashtable<Long, PatientData> patientDataHash = new Hashtable<>();

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
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }));
    }

    public <T extends PatientRecordInterface> void appendToPatientDataHash(List<T> patientRecords) {
        for (T patientRecord : patientRecords) {
            PatientData patientData = this.patientDataHash.get(patientRecord.getPatientId());
            if (patientData == null) {
                patientData = new PatientData(patientRecord.getPatientId());
            }

            patientRecord.updatePatientData(patientData);
            this.patientDataHash.put(patientRecord.getPatientId(), patientData);
        }
    }

    public Hashtable<Long, PatientData> getPatientDataHash() {
        return this.patientDataHash;
    }

    @Override
    public void clear() {
        this.chunkId = null;
        this.measures.clear();
        this.patientDataHash.clear();
    }

    private void evaluatePatientDataForMeasure(PatientData patientData, Measure measure) throws IOException {
        System.out.println(measure.getLogic().getDescription());
    }

}
