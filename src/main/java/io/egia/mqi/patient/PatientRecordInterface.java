package io.egia.mqi.patient;

public interface PatientRecordInterface {
    Long getPatientId();

    void updatePatientData(PatientData patientData);
}
