package io.egia.mqi.measure;

import java.util.Hashtable;
import java.util.List;

import io.egia.mqi.patient.Patient;
import io.egia.mqi.patient.PatientAbstract;
import io.egia.mqi.visit.Visit;
import io.egia.mqi.patient.PatientData;

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
        appendPatientData(patients);
        appendPatientData(visits);
    }

    private <T extends PatientAbstract> void appendPatientData(List<T> patientRecords) {
        for (T t : patientRecords) {
            PatientData tmp = patientDataHash.get(t.getPatientId());
            if (tmp == null) {
                tmp = new PatientData(t.getPatientId());
            }
            if(Patient.class.isInstance(t)) {
                tmp.addPatientRecord((Patient) t);
            }

            if(Visit.class.isInstance(t)) {
                tmp.addPatientRecord((Visit) t);
            }
            patientDataHash.put(t.getPatientId(), tmp);
        }
    }

    public Hashtable<Long, PatientData> getPatientDataHash() {
        return this.patientDataHash;
    }

    public Integer getPatientCount() {
        return patientDataHash.size();
    }

    public void clear() {
        patientDataHash.clear();
    }
}
