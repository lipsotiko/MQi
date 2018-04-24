package io.egia.mqi.patient;

import io.egia.mqi.visit.Visit;

import java.util.ArrayList;
import java.util.List;

public class PatientData {
    private Long patientId;

    private List<Patient> patients = new ArrayList<>();
    private List<Visit> visits = new ArrayList<>();

    public PatientData(Long patientId) {
        this.patientId = patientId;
    }

    public void addPatientRecord(Patient p) {
        this.patients.add(p);
    }

    public void addPatientRecord(Visit v) {
        this.visits.add(v);
    }

    public Long getPatientId() {
        return this.patientId;
    }

    public List<Visit> getVisits() {
        return this.visits;
    }

    public int getPatientCount(){
        return this.patients.size();
    }

    public int getVisitCount(){
        return this.visits.size();
    }
}
