package io.egia.mqi.patient;

import io.egia.mqi.visit.CodeSet;
import io.egia.mqi.visit.Visit;

import java.util.ArrayList;
import java.util.List;

public class PatientData {
    private Long patientId;

    private Patient patient;
    private List<Visit> visits = new ArrayList<>();
    private List<CodeSet> codeSets = new ArrayList<>();

    public PatientData(Long patientId) {
        this.patientId = patientId;
    }

    public void addPatientRecord(Patient p) {
        this.patient = p;
    }

    public void addPatientRecord(Visit v) {
        this.visits.add(v);
    }

    public Long getPatientId() {
        return this.patientId;
    }

    public int getVisitCount(){
        return this.visits.size();
    }

    public Patient getPatient() {
        return patient;
    }

    public List<Visit> getVisits() {
        return visits;
    }
}
