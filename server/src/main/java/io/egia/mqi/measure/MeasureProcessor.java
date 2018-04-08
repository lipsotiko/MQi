package io.egia.mqi.measure;

import io.egia.mqi.patient.Patient;
import io.egia.mqi.patient.PatientData;
import io.egia.mqi.patient.PatientRecordInterface;
import io.egia.mqi.visit.Visit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Hashtable;
import java.util.List;

/**
 * 
 * @author vango
 *
 * The purpose of this class is to make all patient data within
 * a chunk available to rules for the purposes of applying measure
 * logic and storing measure results.
 */

public class MeasureProcessor {
    private Logger log = LoggerFactory.getLogger(MeasureProcessor.class);
    private Long chunkId;
    private List<Measure> measures;
    private Hashtable<Long, PatientData> patientDataHash = new Hashtable<>();

    public MeasureProcessor(Long chunkId, List<Measure> measures, List<Patient> patients, List<Visit> visits) {
        this.chunkId = chunkId;
        this.measures = measures;
        appendToPatientDataHash(patients);
        appendToPatientDataHash(visits);
    }

    public void process() {
        measures.forEach((m)-> log.info(String.format("Processing measure: %s, with chunk id: %s", m.getFileName(), chunkId)));
    }

    private <T extends PatientRecordInterface> void appendToPatientDataHash(List<T> patientRecords) {
        for (T t : patientRecords) {
            PatientData patientData = patientDataHash.get(t.getPatientId());
            if (patientData == null) {
                patientData = new PatientData(t.getPatientId());
            }

            t.updatePatientData(patientData);
            patientDataHash.put(t.getPatientId(), patientData);
        }
    }

    public Hashtable<Long, PatientData> getPatientDataHash() {
        return this.patientDataHash;
    }

    public void clear() {
        chunkId = null;
        measures.clear();
        patientDataHash.clear();
    }
}
