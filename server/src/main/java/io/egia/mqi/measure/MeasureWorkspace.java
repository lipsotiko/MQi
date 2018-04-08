package io.egia.mqi.measure;

import io.egia.mqi.patient.Patient;
import io.egia.mqi.patient.PatientData;
import io.egia.mqi.patient.PatientRecordInterface;
import io.egia.mqi.visit.Visit;

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

public class MeasureWorkspace {
    private Hashtable<Long, PatientData> patientDataHash = new Hashtable<>();

    public MeasureWorkspace(List<Patient> patients, List<Visit> visits) {
        appendToPatientDataHash(patients);
        appendToPatientDataHash(visits);
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
        patientDataHash.clear();
    }
}
