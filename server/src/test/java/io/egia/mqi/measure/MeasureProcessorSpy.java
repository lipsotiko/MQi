package io.egia.mqi.measure;

import io.egia.mqi.patient.Patient;
import io.egia.mqi.visit.Visit;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class MeasureProcessorSpy implements MeasureProcessor {

    List<Measure> setMeasuresWasCalledWith = new ArrayList<>();
    boolean processWasCalled = false;
    boolean clearWasCalled = false;
    List<Patient> setPatientDataWasCalledWithPatients = new ArrayList<>();
    List<Visit> setPatientDataWasCalledWithVisits = new ArrayList<>();

    @Override
    public void process(List<Measure> measures, List<Patient> patients, List<Visit> visits, ZonedDateTime timeExecuted) {

        processWasCalled = true;

        if(measures != null) {
            setMeasuresWasCalledWith = measures;
        }

        if (patients != null){
            setPatientDataWasCalledWithPatients.addAll(patients);
        }

        if (visits != null) {
            setPatientDataWasCalledWithVisits.addAll(visits);
        }
    }

    @Override
    public void clear() {
        clearWasCalled = true;
    }

}
